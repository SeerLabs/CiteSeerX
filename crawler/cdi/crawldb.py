# This class is capable of doing the following tasks
# (*) create database if it does not exist
#     the name of the database is specified in the settings.py 
# (*) generate database tables if they do not exist
#     the tables in the database are "dbt_document" and "dbt_parenturl"
#     as specified in the runconfig.py 
# (*) check database elements 
#     use certain keys 
# (*) write data into a given database table
import settings
from django.db import connection,transaction
import _mysql_exceptions
import runconfig
import threading
import cursorutils
class CrawlDB(object):
    def __init__(self):
	if settings.DATABASES.has_key('default'):
	    self.dbname = settings.DATABASES['default']['NAME']
	else:
	    self.dbname = settings.DATABASES['NAME']
	self.update_doc_lock = threading.Lock()
	
    # check if a record has already stored in database
    # by matching keys. return True if record is already existed
    # or False if not
    def checkRecord(self,dbt_name,md5='',id=0):
	# check which key to use
	if md5 == '' and id == 0:
	    # a keyword should be provided
	    raise Exception("You do not provide a key")
	elif md5 != '' and id >0:
	    # only one keyword 
	    raise Exception("Only one key is needed")
	elif md5 != '':
	    q = " WHERE md5='"+md5+"'"
	elif id >0:
	    q = " WHERE id="+str(id)

        try:
            self.update_doc_lock.acquire()
            cursor = connection.cursor()
            dbquery = "SELECT * FROM "+dbt_name+q
            cursor.execute(dbquery)
            rows = cursorutils.dictfetchall(cursor)
            if not rows:
		recordExist = False
            else:
		recordExist = True
        finally:
            self.update_doc_lock.release()                                       
	return recordExist

    # query id from crawler database given a certain condition
    # Note that in the "dbquery", id must be included in the query
    def queryDocID(self,dbquery):
	docids = []
	try:
	    self.update_doc_lock.acquire()
	    cursor = connection.cursor()
	    cursor.execute(dbquery)
	    rows = cursorutils.dictfetchall(cursor)
	    if not rows:
		return docids
	finally:
	    self.update_doc_lock.release()

	for row in rows:
	    docids.append(row['id'])

	return docids

    # you must have the permission to create tables 
    # create tables if they do not exist, table names can be arbitrary
    # (set in runconfig.py, but columns are 
    def createTables(self):
	cursor = connection.cursor()
	# create document table
	dbquery = 'CREATE TABLE IF NOT EXISTS `'+runconfig.dbt_document+'` (\
  		`id` int(11) NOT NULL AUTO_INCREMENT,\
  		`url` varchar(255) COLLATE utf8_bin NOT NULL,\
  		`md5` varchar(32) COLLATE utf8_bin NOT NULL,\
  		`host` varchar(255) COLLATE utf8_bin DEFAULT NULL,\
  		`rev_host` varchar(255) COLLATE utf8_bin DEFAULT NULL,\
  		`content_sha1` varchar(40) COLLATE utf8_bin NOT NULL,\
  		`discover_date` datetime NOT NULL,\
  		`update_date` datetime NOT NULL,\
  		`parent_id` int(11) DEFAULT NULL,\
  		`state` int(11) DEFAULT NULL,\
  		`submission_id` int(11) DEFAULT NULL,\
  		PRIMARY KEY (`id`),\
  		UNIQUE KEY `url` (`url`),\
  		UNIQUE KEY `md5` (`md5`),\
  		KEY `main_crawl_document_content_sha1` (`content_sha1`),\
  		KEY `main_crawl_document_discover_date` (`discover_date`),\
  		KEY `main_crawl_document_update_date` (`update_date`),\
  		KEY `main_crawl_document_parent_id` (`parent_id`),\
  		KEY `main_crawl_document_host` (`host`),\
  		KEY `main_crawl_document_state` (`state`),\
  		KEY `rev_host_discover_date` (`rev_host`,`discover_date`),\
  		KEY `rev_host_state` (`rev_host`,`state`)\
		) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;'
	try:
 	    print "create document table ..."
	    cursor.execute(dbquery)
	    print 'database tables created successfully: main_crawl_document'
	except _mysql_exceptions.Warning:
	    print "table '"+runconfig.dbt_document+"' already exists"

	# create parent url table 
	dbquery = "CREATE TABLE IF NOT EXISTS `"+runconfig.dbt_parenturl+"` (\
        	`id` int(11) NOT NULL auto_increment,\
	        `url` varchar(255) collate utf8_bin NOT NULL,\
	        `md5` varchar(32) collate utf8_bin NOT NULL,\
	        `first_crawl_date` datetime NOT NULL,\
	        `last_crawl_date` datetime NOT NULL,\
	        `is_live` tinyint(1) NOT NULL default 1,\
	        PRIMARY KEY  (`id`),\
	        UNIQUE KEY `url` (`url`),\
	        UNIQUE KEY `md5` (`md5`),\
	        KEY `main_crawl_parenturl_first_crawl_date` (`first_crawl_date`),\
	        KEY `main_crawl_parenturl_last_crawl_date` (`last_crawl_date`),\
	        KEY `main_crawl_parenturl_is_live` (`is_live`)\
	        ) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;"

	try: 
            print "create table parenturl ..."
	    cursor.execute(dbquery)
	except _mysql_exceptions.Warning:
	    print "table '"+runconfig.dbt_parenturl+"' already exists"

