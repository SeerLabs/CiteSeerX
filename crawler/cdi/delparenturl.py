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
import datetime
import hashlib
import config # Django configuration and parameters
import runconfig # log parser configuration
from django.conf import settings
os.environ['DJANGO_SETTINGS_MODULE'] = runconfig.django_settings_module
from subprocess import call
import commands
import string 
import glob
import urllib
from urlparse import urlsplit
import logparsers # log parser module
from pprint import pprint
from django.db import connection,transaction
import httplib
import urllib2
import csv
import socket
from operator import itemgetter, attrgetter
def startup():

  # define constant parameters
  config_file = 'cde.conf'
  user_agent = 'citeseerxbot'
  record_file = 'parenturl.revised.v2.csv'
  socket.setdefaulttimeout(10)

  # generate other parameters
  headers = {"User-Agent":user_agent}

  # load parameters from configuration file
  conf = config.Config()
  conf.load(config_file)

  print "settings.DATABASE_NAME = ",settings.DATABASE_NAME

  # log configer
  # if do not create logconfiger here, log files will not be created
  logconfiger = LogConfiger()
  logconfiger.config_loggers()

  # document log generator
  doclogger = DocLogger()

  # load the blacklist
  blacklist = file(conf.blacklistfile).readlines()

  # number counter
  counter_url = Counter()
  # number of URLs that are deleted from database table
  counter_url.newCounter('accepted')

  # retrieve all parent urls from database
  cursor = connection.cursor()
  #dbquery = 'SELECT id,url FROM main_crawl_whitelist2 ORDER BY id;'
  dbquery = 'SELECT id,url FROM main_crawl_whitelist2 ORDER BY id;'
  print dbquery
  cursor.execute(dbquery)
  ids = []
  urls = [] 
  rows = cursor.fetchall()
  for row in rows:
    ids.append(row[0])
    urls.append(row[1])

  print ' >>> records uploaded: ',len(ids)
  # prepare to output result into result file
  recordWriter = csv.writer(open(record_file,'wb'),delimiter=',', \
                            quotechar='"',quoting=csv.QUOTE_NONE)
  
  print 'id range: ',ids[0],ids[-1]
  # loop over all urls in the parent url list and check the following
  # requirements
  # (1) if the url host matches any host names in the blacklist
  # (2) if there's any document urls found in the main_crawl_document table
  # (3) if the url is currently alive
  # (4) write into log 
  # (5) write into output file 'parenturl_revised.csv'
  #     file should contain the following fields
  #     (*) id of this parent url in the main_crawl_parenturl table
  #     (*) host name of this parent url
  #     (*) number of document url found from main_crawl_document table
  #     (*) parent url string
  #     fields are enclosed by double quotes and separated by commas
  # 
  # The log has to contain the following records
  # (1) id of this parent url in the main_crawl_parenturl table
  # (2) host name of this parent url
  # (3) weather this parent url host matches any urls in the blacklist
  # (4) number of document url found from main_crawl_document table
  # (5) if the url is currently alive
  # (6) parent url string
  # 
  record_tuples = [] # the sorted results 
  n_documenturls = [] # the number of documents
  for id,url in zip(ids,urls):
    print ''
    print '>>> processing %(#)s: %(url)s' % {'#':id,'url':url}
    # initialize flags
    host_in_blacklist = -1
    n_documenturl = -1
    url_is_alive = -1

    scheme,host,path,query,fragment = urlsplit(url)
 
    # check the url structure: must be complete
    if (not scheme) or (not host):
      print '    incomplete url component'
      continue

    # CHECK A: if it matches any host in the blacklist
    if host in blacklist: 
      host_in_blacklist = 1
      #print '    check 1/3 host_in_blacklist: Fail'
      continue
    else: 
      host_in_blacklist = 0
      #print '    check 1/3 host_in_blacklist: Pass'

    # CHECK B: number of document urls found in the main_crawl_document table
    #dbquery = 'SELECT count(*) FROM main_crawl_document WHERE state=1 and parent_id = %(#)s'% {'#':id}
    dbquery = 'select id from main_crawl_parenturl where url like "%(#)s"' % {'#':url}
    print dbquery
    cursor.execute(dbquery)
    rows = cursor.fetchall()
    parentid = rows[0][0]

    dbquery = 'SELECT count(*) FROM main_crawl_document WHERE state=1 and parent_id = %(#)s' % {'#':parentid}
    print dbquery
    #dbquery = "SELECT count(*) FROM main_crawl_document w JOIN main_crawl_parenturl p ON w.parent_id = p.id where w.state=1 and p.url=%(#)s;" % {'#':url}

    cursor.execute(dbquery)
    rows = cursor.fetchall()
    n_documenturl = rows[0][0]
    if not n_documenturl:
      #print '    check 2/3 n_documenturl: Fail. '
      continue
    #else:
      #print '    check 2/3 n_documenturl: Pass. n_documenturl = ',n_documenturl
    
    # CHECK C: url is alive
    #url_is_alive = checkURLalive(url)
    url_is_alive = 1
    #if not url_is_alive:
    #  print '    check 3/3 url existance: Fail'
    #  continue
    #else:
    #  print '    check 3/3 url existance: Pass'
      
    # generate the "parent_url" object 
    parent_url = ParentUrl(id,url,url_is_alive,n_documenturl,host,host_in_blacklist)

    # save into log
    #msg = doclogger.generator_url(parent_url)
    #logging.getLogger('delparenturl.url').info(msg)

    # save selected urls into result file
    if (not host_in_blacklist) and url_is_alive and n_documenturl:
      record_tuple = (id,url,url_is_alive,n_documenturl,host,host_in_blacklist)
      record_tuples.append(record_tuple)
      #print '    parent url accepted'
      counter_url.addCounter('accepted')

  # sort results by the number of documents
  record_tuples_sort = sorted(record_tuples, key=itemgetter(3),reverse=True)
  for rts in record_tuples_sort:
    parent_url = ParentUrl(rts[0],rts[1],rts[2],rts[3],rts[4],rts[5])
    record = doclogger.generator_record(parent_url)
    recordWriter.writerow(record)
  
  # print counters
  print 'counter_url:'
  pprint (vars(counter_url)) 

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

    # document is for the exporter
    # delurl.url logs urls matched to the blacklist
    # delurl.bl logs hosts in the blacklist
    for name in ['document','delurl.url','delurl.bl']:
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

  def generator_url(self,url):
    msg = '%10s %1s %5s %s %s %s' % (url.id,url.host_in_blacklist,url.n_documenturl,url.url_is_alive,url.host,url.url)
    return msg

  def generator_bl(self,matched,deleted,host):
    msg = '%10s %10s %s' % (matched,deleted,host)
    return msg

  def generator_record(self,url):
    idstr = str(url.id)
    msg = [url.id,url.host_in_blacklist,url.n_documenturl,\
           url.url_is_alive,url.host,url.url]
    return msg

# define the parent url object, similar to the Source object defined in 
# source.py
class ParentUrl(object):
  def __init__(self,id,url,url_is_alive,n_documenturl,host,host_in_blacklist):
    self.id = id
    self.url = url
    self.url_is_alive = url_is_alive
    self.n_documenturl = n_documenturl
    self.host = host
    self.host_in_blacklist = host_in_blacklist

startup()
