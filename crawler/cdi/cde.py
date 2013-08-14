#!/usr/local/bin/python2.6
# Crawl Document Exporter 
# Python code to export the crawling documents from the crawl repository out
# 
# locate the input file and read the entire log file
# input: a MySQL database query
# output: a directory which contains the queried documents in hierachical order
#	(or just in a batch)
# e.g., 002/123/234/002.123.234.pdf
#
import os # define invironment variable
import sys 
import resource # define resource variable "r"
import output   # defines writer
import logging 
import logging.handlers
import threading
import time
import datetime
import hashlib
import runconfig # log parser configuration
#from django.conf import settings
import settings
os.environ['DJANGO_SETTINGS_MODULE'] = runconfig.django_settings_module
from subprocess import call
import filter_doc
import commands
import string 
import glob
import urllib
import logparsers # log parser module
import counter 
import textextract
import crawldb
import printinfo
import shutil
from exception import BadResourceError
# check configurations, including the following items
# (*) permission to write into the output folder
#     This is checked by creating and delete a folder called "9999" 
#     inside the repository folder.
# 
def checkConfig():
    infoprtr = printinfo.printInfo()

    # crawl repository exists
    if not os.path.exists(runconfig.crawlrepo):
 	infoprtr.printStatus('crawlrepo exists','no')
	return False
    else:
	infoprtr.printStatus('crawlrepo exists','yes')
    
    # permission to write into the output folder 
    testdir = os.path.join(runconfig.cde["outputdir"],'9999')
    if os.path.exists(testdir):
	shutil.rmtree(testdir)
    try:
	os.makedirs(testdir)
	shutil.rmtree(testdir)
    except OSError,e:
	print e
	infoprtr.printStatus('Write permission to outputdir','no')
	return False
    
    # if it passes all configuration checks
    return True

def startup():

  # record start time 
  tic = time.time()

  # create on-screen information print object
  infoprinter = printinfo.printInfo()

  # check configurations
  if not checkConfig():
    infoprinter.printStatus('Configuration check','fail')
    raise SystemExit("Change your configurations in runconfig.py")
  else:
    infoprinter.printStatus('Configuration check','ok')

  # create exporter
  exporter = output.CiteSeerExporter([runconfig.cde["outputdir"],runconfig.crawlrepo])

  # create crawldb
  cdb = crawldb.CrawlDB()

  # create general log configers and config logs
  logconfiger = Log_Configer()
  logconfiger.config_loggers()

  # process DB query, raise error if ids is empty
  dbquery = runconfig.cde["dbquery"]
  ids = cdb.queryDocID(dbquery)
  infoprinter.printPara('#docid',str(len(ids)))
  if not ids:
      infoprinter.printStatus('DB query','fail')
      os.exit()

  # number counter
  counters = counter.Counter()
  counters.newCounter('all')
  counters.setCounter('all',len(ids))
  counters.newCounter('copied') 

  # export each queried document
  if runconfig.cde["toggle_export"]:
      i = 0
      for id in ids:
	  i = i + 1
	  print "%9d/%-9d : %9d" % (i,counters.all,id)
          if exporter.doc_export(id): 
	  	counters.addCounter('copied')
      	  else:
	  	infoprinter.printStatus(str(id),'fail')

    # log successful
    # check repository to see if output PDF files are there
    #msg = doclogger.generator('saved_New',infile,r)
    #logging.getLogger('document').info(msg)
    #infoprinter.printStatus('Document saved','yes')
    # number of documents which are written into db
    #counters.addCounter('saved_New')
        
  counters.printCounter()
  counters.printCountertoFile(runconfig.cde["summaryfile"])

  # record end time to calculate processing time
  # because strftime() will truncate the time string when converting to the
  # user-defined time format, we add "1" second to compensate this loss. 
  toc = time.time()
  processingtime = time.strftime('%H:%M:%S',time.gmtime(toc-tic+1))
  infoprinter.printPara('Processing time: ',processingtime)

def id_to_fname(id,ext):
    # if extention (ext) is not provided, use "pdf"
    try: ext
    except NameError: ext = 'pdf'
    p1 = id / 1000000
    p2 = (id % 1000000) / 1000
    p3 = id % 1000
    s1 = str(p1).zfill(3)
    s2 = str(p2).zfill(3)
    s3 = str(p3).zfill(3)
    p = "%s.%s.%s.%s" % (s1, s2, s3, ext)
    return os.path.join(p)

def create_instance(config_str,params):
    try:
        segments = config_str.strip().split('::')
        module_name = '.'.join(segments[0].split('.')[:-1])
        class_name = segments[0].split('.')[-1]

        #params = segments[1:]

        __import__(module_name)
        module = sys.modules[module_name]

        obj = module.__dict__[class_name](params)
        return obj
    except ValueError:
        logging.critical('Invalid config: %s' % config_str)
        return None

   
class Mime_Type_Filter(object):
  def __init__(self,allow_doc_type):
    # default mimetype 
    self.doctype = 'unknown'     #final document type
    # if no document types specified (allow_doc_type), use pdf/postscript by default 
    try: allow_doc_type
    except NameError:
      allow_doc_type = ['application/pdf','application/postscript']
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
        # print mime type if set
        break
 
    # extract extension of the retrieved file
    # The original URL is first split by "/". The file name is the last element
    # If the file name contains two extensions, use both of them
    # e.g., example.pdf.Z -> self.ext = 'pdf.Z'
    # But this extension is not used, in the main program. 
    # It maybe used for other purposes in the future.
    paths = r.path.split("/")
    filename = paths[-1] # file name is the last element 
    if filename != '': 
      filenames = filename.split(".")
      filenameslen = len(filenames)
      fileexts = ''
      if filenameslen >= 2:
        # but how to deal with part1.part2.ext1.ext2?
        fileexts = filenames[1:filenameslen] #filename.pdf.Z->['pdf','Z']
        self.ext = ".".join(fileexts)
   
    return checktyperesult

class Log_Configer(object):
  def __init__(self):
    self.log_dir = 'log/'
    # if log directory does not exist, create one
    if not os.path.exists(self.log_dir):
      os.makedirs(self.log_dir)
  
  def config_loggers(self):
    rotating_interval = 'D'

    # root logger
    logger = logging.getLogger('')
    logger.setLevel(logging.WARNING)
    log_file = os.path.join(self.log_dir, 'root.log')
    h = logging.FileHandler(log_file)
    formatter = logging.Formatter("%(asctime)s - %(levelname)s - %(message)s")
    h.setFormatter(formatter)
    logger.addHandler(h)

    for name in ['document']:
      logger = logging.getLogger(name)
      logger.setLevel(logging.INFO)
      logger.propagate = False
      log_file = os.path.join(self.log_dir, name + '.log')
      h = logging.handlers.TimedRotatingFileHandler(log_file, rotating_interval)
      formatter = logging.Formatter("%(asctime)s - %(message)s")
      h.setFormatter(formatter)
      logger.addHandler(h)

class Doc_Logger(object):
# used to generate document log content
  def __init__(self,hostname,mimetypefilter):
      self.hostname = hostname
      self._doc_type_filter_cp = mimetypefilter

  def generator(self,flag,filepath,r):
    msg = '%20s %10s %s %s %s %s' % (flag,self._doc_type_filter_cp.doctype,\
                                r.crawl_date,r.url,self.hostname,filepath)
    return msg

startup()

