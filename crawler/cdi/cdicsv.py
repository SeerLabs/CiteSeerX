#!/usr/bin/python
# Crawl Document Importer
# Python code to export the crawling results to the crawl database
# The necessary data including full document path, urls, parenturls, 
# crawl time, hop, and content-types are written into a a csv file
# How to use it
# * Edit configuration file: conf.py on section Config_cdicsv and Config_global
# * Edit settings.py to configure database
# $ python cdicsv.py [-j jobid] [-d] [csvfile] 
# [-j jobid] : optional, specify job id
# [-d] : optional, run in debugging mode
# [csvfile] : required, input csv file (see convert_mkcrawler_csv.py for cols) 
import os # define invironment variable
import sys 
import django
import resource # define resource variable "r"
import output   # defines writer
import logging 
import time
import datetime
import hashlib
from conf import Config_global
from conf import Config_cdicsv
import settings
os.environ['DJANGO_SETTINGS_MODULE'] = Config_global.django_settings_module
import urllib
import counter 
import crawldb
import urlfilters
from exception import BadResourceError
import _mysql_exceptions
from datetimelib_jwu import getcurrentdatetime_trim,getcurrentdatetime_db
from optparse import OptionParser
import argparse
from printlib import print_prog
from filelib import load_cdicsv

def main(confc):

    start = time.time()
    logger = logging.getLogger("main")

    # global configurations
    config = Config_global(confc["jobid"],skip_check=True)
    config_cdicsv = Config_cdicsv()

    # create document writer
    writer = output.CiteSeerWriter([config.crawlrepo,config_cdicsv.crawler])
  
    # create URL filter
    urlfilter = urlfilters.URLFilter(blacklistfile=config.blacklistfile,domainblacklistfile=config.domainblacklistfile)
  
    # create document type filter
    mimetypefilter = Mime_Type_Filter(config.mtypes)
  
    # create document logger (for this middleware)
    doclogger = Doc_Logger(os.getenv('HOSTNAME'),mimetypefilter)
  
    # parse csv file
    logger.info('parsing csv file...')
    gs = load_cdicsv(confc["csv_file"])
  
    # number counter
    counters = counter.Counter()
    counters.newCounter('all')
    counters.setCounter('all',len(gs))
    counters.newCounter('saved_New') 
    counters.newCounter('saved_Duplicate')
    counters.newCounter('saved_bitDuplicate')   # bitwise duplicate
    counters.newCounter('filtered_all')
    counters.newCounter('filtered_URLFilter')
    counters.newCounter('filtered_MimetypeFilter')
    counters.newCounter('failed_all')
    counters.newCounter('failed_FileNotFound')    # if inputs are pdf/ps
    counters.newCounter('failed_PDFFileNotFound') # if inputs are gzipped
    counters.newCounter('failed_BadURL') 	# Bad URL
    counters.newCounter('failed_SaveError')	# errors when saving docs
  
    # if required to visit database, check that tables are created
    if config_cdicsv.save_toDB:
        cdb = crawldb.CrawlDB()
        logger.info("database: "+cdb.dbname)
        # create document and parent table if they do not exist
        cdb.createTables()
  
    # loop over each document from csv file
    doci = 0
    max_length = len(str(len(gs)))
    for g in gs:
        accepted = False
        doci += 1
  
        # get resource variable 
        is_seed = True if g["hop"] == 0 else False
        try:
            r = resource.Resource(None, g['parenturl'],g['url'],\
                is_seed,g['hop'],batch=0,anchor_text="")
        except TypeError,e:
            logger.error(e)
            os.exit(1)
        except BadResourceError,e:
            logger.error("Error parsing Url : "+g["url"])
  	    counters.addCounter('failed_BadURL')
  	    continue

        # apply URL filter
        if config_cdicsv.apply_urlFilter:
      	    if not urlfilter.check(r.url,apply_blacklistrule=False):
  	        msg = "%s %s %s"%('URLRejected',urlfilter.rejectreason,r.url)
                logger.debug(msg)
                counters.addCounter('filtered_URLFilter')
                continue
  
        r.crawl_date = g["datetime"]
        r.content_type = g["content_type"]
        logger.debug('mime-type : '+r.content_type)

        # retrieve local copies of document
        infile = g["fullpath"]
        logger.debug("fullpath : %(1)s" % {"1":infile})

        # apply doctype_filter, which checks document mimetype type
        mimetypefilter_ok = mimetypefilter.check(r)
        if not mimetypefilter_ok: 
            msg = doclogger.generator('DocumentTypeNotAccepted',infile,r)
            logger.debug(msg)
            counters.addCounter('filtered_MimetypeFilter')
            continue
        else:
            logger.debug("%(1)8d/%(2)-8d Document type: accepted" % {"1":doci,"2":counters.all})

        # check if document URL is already in db
        # if not, add it into db; if yes, skip it
        # However, if the overwrite_file toggle is set, write to db anyway
        if config_cdicsv.save_toDB:
            recordExist = cdb.checkRecord(config.document_table,md5=r.md5)
  	    if not recordExist:
                logger.debug("New document")
            else:
      	        msg = doclogger.generator('saved_Duplicate',infile,r)
                logger.debug(msg)
  	        counters.addCounter('saved_Duplicate')
  	        if not config_cdicsv.overwrite_file:
      	            continue
   
        inpdf = infile
        logger.debug("Document file found: %(1)s" % {"1":inpdf})
        
        # write document information into database
        # database settings can be found at settings.py
        # read file content and calculate SHA1 
        try:
            f = open(inpdf,'r')
            data = f.read()
            f.close()
        except IOError:
            msg = doclogger.generator('FileNotFound',infile,r)
            logger.error(msg)
            counters.addCounter('failed_FileNotFound')
            if config_cdicsv.ignore_nonexist:
                continue
            else:
                sys.exit(-1)

        # if require to save to db,calculate SHA1
        if config_cdicsv.save_toDB:
            r.content_sha1 = hashlib.sha1(data).hexdigest() 
            try:
                # save to db, save doc and metadata
                dup_bit = writer.save2(r,data) 
            except IOError,e:
                msg = doclogger.generator('IOErrorSave',infile,r)
                logger.debug(msg)
    	        counters.addCounter('failed_SaveError')
    	        continue
            except OSError,e:
                msg = doclogger.generator('OSErrorSave',infile,r)
                logger.debug(msg)
    	        counters.addCounter('failed_SaveError')
    	        continue
            except _mysql_exceptions.Warning,e:
    	        msg = doclogger.generator("Error",infile,r)
                logger.debug(msg)
    	        counters.addCounter('failed_SaveError')
    	        continue
            except django.db.utils.IntegrityError,e:
    	        msg = doclogger.generator("IntegrityError",infile,r)
                logger.debug(msg)
    	        counters.addCounter('failed_SaveError')
    	        continue
    
            if dup_bit:
                counters.addCounter("saved_bitDuplicate")

        # log successful
        # check repository to see if output PDF files are there
        msg = doclogger.generator('saved_New',infile,r)
        logger.debug(msg)
        logger.info("[{1:{0}}/{2:{0}}] Saved : {3}".format(max_length,doci,counters.all,infile))
        counters.addCounter('saved_New')
        accepted = True
        
    # print counters in a new line
    counters.setCounter('filtered',counters.filtered_MimetypeFilter+\
                                   counters.filtered_URLFilter)
    counters.setCounter('failed',counters.failed_BadURL+\
                                 counters.failed_FileNotFound+\
                                 counters.failed_PDFFileNotFound+\
                                 counters.failed_SaveError)
    counters.printCounter()

    # record end time to calculate processing time
    # because strftime() will truncate the time string when converting to the
    # user-defined time format, we add "1" second to compensate this loss. 
    toc = time.time()
    processingtime = time.strftime('%H:%M:%S',time.gmtime(toc-start+1))
    logger.info('Processing time : %(1)s'% {"1":processingtime})
    logger.info("CSV file : %(1)s"%{"1":confc["csv_file"]})
    logger.info("Crawl repository : %(1)s"%{"1":config.crawlrepo})
    logger.info('End')
    
class Mime_Type_Filter(object):
  def __init__(self,allow_doc_type):
    # default mimetype 
    self.doctype = 'unknown'     #final document type
    # use pdf by default 
    try: allow_doc_type
    except NameError:
        allow_doc_type = ['application/pdf']
    self.allowtype = allow_doc_type

    # extension of original link file
    self.ext = '' 

    # some website may use "bit stream" so that the content type is 
    # "octet-streamm". However, we cannot distinguish for sure if this is pdf 
    # These documents need to be crawled separated with special settings
    # mime type information is printed on the screen by default
    # if verbose is set to False, mime type information is not printed
    # 
  def check(self,r):
    checktyperesult = False
    self.doctype = r.content_type
    for elem in self.allowtype:
      if elem in r.content_type:
	# mime type accepted
        checktyperesult = True
        break
    return checktyperesult


class Doc_Logger(object):
# used to generate document log content
  def __init__(self,hostname,mimetypefilter):
      self.hostname = hostname
      self._doc_type_filter_cp = mimetypefilter

  def generator(self,flag,filepath,r):
    msg = '%25s %20s %10s %s %s %s %s' % (getcurrentdatetime_db(),flag,self._doc_type_filter_cp.doctype,\
                                r.crawl_date,r.url,self.hostname,filepath)
    return msg

# You can input a string as the "jobid", if you do not input anything,
# the program uses the current datetime as the "jobid"
if __name__=="__main__":
    # accept input parameters
    parser = argparse.ArgumentParser()
    #parser = OptionParser()
    parser.add_argument("-v","--verbose",dest="verbose",\
                        action="store_true",default=False,\
                        help="Set the logging mode to logging.DEBUG")
    parser.add_argument("-j","--jobid",dest="jobid",\
                        help="specify job ID",metavar="JOB ID",\
                       default=getcurrentdatetime_trim())
    parser.add_argument("csv_file",type=str,metavar="CSV FILE",\
                       help="CSV file compatible with importing format")
    args = parser.parse_args()
    logging_level = logging.DEBUG if args.verbose else logging.INFO
    try:
        csv_file = args[0]
    except:
        print "csv file required as an argument."

   # logging configurations
    logging.basicConfig(level=logging_level,\
                   filename="logs/cdicsv-"+args.jobid+".log",\
                   format="%(asctime)s %(name)-8s %(levelname)-8s %(message)s",
                   mode="w")
    console = logging.StreamHandler()
    console.setLevel(logging_level)
    formatter = logging.Formatter("%(name)-20s %(levelname)-8s %(message)s")
    console.setFormatter(formatter)
    logging.getLogger("").addHandler(console)

    logging.info("logging configuration done")

    # pass command line configuration to main program
    confc = {"jobid":args.jobid,"csv_file":args.csv_file}

    main(confc)
