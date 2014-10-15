#!/usr/bin/env

import config

import os
import sys
import threading
import urlparse

os.environ['DJANGO_SETTINGS_MODULE'] = config.django_settings_module

from django.db import IntegrityError, transaction
from django.utils.encoding import DjangoUnicodeDecodeError        
from citeseerx_crawl.main_crawl.models import Document, ParentUrl 

class CiteSeerWriter(object):
    def __init__(self, params):
        self.output_dir = params[0]
        self.update_parent_lock = threading.Lock()
        self.update_doc_lock = threading.Lock()
            
    def save(self, r, data):
        # save parent first (even fail to save doc)
        if r.parent_url != None:              
            try:
                self.update_parent_lock.acquire()
                                  
                try:
                    p = ParentUrl.objects.get(md5=r.parent_md5)
                    if r.crawl_date > p.last_crawl_date:
                        p.last_crawl_date = r.crawl_date                                
                except ParentUrl.DoesNotExist:
                    p = ParentUrl(
                        url=r.parent_url.decode('utf8'),
                        md5=r.parent_md5,
                        first_crawl_date=r.crawl_date,
                        last_crawl_date=r.crawl_date,
                        is_live=True
                        )  
                p.save()
                         
            finally:
                self.update_parent_lock.release()
        else:
            p = None
            
        self.save_doc(r, data, p)
    
    @transaction.commit_on_success    
    def save_doc(self, r, data, p):
        # save db first to get the id
        db_entry_updated = False
        file_updated = False
        
        try:
            self.update_doc_lock.acquire()
             
            try:
                d = Document.objects.get(md5=r.md5)                    
                
                # update sha1 when necessary
                if d.content_sha1 != r.content_sha1:
                    d.content_sha1 = r.content_sha1
                    d.state = 0 # set state to "crawled"
                    if r.crawl_date > d.update_date: 
                        d.update_date = r.crawl_date
                    
                    db_entry_updated = True
                    file_updated = True                
                
                # update parent when necessary
                # only update db, not file
                if p != None:
                    if d.parent == None:                
                        d.parent = p                
                        db_entry_updated = True                
                    elif is_better_parent(d.url, d.parent.url, p.url):                
                        d.parent = p
                        db_entry_updated = True                                                       
                    
            except Document.DoesNotExist:
                d = Document(
                    url=r.url.decode('utf8'),
                    md5=r.md5,
                    host=r.host.decode('utf8'),
                    content_sha1=r.content_sha1,                    
                    discover_date=r.crawl_date,
                    update_date=r.crawl_date,
                    parent=p,
                    state=0
                    )
                db_entry_updated = True
                file_updated = True
              
            if db_entry_updated:
                d.save()                                         
        finally:
            self.update_doc_lock.release()                                                                                                      
        
        if file_updated:    
            try:
                file_path = self.id_to_path(d.id)
                dir_path = os.path.dirname(file_path)
                                            
                if not os.path.exists(dir_path):
                    os.makedirs(dir_path)
                
                f = open(file_path, 'w')
                f.write(data)
                f.close()
                    
                # save metadata file
                f = open(file_path + ".met", 'w')
                f.write(self.get_metadata_xml(r))
                f.close()
            except IOError, e:
                raise IOError, 'File (id=%d) failed to save: %s' % (d.id, str(e))
                return
    
    def get_metadata_xml(self, r):
        xml = '<?xml version="1.0" encoding="UTF-8"?><CrawlData><crawlDate>%s</crawlDate><lastModified>%s</lastModified><url>%s</url><parentUrl>%s</parentUrl><SHA1>%s</SHA1></CrawlData>\n' % (r.crawl_date, r.last_modified, r.url, r.parent_url, r.content_sha1)
        
        return xml                
    
    def id_to_path(self, id):
        p1 = id / 1000000
        p2 = (id % 1000000) / 1000
        p3 = id % 1000
        s1 = str(p1).zfill(3) 
        s2 = str(p2).zfill(3)
        s3 = str(p3).zfill(3)
        p = "%s/%s/%s/%s.%s.%s.pdf" % (s1, s2, s3, s1, s2, s3)
        return os.path.join(self.output_dir, p)
        
def is_better_parent(doc_url, old_par_url, new_par_url):
    if new_par_url == None:
        return False
    
    if old_par_url == None:
        return True
      
    scheme_doc, host_doc, path_doc, query_doc, fragment_doc = urlparse.urlsplit(doc_url)
    scheme_old, host_old, path_old, query_old, fragment_old = urlparse.urlsplit(old_par_url)
    scheme_new, host_new, path_new, query_new, fragment_new = urlparse.urlsplit(new_par_url)
        
    dir_doc = os.path.dirname(path_doc)
    dir_old = os.path.dirname(path_old)
    dir_new = os.path.dirname(path_new)
        
    if host_new == host_doc and host_old != host_doc:
        better = True
    elif host_new == host_doc and host_old == host_doc:
        if dir_new == dir_doc and dir_old != dir_doc:
            better = True
        elif dir_new != dir_doc and dir_old != dir_doc:
            if dir_doc.startswith(dir_new) and not dir_doc.startswith(dir_old):
                better = True 
            elif dir_doc.startswith(dir_new) and dir_doc.startswith(dir_old):
                if len(dir_new) > len(dir_old):
                    better = True                        
                else:   
                    better = False                             
            else:
                better = False
        else:
            better = False 
    else:            
        better = False
        
    return better 
