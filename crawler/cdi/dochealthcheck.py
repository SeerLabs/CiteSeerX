#!/usr/bin/python
# check document health from the crawler repository, document must be in PDF
# format. The program will write a report file listing unhealthy file IDs.
# If not, you can toggle if 
# this document should be removed from the repository and or from the 
# database. This program also checks the size of each document and write them
# to a separate file. 
#
# The program just use the "file" command to check the MIME type of that document. 
# It uses Django cursor to execute the DELETE command. 
# 
# input: 
# * accepted_mime_types: accepted document MIME types
# * ids: document IDs to be checked
# 
# output: 
# * docsize.txt: a file containing the document sizes in Byte
# * doccheck.unhealth.txt: a file containing IDs of unhealthy documents
#
import os # define invironment variable
import dochealthcheck_config # log parser configuration
from django.conf import settings
os.environ['DJANGO_SETTINGS_MODULE'] = dochealthcheck_config.django_settings_module
import commands
from django.db import connection,transaction
import socket
import crawldb
import printinfo
import counter
import threading
import cursorutils
def startup(verbose=False):

    # create on-screen information print object
    infoprinter = printinfo.printInfo()
  
    # define counters
    counters = counter.Counter()
    counters.newCounter('all')
    counters.setCounter('all',0)
    counters.newCounter('healthy')
    counters.newCounter('inrepo')
  
    # create output directory if it does not exist
    if not os.path.exists(dochealthcheck_config.outputdir):
        os.makedirs(dochealthcheck_config.outputdir)
  
    # create database object
    cdb = crawldb.CrawlDB()
    # print database names
    infoprinter.printPara('Database name',cdb.dbname)

    # create lock object
    update_doc_lock = threading.Lock()
  
    try:
        update_doc_lock.acquire()
        cursor = connection.cursor()
     	# select documents to check
    	#dbquery = "SELECT id FROM "+dbt_name+" WHERE submission_id=-2" 
    	dbquery = "SELECT id FROM "+dochealthcheck_config.dbt_name+" WHERE submission_id=-2" 
    	print dbquery
        cursor.execute(dbquery)
        rows = cursorutils.dictfetchall(cursor)
        if not rows:
            recordExist = False
            infoprinter.printPara('Number of records',str(0))
            return
        else:
            recordExist = True
            infoprinter.printPara('Number of records',str(len(rows)))
            ids = rows
    finally:
        update_doc_lock.release()
  
    # open document size file to write 
    f_docsize = open(dochealthcheck_config.outputdir+dochealthcheck_config.f_docsize,'w') 
    f_docsize.write('crawlid byte\n')
  
    # open unhealthy document to write
    f_unhealthdoc = open(dochealthcheck_config.outputdir+dochealthcheck_config.f_unhealthdoc,'w')
    f_unhealthdoc.write('unhealth_crawlid\n')
  
    # start checking each file
    counters.setCounter('all',len(ids))
    ids_unhealth = []
    for id in ids:
     	# construct the full document path from the document ID
        infile = dochealthcheck_config.inputdir+idtopath(id['id'])
      
        # check if file exists
        if not os.path.exists(infile):
            infoprinter.printStatus('file exists','no')
            continue
        counters.addCounter('inrepo')
      
        # check file size in bytes
        statinfo = os.stat(infile)
        s = str(id['id'])+' '+str(statinfo.st_size)
        f_docsize.write(s+'\n')
          
        # check the file type
        cmd_file = 'file -i "'+infile+'"'
        cmdoutput = commands.getoutput(cmd_file)
	if verbose: print cmdoutput
      
        # check each accepted document, documents whose mimetypes are not
        # in the accepted mime types are identified as "unhealthy"
	healthy = False
	for am in dochealthcheck_config.accepted_mimes:
            if am in cmdoutput:
		healthy = True
		print 'document is healthy',id['id']
      	    	counters.addCounter('healthy')
                break
        if healthy:
	    continue

     	print "unhealthy document: ",id['id']
        # write unheathy document ID to output file
        f_unhealthdoc.write(str(id['id'])+'\n')
        ids_unhealth.append(id['id'])
      
       
        # delete file folder from repository
        if dochealthcheck_config.toggle_delete_from_repo:
	    infiledir = os.path.dirname(infile)
            cmd_repo = 'rm -rf '+infiledir
	    cmd_repo_output = commands.getoutput(cmd_repo)
	    if not os.path.exists(infiledir):
             	infoprinter.printStatus(cmd_repo,'OK')
	    else: 
		infoprinter.printStatus(cmd_repo,'FAIL')
		return
      
        # delete records from database
        if dochealthcheck_config.toggle_delete_from_db:
            # delete the record from database
            cmd_db = 'DELETE FROM '+dochealthcheck_config.dbt_name+' WHERE id='+str(id['id'])
            print cmd_db
            cursor.execute(cmd_db)
      
    # close filese
    f_docsize.close()
    f_unhealthdoc.close()

    # commit all transactions after looping over all documents
    if dochealthcheck_config.toggle_delete_from_db:
        transaction.commit_unless_managed()
     
    # print out counters
    counters.printCounter()

# convert ID to path
# e.g., 6871221-> 006/871/221/006.871.221.pdf
def idtopath(id):
    p1 = id / 1000000
    p2 = (id % 1000000) / 1000
    p3 = id % 1000
    s1 = str(p1).zfill(3)
    s2 = str(p2).zfill(3)
    s3 = str(p3).zfill(3)
    p = "%s/%s/%s/%s.%s.%s.pdf" % (s1, s2, s3, s1, s2, s3)
    return p

    
startup()
