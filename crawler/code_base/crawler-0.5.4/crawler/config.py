#!/usr/bin/env python2.4

import ConfigParser
import os
import sys

DEFAULT_SECTION='sys'

class Config(object):

    VALUE_ATTR = [
        'spider_number',
        'spider_timeout',
        'ignore_invalid_seeds',
        'fetching_on_open_logging'        
        ]
    
    STRING_ATTR = [
        'user_agent',
        'seed_line_parser_class',
        'activemq_frontier',        
        'activemq_message_queue',
        'before_enqueue_filter_rules',
        'before_download_filter_rules',
        'before_save_filter_rules',
        'before_parse_filter_rules',  
        'log_dir',
        'bots_dir',
        'writer',
        'due',            
        ]
        
    def __init__(self):
        """set default values of config parameters"""
        self.user_agent = ''
        self.spider_number = 100
        self.spider_timeout = 10
        self.ignore_invalid_seeds = False
        self.seed_line_parser_class = 'crawler.seed_line_parsing.DefaultSeedLineParser'
        self.activemq_frontier = 'localhost:61613:/queue/frontier'
        self.activemq_message_queue = 'localhost:61613:/queue/message_queue'        
        self.before_enqueue_filter_rules = "SchemeRule"
        self.before_download_filter_rules = "AllowContentTypeRule::text/html"
        self.before_save_filter_rules = "AllowContentTypeRule::*"
        self.before_parse_filter_rules = "AllowPatternRule::*"
        self.log_dir = '/tmp/log'
        self.bots_dir = '/tmp/bots'
        self.writer = 'crawler.output.MirrorWriter::/tmp/crawl'
        self.due = 'crawler.due.MemoryDue'
        self.fetching_on_open_logging = False
        
        self.post_process()
        
    def post_process(self):
        # parse activemq_frontier
        parts = self.activemq_frontier.split(':')
        if len(parts) == 3:
            try:
                self.frontier_port = int(parts[1])
            except ValueError:
                sys.stderr.write("Invalid activemq_frontier.\n")
                return False

            self.frontier_host = parts[0]
            self.frontier_dest = parts[2]
        else:
            sys.stderr.write("Invalid activemq_frontier.\n")
            return False

        # parse activemq_message_queue
        parts = self.activemq_message_queue.split(':')
        if len(parts) == 3:
            try:
                self.mq_port = int(parts[1])
            except ValueError:
                sys.stderr.write("Invalid activemq_message_queue.\n")
                return False
            
            self.mq_host = parts[0]
            self.mq_dest = parts[2]
        else:
            sys.stderr.write("Invalid activemq_frontier.\n")
            return False                                                                                                                        
        
        return True
        
    def load(self, config_file):
        """load from config file"""
        if config_file != None:
            if os.path.isfile(config_file):
                parser = ConfigParser.ConfigParser()
                parser.read(config_file)
            
                for k in Config.VALUE_ATTR:
                    try:
                        v = parser.get(DEFAULT_SECTION, k)
                        self.__dict__[k] = eval(v)                         
                        print "%s =" % k, v
                    except ConfigParser.NoOptionError:
                        pass   
                        
                for k in Config.STRING_ATTR:
                    try:
                        v = parser.get(DEFAULT_SECTION, k)
                        self.__dict__[k] = v                         
                        print "%s =" % k, v
                    except ConfigParser.NoOptionError:
                        pass                                          
                
                suc = self.post_process()
                return suc
            else:
                print "config file %s does not exist" % config_file
                return False
