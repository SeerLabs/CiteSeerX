#!/usr/bin/env
# adopted from csxbot-0.3/csxcrawler/output.py

import runconfig

import os
import sys
import threading
import urlparse
import socket
#from django.conf import settings
import settings
import _mysql_exceptions

os.environ['DJANGO_SETTINGS_MODULE'] = runconfig.django_settings_module

from django.db import IntegrityError, transaction,connections,connection
from django.utils.encoding import DjangoUnicodeDecodeError        
#from citeseerx_crawl.main_crawl.models import Document, ParentUrl
#from models import Document, ParentUrl
import cursorutils
#from my_crawl.slide_crawl.models import Document, ParentUrl, Slide

class CiteSeerWriter(object):
    def __init__(self, params):
        self.output_dir = params[0]  # directory of document file and metadata
        self.crawler = params[1]     # crawler name
        self.update_parent_lock = threading.Lock()
        self.update_doc_lock = threading.Lock()
        self.hostname = os.getenv('HOSTNAME')
        self.docid = None #record document ID
   
    # check if the resource URL has existed in db by matching md5
    def dbcheck(self,r):
        try:
            self.update_doc_lock.acquire()
	    cursor = connection.cursor()
	    dbquery = "SELECT * FROM "+runconfig.dbt_document+\
		" WHERE md5='"+r.md5+"'"
	    cursor.execute(dbquery)
	    rows = cursorutils.dictfetchall(cursor)
	    if not rows:
		return False
	    else: 
		row = rows[0]
		self.docid = row['id']
        finally:
            self.update_doc_lock.release()                                                                                                      

    def save(self, r, data):
        # save parent first (even fail to save doc)
        if r.parent_url != None:              
            try:
                self.update_parent_lock.acquire()
	  	cursor = connection.cursor()
                dbquery = "SELECT * FROM "+runconfig.dbt_parenturl+" WHERE md5='"+r.parent_md5+"'"
		cursor.execute(dbquery)
		rows = cursorutils.dictfetchall(cursor)
	 	# insert a new parent URL
		if not rows:
		    dbquery = "INSERT INTO "+runconfig.dbt_parenturl+\
			" (url,md5,first_crawl_date,last_crawl_date,is_live) "+\
                        " VALUES (%s,%s,%s,%s,%s)" 
                    dbquerypar = (r.parent_url.decode('utf8'),r.parent_md5,str(r.crawl_date),str(r.crawl_date),'1')
		    cursor.execute(dbquery,dbquerypar)
		    pid = cursor.lastrowid
		    transaction.commit_unless_managed()
		# update an existing parent URL
		else:
		    row = rows[0]
		    pid = row['id']
		    if r.crawl_date > row['last_crawl_date']:
			dbquery = "UPDATE "+runconfig.dbt_parenturl+\
			    " SET last_crawl_date='"+str(r.crawl_date)+\
			    "' WHERE id="+str(row['id'])+";"
			cursor.execute(dbquery)
		        transaction.commit_unless_managed()
            finally:
                self.update_parent_lock.release()
        else:
            pid = None
            
        # save document
   	# rpid is the parent id of the resource URL
	# if the resource URL is a seed, rpid is None
	# other wise, it is the id in the parent url table. 
        self.save_doc(r, data, pid)
    
#   @transaction.commit_on_success    
    def save_doc(self, r, data, pid):
        # save db first to get the id
        db_entry_updated = False
	# save a new copy of file only if it is new or an updated version
        file_updated = True
        try:
            self.update_doc_lock.acquire()
	    cursor = connection.cursor()
	    dbquery = "SELECT * FROM "+runconfig.dbt_document+\
		" WHERE md5='"+r.md5+"'"
	    cursor.execute(dbquery)
	    rows = cursorutils.dictfetchall(cursor)
	    # insert a new record
 	    # note that the cursor cannot convert a Python "None" to 
	    # a MySQL NULL value. 
	    parent_idstr = str(pid) if str(pid) != 'None' else None
	    if not rows:
		dbquery = "INSERT INTO "+runconfig.dbt_document+\
		    "(url,md5,host,rev_host,content_sha1,discover_date,update_date,parent_id,submission_id,state)"+\
                    " VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"
                dbquerypar = (r.url,r.md5,r.host,r.host[::-1],r.content_sha1,str(r.crawl_date),str(r.crawl_date),parent_idstr,str(r.batch),'0')
                #print dbquery % dbquerypar
		try: 
		    cursor.execute(dbquery,dbquerypar)
	   	except TypeError,e:
		    print 'output.py. TypeError. dbquery = ',dbquery % dbquerypar
		    raise SystemExit(e)
		except _mysql_exceptions.OperationalError,e:
		    print 'output.py. MySQL Operationl Error. dbquery = ',dbquery % dbquerypar
		    raise SystemExit(e)

		transaction.commit_unless_managed()
		self.docid = cursor.lastrowid
		
		db_entry_updated = True
		# need to update file
		file_updated = False 
	    # update an existing record, in following cases
	    # (1) if SHA1 is different, update SHA1 and update time
	    # (2) if URL has a valid parent (non-seed), record the parent_id 
	    #     if it already has a parent_id, choose a better one
	    else:
		row = rows[0]
		if row['content_sha1'] != r.content_sha1:
		    file_updated = False
		    row['content_sha1'] = r.content_sha1
		    row['state'] = 0
		
		    if r.crawl_date > row['update_date']: 
			row['update_date'] = r.crawl_date

		# if pid is None, no need to update parent id in document table
		if pid != None:
		    if row['parent_id'] == None:
			row['parent_id'] = pid
		    # there is a valid parent_id with the existing document URL
		    else:
			dbquery = "SELECT url FROM "+runconfig.dbt_parenturl+\
			    " WHERE id="+str(row['parent_id'])
			cursor.execute(dbquery)
			row_parent = cursorutils.dictfetchone(cursor)
			# if new parent is better, use new parent id 
			if is_better_parent(row['url'],\
			    row_parent['url'],r.parent_url):
			    row['parent_id'] = pid

		# update the existing record 
		dbquery = "UPDATE "+runconfig.dbt_document+\
		    " SET content_sha1='"+row['content_sha1']+\
		    "',state="+str(row['state'])+\
		    ",update_date='"+str(row['update_date'])+\
		    "',parent_id="+str(row['parent_id'])+\
		    " WHERE id="+str(row['id'])
		cursor.execute(dbquery)
		transaction.commit_unless_managed()

	        self.docid = row['id']
        finally:
            self.update_doc_lock.release()                                                                                                      
        # if need to update document file
        if not file_updated:    
            try:
                # create file folder if not exist 
                file_path = self.id_to_path(self.docid)
                dir_path = os.path.dirname(file_path) 
		try: 
                    if not os.path.exists(dir_path):
                        os.makedirs(dir_path)
		except:
		    print 'error when making directory: '+dir_path
                
                # save document file to repository
                f = open(file_path, 'w')
                f.write(data)
                f.close()

                # save metadata file
                f = open(file_path + ".met", 'w')
                f.write(self.get_metadata_xml(r))
                f.close()
            except IOError, e:
                print 'File (id=%d) failed to save: %s' % (self.docid, str(e))
                raise IOError, 'File (id=%d) failed to save: %s' % (self.docid, str(e))
                return

	# check file availability
	try: 
	    fc = open(file_path)
      	except IOError:
      	    raise IOError,'File (id=%d) failed to save: %s' % (self.docid,str(e))
	finally:
	    fc.close()
    
    def savetxt(self,txtdata): 
        # copy text file into same directory as doc and metadata files
        file_pdf = self.id_to_path(self.docid)
        file_pdfext = os.path.splitext(file_pdf)
        file_txt = file_pdfext[0]+'.txt'
        
        # save text file
	try: 
            ft = open(file_txt,'w')
            ft.write(txtdata)
            ft.close()
	except IOError,e:
	    raise IOError, 'File {0} failed to save'.format(file_txt)

	    return
        
    def get_metadata_xml(self, r):
        xml = '<?xml version="1.0" encoding="UTF-8"?><CrawlData><crawler>%s</crawler><crawlDate>%s</crawlDate><lastModified>%s</lastModified><url>%s</url><parentUrl>%s</parentUrl><SHA1>%s</SHA1></CrawlData>\n' % (self.crawler, r.crawl_date, r.last_modified, r.url, r.parent_url, r.content_sha1)
        
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
