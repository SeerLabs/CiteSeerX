#!/usr/bin/env python2.4

import pickle
import logging

import stomp

import messaging
import resource
from exception import BadResourceError

class Submitter(object):
    """Submitter class
    
    Attributes:
        mq_host   
        mq_port
        mq_dest
        mq_sender_conn     
    """
    
    def __init__(self, mq_host, mq_port, mq_dest):        
        self.mq_sender_conn = None
        self.mq_host = mq_host
        self.mq_port = mq_port
        self.mq_dest = mq_dest 

    def connect_mq(self):                
        self.mq_sender_conn = stomp.Connection(host_and_ports=[(self.mq_host, self.mq_port)])
        self.mq_sender_conn.start()
        self.mq_sender_conn.connect()

        return True     
    
    def disconnect_mq(self):        
        if self.mq_sender_conn.is_connected():
            self.mq_sender_conn.stop() 
            
    def send_to_mq(self, m):
        msg = pickle.dumps(m)
        self.mq_sender_conn.send(
            msg,
            destination=self.mq_dest,
            persistent='false')                      
    
    def submit(self, url, batch=0):
        """Load one seed from seed url.
        
        Args:
            url: seed link
            batch: batch # of the url to submit
            
        Returns:
            success/fail
        """
        
        try:
            r = resource.Resource(None, None, url, True, 0, batch)
        except BadResourceError:
            return False
        except KeyError:
            print '<crawler.submit.Submitter.submit KeyError:> url = ',url
            print '<crawler.submit.Submitter.submit KeyError:> batch = ',batch
        except UnicodeEncodeError:
	    print '<crawler.submit.Submitter.submit UnicodeEncodeError:> url = ',url
            print '<crawler.submit.Submitter.submit UnicodeEncodeError:> batch = ',batch
        else:                
            m = messaging.ResourceDiscoveredMsg(r)            
            self.send_to_mq(m)
            return True
    
    def submit_seeds_from_file(self, seed_file, seed_line_parser, ignore_invalid_seeds, batch=0):    
        """Load seeds from file.
        
        Args:
            seed_file: path of input seed file
            seed_line_parser: seed line parser
            ignore_invalid_seeds: whether ignore invaild seeds
                    
        Returns:
            # of seeds submitted
        """
        
        line_count = 1
        
        try:
            f_seed = open(seed_file, 'r')
        except IOError:
            logging.exception('Cannot open seed file %s' % seed_file)
            return None
        else:                                    
            for line in f_seed:
                r = seed_line_parser.parse(line[:-1], batch)
                if r != None:                                    
                    m = messaging.ResourceDiscoveredMsg(r)            
                    self.send_to_mq(m)             
                else:
                    logging.error('Invalid seed at line %d' % line_count)
                    if not ignore_invalid_seeds:
                        break
                    
                line_count += 1
        
        return line_count-1
