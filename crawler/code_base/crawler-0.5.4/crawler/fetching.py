#!/usr/bin/env python2.4

import httplib
import urllib2
import pickle
import datetime
import hashlib
import logging
import codecs
import stomp
import socket
from html_helper import get_charset

class Spider(stomp.ConnectionListener):
    """Spider class
    
    possible status:
        idle        
        fetching
        saving
        discovering
    """
    
    def __init__(self, id, master):        
        self.frontier_conn = None
        self.id = id
        self._master = master
        self.status = 'idle'    
        

    def connect_frontier(self, host, port, dest):
        self.frontier_conn = stomp.Connection(host_and_ports=[ (host, port) ])
        self.frontier_conn.set_listener('', self)
        self.frontier_conn.start()
        self.frontier_conn.connect()
        self.frontier_conn.subscribe(destination=dest, ack='auto')
    
    def disconnect_frontier(self):
        if self.frontier_conn.is_connected():
            self.frontier_conn.stop()

    def __str__(self):
        return "Spider-%s" % self.id
    
    def fetch(self, r):
        self.status = 'fetching'
        
        data = None

        user_agent = self._master.conf.user_agent
        headers = {'User-Agent':user_agent}
        req = urllib2.Request(r.url, None, headers)
        
        f = None                                    
        try:                                            
            try:
                f = urllib2.urlopen(req)
                self.parse_headers(r, f)
            except httplib.HTTPException, e:
                self._master.on_resource_failed(r, 'httplib.HTTPException', str(e))
                return
            except urllib2.URLError, e:
                if hasattr(e, 'code'):
                    error_code = e.code
                else:
                    error_code = 'N/A'
                    
                if hasattr(e, 'reason'):
                    info = e.reason
                else:
                    info = ''
                                        
                self._master.on_resource_failed(r, error_code, info)                
                return
            except socket.timeout:        
                self._master.on_resource_failed(r, 'socket.timeout', '')
                return
            except socket.error, e:
                self._master.on_resource_failed(r, 'socket.error', str(e))
                return
            except IOError, e:
                self._master.on_resource_failed(r, 'IOError', str(e))
                return
            
            if self._master.conf.fetching_on_open_logging:
                msg = "%s\t%d\t%s\t%s\t%s\t%s\t%s" % (r.host, r.hop, r.parent_md5, r.md5, r.anchor_text, r.content_type, r.url)
                logging.getLogger('fetching.on_open').info(msg)                
                        
            if not self._master.before_download_filter.check(r):                
                self._master.on_resource_filtered(r, False)
                return            
                
            try:
                data = f.read()
            except IOError, e:
                self._master.on_resource_failed(r, 'IOError', str(e))
                return
            except socket.timeout:
                self._master.on_resource_failed(r, 'socket.timeout', '')
                return
            except socket.error, e:
                self._master.on_resource_failed(r, 'socket.error', str(e))
                return
            except httplib.HTTPException, e:
                self._master.on_resource_failed(r, 'httplib.HTTPException', str(e))
                return
            except ValueError, e:
                self._master.on_resource_failed(r, 'ValueError', str(e))
                return
                
        finally:
            if f:
                f.close()        
         
        if r.content_type == "text/html":                        
            # charset parsed from header and html could be different
            if r.charset is None or r.charset == '':
                html_charset = get_charset(data)
                r.charset = html_charset                
                                    
            if r.charset is not None and r.charset != '':
                encoding = r.charset                                
                
                try:
                    codecs.lookup(encoding)
                except LookupError, e:                    
                    #logging.warning('[%s] [%s] %s' % ('invalid-encoding', r.url, str(e)))                    
                    encoding = 'utf8' # replace it with utf8 if invalid  
            else:
                # try utf-8 if we don't know the encoding                
                encoding = 'utf8'
            
            udata = data.decode(encoding, 'replace')            
            data = udata.encode('utf8')
            r.html = data                                          
                
        # update content_length when we have the real data
        r.content_length = len(data)
        r.content_sha1 = hashlib.sha1(data).hexdigest()
            
        r.crawl_date = datetime.datetime.now()
                
        self.status = 'saving'
        
        if self._master.before_save_filter.check(r):
            try:
                self._master.writer.save(r, data)
            except IOError, e:
                self._master.on_resource_failed(r, 'IOError', str(e))                
                return
            except OSError, e:
                self._master.on_resource_failed(r, 'OSError', str(e))
                return
        
        self.status = 'discovering'                           
        self._master.on_resource_fetched(r)
    
    def parse_headers(self, r, f):
        """headers will be converted to lower case"""
        #print 'content-type:', f.info().getheaders('content-type')
        #print 'content-length:', f.info().getheaders('content-length')              
        #print 'last-modified:', f.info().getheaders('last-modified')
        
        if len(f.info().getheaders('content-type')) > 0: 
            content_type_header = f.info().getheaders('content-type')[0].lower()
        else:
            content_type_header = 'N/A'
        
        if len(f.info().getheaders('content-length')) > 0:                        
            content_length_header = f.info().getheaders('content-length')[0]
        else:
            content_length_header = 'N/A'
        
        if len(f.info().getheaders('last-modified')) > 0:
            last_modified_header = f.info().getheaders('last-modified')[0]
            # todo: need to convert it to datetime object            
            r.last_modified = last_modified_header
            #print 'r.last_modified:', r.last_modified
        else:
            r.last_modified = None    
        
        if content_type_header.find(';') != -1:                
            parts = content_type_header.split(';')
            content_type = parts[0].strip()
                
            if parts[1].find('charset') != -1 and parts[1].find('=') != -1:
                seg = parts[1].split('=')
                charset = seg[1].strip()
            else:    
                charset = None
        else:
            content_type = content_type_header.strip()
            charset = None
        
        try:
            content_length = int(content_length_header)
        except ValueError:
            content_length = -1
            
        r.content_type = content_type
        r.content_length = content_length
        r.charset = charset                
        
    def on_error(self, headers, message):        
        logging.error("FrontierError: %s." % message)

    def on_message(self, headers, message):
        try:            
            try:            
                r = pickle.loads(message)
            except ValueError, e:
                logging.getLogger('stomp.py').error('Fail to load resource from frontier: %s' % str(e))                                                               
            except:
                logging.exception('Fail to load resource from frontier')
            else:
                try:
                    self._master.stat_lock.acquire()
                    try:
                        self._master.dequeue_count += 1
                    finally:
                        self._master.stat_lock.release()
                    
                    self.fetch(r)
                except:                    
                    logging.exception('%s\nUrl:%s\nParent:%s' % ('-'*80, r.url, r.parent_url))
        finally:
            self.status = 'idle'
        
