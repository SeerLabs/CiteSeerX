#!/usr/bin/env python

import sys
import pickle
import datetime
from threading import Thread
from Queue import Empty
import logging

import stomp


class Message(object):
    def __init__(self):
        self.info = ""
        self.time_stamp = datetime.datetime.now()
        
    def __str__(self):
        return "[%s] %s" % (self.time_stamp, self.info)       
        
class ResourceDiscoveredMsg(Message):
    def __init__(self, r):
        Message.__init__(self)
        self.resource = r
    
    def __str__(self):
        return "[%s] [%s]" % (self.time_stamp, self.resource.url)
        
class AMQMessageHandler(stomp.ConnectionListener):
    def __init__(self, master):
        self.mq_conn = None
        self._master = master

    def connect_mq(self, host, port, dest):
        self.mq_conn = stomp.Connection(host_and_ports=[ (host, port) ])
        self.mq_conn.set_listener('', self)
        self.mq_conn.start()
        self.mq_conn.connect()
        self.mq_conn.subscribe(destination=dest, ack='auto')

    def disconnect_mq(self):
        if self.mq_conn.is_connected():
            self.mq_conn.stop()

    def on_error(self, headers, message):
        logging.error("MQError: %s." % message)

    def on_message(self, headers, message):
        try:            
            m = pickle.loads(message)
        except ValueError, e:
            logging.getLogger('stomp.py').error('Fail to load resource from frontier: %s' % str(e))                                                       
        except:
            logging.exception('Fail to load message from MQ')
        else:
            try:
                self.handle_message(m)
            except:
                logging.exception('[' + m.resource.url + '] [' + m.__class__.__name__ + ']')
                
    def handle_message(self, m):
        if m.__class__.__name__.endswith('ResourceDiscoveredMsg'):
            self.handle_resource_discovered_msg(m)
        else:
            # should not reach here
            logging.warning("Bug: [MessageHandler.handle_message] %s" % m.__class__.__name__)

    def handle_resource_discovered_msg(self, m):        
        self._master.discover_resource(m.resource)
