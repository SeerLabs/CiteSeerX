#!/usr/bin/env python2.4
import hashlib
import urlparse
import url_normalization
import logging
from datetime import *

from exception import BadResourceError

class Resource(object):
    """Resource Class
    
    Attributes:    
        Optional:        
            parent_url
            parent_md5
        
        Creation Time Attributes:
            code (use md5 if not provided)
            url
            md5
            is_seed
            hop
            scheme
            host
            path
            query
            port
            segments
            depth
            batch (default 0)
            anchor_text (default '')
        
        Run Time Attributes:
            crawl_date
            last_modified
            content_type
            content_length
            charset        
            html
            content_sha1
            filtered_by
        
        Not used yet:
            from_cache = False
    """
    def __init__(self, code, parent_url, url, is_seed, hop, batch=0, anchor_text=''):
        self.code = code                
        
        if parent_url != None:
            norm_parent_url = url_normalization.get_canonical_url(parent_url)
            if norm_parent_url == '':
                print 'parent_url=',parent_url
                raise BadResourceError, 'norm_parent_url is empty string. Invalid Parent URL'
            
            if isinstance(norm_parent_url, unicode):
                norm_parent_url = norm_parent_url.encode('utf8')
                
            try:
                norm_parent_url.decode('utf8')
            except:
                logging.warning('resource.py >> cannot decode norm_parent_url: %s' % norm_parent_url)                
            
            self.parent_md5 = hashlib.md5(norm_parent_url).hexdigest()
        else:
            norm_parent_url = None
            self.parent_md5 = None                                         
            
        norm_url = url_normalization.get_canonical_url(url)
        if norm_url == '':
            raise BadResourceError, 'Invalid URL'
            
        if isinstance(norm_url, unicode):
            norm_url = norm_url.encode('utf8')
            
        try:
            norm_url.decode('utf8')
        except:
            logging.warning('resource.py >> cannot decode norm_url: %s' % norm_url)                      
                
        self.parent_url = norm_parent_url
        self.url = norm_url
        self.is_seed = is_seed
        self.hop = hop
        self.batch = batch
        
        self.scheme, self.host, self.path, self.query, fragment = urlparse.urlsplit(norm_url)
        
        if self.host.find(':') != -1:
            parts = self.host.split(':')
            self.host = parts[0]
            self.port = parts[1]
        else:
            self.port = None            
        
        # self.path always starts with '/' since http://www.aaa.com will 
        # always be normalized to http://www.aaa.com/
        
        self.segments = self.path.split('/')[1:]
        
        # http://www.cse.psu.edu/                      Depth 1
        # http://www.cse.psu.edu/index.php             Depth 1
        # http://www.cse.psu.edu/~shzheng/index.htm    Depth 2 
        self.depth = len(self.segments)             
        
        self.md5 = hashlib.md5(norm_url).hexdigest()        

        # if no code is assigned, use MD5 as default code
        if code == None or code == '':
            self.code = self.md5
        
        self.anchor_text = anchor_text
        
        self.crawl_date = datetime.now()# default time is time to add to db
        self.last_modified = None
        self.content_type = None
        self.content_length = -1
        self.charset = None
        self.from_cache = False
        self.html = None
        self.content_sha1 = hashlib.sha1(self.url).hexdigest() #None
        self.filtered_by = None
        self.no_fetch = False
        self.ext = '.pdf' # by default, extension is .pdf
