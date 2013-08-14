#!/usr/local/bin/python2.6
# Delete selected URLs from parenturl table in the citeseerx_crawl database
#
# URLs maching the following conditions are deleted
# (1) URL domain is not in the blacklist file
# (2) There are zero url matching this parenturl in the main_crawl_document table
# (3) The URL is not alive anymore
# input: full path of blacklist file
# output: a single record of log file
# 
import os # define invironment variable
import sys 
import output   # defines writer
import logging 
import logging.handlers
import threading
import time
from datetime import datetime
import hashlib
#import config # Django configuration and parameters
import runconfig # log parser configuration
#from django.conf import settings
import settings
os.environ['DJANGO_SETTINGS_MODULE'] = runconfig.django_settings_module
from subprocess import call
import commands
import string 
import glob
import urllib
from urlparse import urlsplit
import logparsers # log parser module
#from pprint import pprint
from django.db import connection,transaction
import httplib
import urllib2
import csv
import socket
import counter
import printinfo
from operator import itemgetter, attrgetter
def startup():

  # define constant parameters
  datestr = datetime.now().strftime("%Y-%m-%d")
  output_file = 'whitelist.'+datestr+'.csv'
  socket.setdefaulttimeout(runconfig.whitelistgen['sockettimeout'])

  # create on-screen information print object
  infoprinter = printinfo.printInfo()
  # print database name
  infoprinter.printPara('DATABASE',settings.DATABASES['default']['NAME'])

  # generate other parameters
  headers = {"User-Agent":runconfig.whitelistgen['user_agent']}

  # create general log configers and config logs
  logconfiger = LogConfiger()
  logconfiger.config_loggers(runconfig.general_logs)

  # create document logger
  doclogger = DocLogger()

  # load the blacklist and remove trailing '\n' 
  blacklisturlstrail = file(runconfig.blacklistfile).readlines()
  blacklisturls = []
  for url in blacklisturlstrail: blacklisturls.append(url.strip('\n'))

  # load domain blacklist file and remove the trailing '\n'
  domainblacklisttrail = file(runconfig.domainblacklistfile).readlines()
  domainblacklisturls = []
  for durl in domainblacklisttrail: domainblacklisturls.append(durl.strip('\n'))

  # number counter
  counters = counter.Counter()
  counters.newCounter('all')
  # number of accepted URLs
  counters.newCounter('accepted')

  # ids includes main_crawl_parenturl ID and main_crawl_submission ID
  # dbtable = 'parenturl' or 'submission'
  # URL includes urls from main_crawl_parenturl and main_crawl_submission
  ids = []
  dbtable = []
  urls = [] 

  # retrieve all parent urls 
  cursor = connection.cursor()
  dbquery = 'SELECT id,url FROM main_crawl_parenturl;'
  #infoprinter.printStatus(dbquery,'running')
  cursor.execute(dbquery)
  rows = cursor.fetchall()
  for row in rows:
    ids.append(row[0])
    dbtable.append('parenturl')
    urls.append(row[1])
  #infoprinter.printStatus(dbquery,'OK')
  infoprinter.printPara('#parentURLs',str(len(ids)))

  # retrieve all submitted urls 
  dbquery = 'SELECT id,url FROM main_crawl_submission;'
  #infoprinter.printStatus(dbquery,'running')
  cursor.execute(dbquery)
  rows = cursor.fetchall()
  #infoprinter.printStatus(dbquery,'OK')
  for row in rows:
    ids.append(row[0])
    dbtable.append('submission')
    urls.append(row[1])
  infoprinter.printPara('#submittedURLs',str(len(ids)))
  
  infoprinter.printPara('TOTAL #candidate urls: ',str(len(ids)))
  counters.setCounter('all',len(ids))

  # output result into result file
  recordWriter = csv.writer(open(output_file,'wb'),delimiter=',', \
                            quotechar='"',quoting=csv.QUOTE_NONE)
  
  #print 'id range: ',ids[0],ids[-1]
  # loop over all urls in the parent url list and check the following
  # requirements
  # (1) if the url host matches any host names in the blacklist
  # (2) if there's any document urls found in the main_crawl_document table
  # (3) if the url is currently alive
  # (4) write into log 
  # (5) write into output file, which should contain the following fields
  #     (*) id of this parent url in the main_crawl_parenturl table
  #     (*) number of document url found from main_crawl_document table
  #	(*) host name of this parent URL string
  #     (*) parent url string
  #     fields are enclosed by double quotes and separated by commas
  # 
  # The log contains the following fields (log will slow down the
  # process and should be disabled after debugging and testing)
  # (1) id of this parent url in the main_crawl_parenturl table
  # (2) host name of this parent url
  # (3) weather this parent url host matches any urls in the blacklist
  # (4) number of document urls found from main_crawl_document table
  # (5) if the url is currently alive
  # (6) parent url string
  # 
  record_tuples = [] # the sorted results 
  n_documenturls = [] # the number of documents
  for id,dbt,url in zip(ids,dbtable,urls):
    print ''
    print '%(#)9s - %(s)9s - %(url)s' % {'#':id,'s':dbt,'url':url}
    scheme,host,path,query,fragment = urlsplit(url)
 
    # check the url structure: must be complete
    if (not scheme) or (not host):
      infoprinter.printStatus('complete url','no')
      continue

    # generate the "parent_url" object for logging and final output
    # parent_url.url_is_alive = 0
    # parent_url.n_documenturl = 0
    # parent_url.pass_blacklist_check = 1
    parent_url = ParentUrl(id,dbt,url,0,0,host,1)

    # CHECK A0: if it matches any domain in the domain blacklist
    for durl in domainblacklisturls:
     	if host.endswith(durl): 
	    parent_url.pass_blacklist_check = 0
	    break
    if not parent_url.pass_blacklist_check:
	infoprinter.printStatus('Blacklist check','fail')
        # save into log
        msg = doclogger.generator_url(parent_url)
        logging.getLogger('whitelistgen').info(msg)
	continue

    # CHECK A1: if it matches any host in the blacklist
    if host in blacklisturls: 
      infoprinter.printStatus('Blacklist check','fail')
      parent_url.pass_blacklist_check = 0
      # save into log
      msg = doclogger.generator_url(parent_url)
      logging.getLogger('whitelistgen').info(msg)
      continue

    # Pass domain blacklist check
    infoprinter.printStatus('Blacklist check','pass')

    # CHECK B: number of document urls found in the main_crawl_document table
    # we only check URLs in the parenturl table. 
    if dbt == 'parenturl':
        dbquery = """SELECT id FROM main_crawl_parenturl WHERE url=%s""" 

        #infoprinter.printStatus(dbquery,'running')
	cursor.execute(dbquery, (url,)) 
        rows = cursor.fetchall()
        parentid = rows[0][0]
        infoprinter.printStatus(dbquery,'ok')
    
        dbquery = 'SELECT count(*) FROM main_crawl_document WHERE state=1 and parent_id = %(#)s' % {'#':parentid}
        #infoprinter.printStatus(dbquery,'running')
        cursor.execute(dbquery)
        rows = cursor.fetchall()
        parent_url.n_documenturl = rows[0][0]
        infoprinter.printStatus(dbquery,'ok')
        if not parent_url.n_documenturl:
          infoprinter.printStatus('Ingestable document links check','fail')
          # save into log
          msg = doclogger.generator_url(parent_url)
          logging.getLogger('whitelistgen').info(msg)
          continue
        else:
          infoprinter.printStatus('Ingestable document links check','pass')
    else:
	parent_url.n_documenturl = 9999
	infoprinter.printStatus('User submitted URL','yes')
	infoprinter.printStatus('Ingestable document links check','pass')
    
    # CHECK C: url is alive
    if runconfig.whitelistgen['checkurlalive']:
	infoprinter.printStatus('Check URL is alive','running')
    	parent_url.url_is_alive = checkURLalive(url)
    else: #assume URL is alive if do not check
    	parent_url.url_is_alive = 1

    if not parent_url.url_is_alive:
	infoprinter.printStatus('Check URL is alive','fail')
        # save into log
        msg = doclogger.generator_url(parent_url)
        logging.getLogger('whitelistgen').info(msg)
      	continue
    else:
	infoprinter.printStatus('Check URL is alive','pass')
      
    # save into log
    msg = doclogger.generator_url(parent_url)
    logging.getLogger('whitelistgen').info(msg)

    # save selected urls into a tuple list before sorting them
    if (parent_url.pass_blacklist_check) and parent_url.url_is_alive and parent_url.n_documenturl:
      record_tuple = (id,dbt,url,parent_url.url_is_alive,\
			parent_url.n_documenturl,host,parent_url.pass_blacklist_check)
      record_tuples.append(record_tuple)
      infoprinter.printStatus('URL included in whitelist','yes')
      counters.addCounter('accepted')

  # sort results by the number of documents, user submitted documents are at the top
  record_tuples_sort = sorted(record_tuples, key=itemgetter(4),reverse=True)
  for r in record_tuples_sort:
    parent_url = ParentUrl(r[0],r[1],r[2],r[3],r[4],r[5],r[6])
    record = doclogger.generator_record(parent_url)
    recordWriter.writerow(record)
  
  # print counters
  counters.printCounter()

def checkURLalive(url,user_agent='citeseerxbot'):
  headers = {"User-Agent":user_agent}
  req = urllib2.Request(url,None,headers)
  try:
    f = urllib2.urlopen(req)
    return 1
  except httplib.HTTPException,e:
    print 'httplib.HTTPException',str(e)
    return 0
  except urllib2.URLError,e:
    print 'URLError',str(e)
    return 0
  except socket.timeout:
    print 'socket.timeout'
    return 0
  except socket.error,e:
    print 'socket.error'
    return 0
  except IOError,e:
    print 'IOError',str(e)
    return 0

# check if a url exist by downloading the header
def checkURLalive2(url):
  host,path = urlparse.urlparse(url)[1:3]
  try:
    conn = httplib.HTTPConnection(host)
    conn.request('HEAD',path)
    res  = con.getresponse()
    return res.status == 200
  except StandardError:
    print 'checkURLalive2: unexpected error'
    return False
  
def id_to_fname(id,ext):
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

# add a counter and add or subtract numbers from these counters
class Counter(object):
  def __init__(self):
    self.all  = 0
 
  # set counter values
  def setCounter(self,countername,number=0):
    setattr(self,countername,number)

  # create a new counter
  def newCounter(self,countername):
    setattr(self,countername,0)

  # add a number to a counter (1 by default)
  def addCounter(self,countername,number=1):
    countervalue = getattr(self,countername)
    setattr(self,countername,countervalue+number)
    return getattr(self,countername)
 
  # subtract a number to a counter (1 by default)
  def subCounter(self,countername,number=1):
    countervalue = getattr(self,countername)
    setattr(self,countername,countervalue+number)
    return getattr(self,countername)

class LogConfiger(object):
  def __init__(self):
    self.log_dir = runconfig.cdilogdir
    # if log directory does not exist, create one
    if not os.path.exists(self.log_dir):
      os.makedirs(self.log_dir)
  
  # general_logs are log file names without ".log"
  # example, if you want the log file name to be "document.log" and "url.log"
  # general_logs = ['document','url']
  # root.log always exists
  def config_loggers(self,general_logs):
    rotating_interval = 'D'

    # root logger
    logger = logging.getLogger('')
    logger.setLevel(logging.WARNING)
    log_file = os.path.join(self.log_dir, 'root.log')
    h = logging.FileHandler(log_file)
    formatter = logging.Formatter("%(asctime)s - %(levelname)s - %(message)s")
    h.setFormatter(formatter)
    logger.addHandler(h)

    # document is for the exporter
    # delurl.url logs urls matched to the blacklist
    # delurl.bl logs hosts in the blacklist
    for name in general_logs:
      logger = logging.getLogger(name)
      logger.setLevel(logging.INFO)
      logger.propagate = False
      log_file = os.path.join(self.log_dir, name + '.log')
      h = logging.handlers.TimedRotatingFileHandler(log_file, rotating_interval)
      formatter = logging.Formatter("%(asctime)s - %(message)s")
      h.setFormatter(formatter)
      logger.addHandler(h)

# generate document log content
class DocLogger(object):
  def __init__(self):
    pass

  # generate log from input URL object
  def generator_url(self,url):
    msg = '%10s %10s %1s %5s %s %s %s' % (url.id,url.dbt,url.pass_blacklist_check,url.n_documenturl,url.url_is_alive,url.host,url.url)
    return msg

  # generate log from blacklist
  def generator_bl(self,matched,deleted,host):
    msg = '%10s %10s %s' % (matched,deleted,host)
    return msg

  # generate log from record object
  # note that url.id could be the id in the citeseerx_crawl.parenturl table or
  # citeseerx_crawl.submission table (if n_documenturl = 9999)
  def generator_record(self,url):
    idstr = str(url.id)
    msg = [url.id,url.n_documenturl,url.host,url.url]
    return msg

# define the parent url object, similar to the Source object defined in 
# source.py
class ParentUrl(object):
  def __init__(self,id,dbt,url,url_is_alive,n_documenturl,host,pass_blacklist_check):
    self.id = id
    self.dbt = dbt
    self.url = url
    self.url_is_alive = url_is_alive
    self.n_documenturl = n_documenturl
    self.host = host
    self.pass_blacklist_check = pass_blacklist_check

startup()
