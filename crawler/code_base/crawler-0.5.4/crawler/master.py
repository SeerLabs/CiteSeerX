#!/usr/bin/env python2.4
import sys
import os
import time
import datetime
import socket
import pickle
import threading
import logging
import logging.handlers
import urlparse

import stomp

import config
import messaging
import fetching
import filtering
import submit
import bots
import html_helper
import resource
from exception import BadResourceError
    
class Master(object):
    """Master class
    
    Attributes:
        conf
        seed_hosts
        seed_line_parser
        writer        
        due
        frontier_sender
        spiders
        amq_handler
        bots_checker
        before_enqueue_filter
        before_download_filter
        before_save_filter
        before_parse_filter
        frontier_sender_conn
        fetched_count
        pre_filtered_count
        post_filtered_count
        failed_count
        enqueue_count
        dequeue_count                
        seed_submitter
        is_active
        start_time
        stat_locker
    """
    def __init__(self):        
        self.conf = config.Config() # create a default config
        self.seed_hosts = {}
        self.spiders = []
        self.fetched_count = 0
        self.pre_filtered_count = 0
        self.post_filtered_count = 0
        self.failed_count = 0
        self.enqueue_count = 0
        self.dequeue_count = 0          
        self.is_active = True     
        self.start_time = datetime.datetime.utcnow()
        self.stat_lock = threading.Lock() 
                
    def get_seed_host_info(self, r):
        """save host and substring info for seeds"""
        if r.host in self.seed_hosts:
            substrings = self.seed_hosts[r.host]
            # add local path to substrings if it does not exist
            already_have = False
            for substr in substrings:
                if substr == r.path:
                    already_have = True
                    break
            
            if not already_have:                 
                substrings.append(r.path)
        else:
            substrings = []
            substrings.append(r.path)
            self.seed_hosts[r.host] = substrings 
        
    def load_seeds(self, seed_file, seed_url):        
        if seed_file != None and os.path.isfile(seed_file):
            # load from file
            self.seed_submitter.submit_seeds_from_file(
                seed_file,
                self.seed_line_parser,
                self.conf.ignore_invalid_seeds)                              
        elif seed_url != None and (seed_url.startswith('http://')
                or seed_url.startswith('https://')):
            # load one seed
            self.seed_submitter.submit(seed_url)  
                                   
        return True         
    
    def start(self, config_file, seed_file, seed_url):
        # load config
        if config_file != None:
            print "loading config file..."
            suc = self.conf.load(config_file)            
        else:
            print "use default config"
            suc = True
    
        if not suc: return

        socket.setdefaulttimeout(self.conf.spider_timeout)        
                
        # create dirs
        if not os.path.exists(self.conf.log_dir):
            os.makedirs(self.conf.log_dir)
        
        # config loggers
        self.config_loggers()        
        
        # create seed line parser
        self.seed_line_parser = self.create_instance(self.conf.seed_line_parser_class)
        if self.seed_line_parser is None: return False
                    
        # create writer
        self.writer = self.create_instance(self.conf.writer)
        if self.writer is None: return False
                
        # create before_enqueue_filter
        print "create filters..."
        self.before_enqueue_filter = filtering.Filter(
            'before_enqueue',
            self.conf.before_enqueue_filter_rules,
            self)
        
        self.before_download_filter = filtering.Filter(
            'before_download',
            self.conf.before_download_filter_rules,
            self)
        
        self.before_save_filter = filtering.Filter(
            'before_save',
            self.conf.before_save_filter_rules,
            self) 
                         
        self.before_parse_filter = filtering.Filter(
            'before_parse',
            self.conf.before_parse_filter_rules,
            self)
            
        # create DUE
        self.due = self.create_instance(self.conf.due)        
        if self.due is None: return False
        
        # create bots checker
        self.bots_checker = bots.RobotsChecker(self.conf.user_agent, self.conf.bots_dir, self)        

        # create frontier sender
        self.frontier_sender = FrontierSender(self)
        
        # construct pipeline
        self.due.receiver = self.bots_checker
        self.bots_checker.receiver = self.frontier_sender
        
        # connect frontier and message queue    
        print "connecting frontier and MQ..."
        suc = self.connect_activemqs()
        if not suc: return
        
        # create handlers
        print "create handlers..."
        suc = self.create_handlers()
        if not suc: return                

        # create spiders
        print "create spiders..."
        suc = self.create_spiders()
        if not suc: return
              
        print("crawler started.")
        
        # creat submitter
        self.seed_submitter = submit.Submitter(
            self.conf.mq_host,
            self.conf.mq_port,
            self.conf.mq_dest)
        self.seed_submitter.connect_mq()
        
        # load seed if any             
        print "loading seeds..."
        suc = self.load_seeds(seed_file, seed_url)
        if not suc: return              
                
        try:
            while True:
                time.sleep(1)
                self.report()
                    
        except KeyboardInterrupt:            
            self.disconnect_activemqs()            
            for s in self.spiders:
                s.disconnect_frontier()
            self.amq_handler.disconnect_mq()
            self.seed_submitter.disconnect_mq()            
            
            self.is_active = False            
                        
            print("crawler stopped.")
    
    def config_loggers(self):
        rotating_interval = 'D'
               
        # root logger
        logger = logging.getLogger('')
        logger.setLevel(logging.WARNING)        
        log_file = os.path.join(self.conf.log_dir, 'root.log')
        h = logging.FileHandler(log_file)
        formatter = logging.Formatter("%(asctime)s - %(levelname)s - %(message)s")
        h.setFormatter(formatter)        
        logger.addHandler(h)                
        
        for name in ['resource.fetched', 'resource.filtered', 'resource.failed']:
            logger = logging.getLogger(name)
            logger.setLevel(logging.INFO)
            logger.propagate = False
            log_file = os.path.join(self.conf.log_dir, name + '.log')
            h = logging.handlers.TimedRotatingFileHandler(log_file, rotating_interval)
            formatter = logging.Formatter("%(asctime)s - %(message)s")
            h.setFormatter(formatter)
            logger.addHandler(h)

        if self.conf.fetching_on_open_logging:
            # feching.on_open logging
            logger = logging.getLogger('fetching.on_open')
            logger.setLevel(logging.INFO)
            logger.propagate = False
            log_file = os.path.join(self.conf.log_dir, 'fetching.on_open.log')
            h = logging.FileHandler(log_file)
            formatter = logging.Formatter("%(asctime)s\t%(message)s")
            h.setFormatter(formatter)
            logger.addHandler(h) 
        
        # logging for stomppy
        logger = logging.getLogger('stomp.py')
        logger.setLevel(logging.WARNING)
        logger.propagate = False
        log_file = os.path.join(self.conf.log_dir, 'stomp.log')
        h = logging.FileHandler(log_file)
        logger.addHandler(h)        
                            

    def create_spiders(self):
        for i in range(self.conf.spider_number):
            s = fetching.Spider(str(i), self)
            self.spiders.append(s)
            s.connect_frontier(self.conf.frontier_host, self.conf.frontier_port, self.conf.frontier_dest)
            
        return True
        
    def create_handlers(self):            
        # amq message handler   
        self.amq_handler = messaging.AMQMessageHandler(self)
        self.amq_handler.connect_mq(self.conf.mq_host, self.conf.mq_port, self.conf.mq_dest)
        
        return True        
        
    def connect_activemqs(self):        
        print 'start connect_activemq'
        self.frontier_sender_conn = stomp.Connection(host_and_ports=[(self.conf.frontier_host, self.conf.frontier_port)])
        print 'stomp.connection finished'
        self.frontier_sender_conn.start()
        print 'stomp.frontier_sender_conn.start finished'
        self.frontier_sender_conn.connect()
        print 'stomp.frontier_sender_conn.connect finished'

        return True     
    
    def disconnect_activemqs(self):        
        if self.frontier_sender_conn.is_connected():
            self.frontier_sender_conn.stop()                  
    
    def enqueue_frontier(self, r):
        msg = pickle.dumps(r)        
        self.frontier_sender_conn.send(
            msg,
            destination=self.conf.frontier_dest,
            persistent='false')
        
        self.stat_lock.acquire()
        try:
            self.enqueue_count += 1
        finally:
            self.stat_lock.release()        
            
    def discover_resource(self, r):
        if r.is_seed:
            self.get_seed_host_info(r)
        
        # apply before-enqueue filter
        if not self.before_enqueue_filter.check(r):
            self.on_resource_filtered(r, True)
            return
        
        # send to DUE
        self.due.put(r)
    
    def on_resource_fetched(self, r):
        self.stat_lock.acquire()
        try:        
            self.fetched_count += 1
        finally:
            self.stat_lock.release()
                
        msg = "[%s] [%d] [%s] [%s] [%s]" % (r.crawl_date, r.hop, r.content_type, r.url, r.parent_url)
        logging.getLogger('resource.fetched').info(msg)
        
        if r.content_type == 'text/html':
            if self.before_parse_filter.check(r):
                if isinstance(r.html, unicode):
                    logging.warning('messaging.py >> unexpected unicode html [%s]' % r.url)
                        
                links = html_helper.get_links(r.html)
                parent_url = r.url
                
                if isinstance(parent_url, unicode):
                    logging.warning('messaging.py >> unexpected unicode parent_url [%s]' % parent_url)
                
                hop = r.hop + 1
                for link_pair in links:
                    link = link_pair[0]
                    anchor_text = link_pair[1]
                    
                    if isinstance(link, unicode):
                        logging.warning('messaging.py >> unexpected unicode link [%s]' % link)
                                                                                                        
                    url = urlparse.urljoin(parent_url, link)
                    
                    if isinstance(url, unicode):
                        logging.warning('messaging.py >> unexpected unicode url [%s]' % url)
                    
                    try:
                        new_r = resource.Resource(None, parent_url, url, False, hop, r.batch, anchor_text)
                    except BadResourceError:
                        pass # ignored
                    else:
                        self.discover_resource(new_r)

    
        
    def on_resource_filtered(self, r, pre_filtering):
        # pre_filtering: before frontier; post_filtering: after frontier
        self.stat_lock.acquire()
        try:        
            if pre_filtering:
                self.pre_filtered_count += 1
            else:
                self.post_filtered_count += 1
        finally:
            self.stat_lock.release()
        
        msg = "[%s] [%s] [%s]" % (r.filtered_by, r.url, r.parent_url)
        logging.getLogger('resource.filtered').info(msg)
        
    def on_resource_failed(self, r, error_code, info):
        self.stat_lock.acquire()
        try:        
            self.failed_count += 1
        finally:
            self.stat_lock.release()        
        
        msg = '[%s] [%s] [%s] %s' % (error_code, r.url, r.parent_url, info)
        logging.getLogger('resource.failed').info(msg)
        
    def create_instance(self, config_str):
        try:
            segments = config_str.strip().split('::')
            module_name = '.'.join(segments[0].split('.')[:-1])
            class_name = segments[0].split('.')[-1] 
                                    
            params = segments[1:]
                
            __import__(module_name)
            module = sys.modules[module_name]
                 
            obj = module.__dict__[class_name](params)
            return obj                        
        except ValueError:            
            logging.critical('Invalid config: %s' % config_str)
            return None                            
        
    def report(self):
        spider_states = [ x.status for x in self.spiders ]
        idle_spider_count = spider_states.count('idle')
        
        fetching_spider_count = spider_states.count('fetching')
        saving_spider_count = spider_states.count('saving') 
        discovering_spider_count = spider_states.count('discovering') 
                
        elapsed_time = datetime.datetime.utcnow() - self.start_time
        total_seconds = elapsed_time.days*3600*24 + elapsed_time.seconds
        if total_seconds != 0:
            speed = float(self.dequeue_count) / float(total_seconds)
        else:
            speed = -1
           
        print "[%s day(s) %s second(s)] Speed:%.2f" % (
            elapsed_time.days,
            elapsed_time.seconds,
            speed
            )
                    
        print "Spiders -- idle:%d fetching:%d saving:%d discovering:%d" % (
            idle_spider_count,            
            fetching_spider_count,
            saving_spider_count,
            discovering_spider_count,            
            )                                 
        
        print "PreFiltered:%d" % (
            self.pre_filtered_count
            )
        
        if self.due.__class__.__name__ == 'MercatorDue':
            print self.due
            
        print "Frontier -- Enqueued:%d Dequeued:%d (+%d -%d *%d)" % (
            self.enqueue_count,
            self.dequeue_count,            
            self.fetched_count, 
            self.post_filtered_count,
            self.failed_count
            )                         
        
        print ""

        # check idle (every 10s)
        if elapsed_time.seconds % 10 == 0:
            if idle_spider_count == self.conf.spider_number:
                if self.due.__class__.__name__ == 'MercatorDue':
                    self.due.notify_idle()
            
class FrontierSender(object):
    """an intermediate component
    """
    def __init__(self, master):
        self._master = master
    
    def put(self, r):
        # enqueue
        self._master.enqueue_frontier(r)    
                    

def parse_parameters():
    """parse command line parameters
    -c  <config file>
    -s  <seed file>
    -u  <seed url>
    """    
    config_file = None
    seed_file = None
    seed_url = None    
    i = 1
    while i < len(sys.argv):             
        p = sys.argv[i]    
        if (i + 1) < len(sys.argv):
            if p == "-c": 
                config_file = sys.argv[i+1]
                i += 1
            elif p == "-s":
                seed_file = sys.argv[i+1]
                i += 1
            elif p == "-u":
                seed_url = sys.argv[i+1]
                i += 1
                
        i += 1
        
    return config_file, seed_file, seed_url    

def main():      
    config_file, seed_file, seed_url = parse_parameters()
    crawler_master = Master()
    crawler_master.start(config_file, seed_file, seed_url)


if __name__ == '__main__':
    main()
