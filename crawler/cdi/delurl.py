#!/usr/local/bin/python2.6
# Delete selected URLs from database records
#
# These URLs are selected by matching all URLs to a host list
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
import logparsers # log parser module
from pprint import pprint
from django.db import connection,transaction
def startup():

  # load configuration file
  config_file = 'cde.conf'

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
  counter_bl = Counter()
  counter_url = Counter()
  # number of hosts in blacklist that have corresponding URLs found 
  counter_bl.newCounter('matched') 
  counter_bl.setCounter('all',len(blacklist))
  # number of URLs that are deleted from database table
  counter_url.newCounter('deleted')

  # loop over each blacklist host do the following tasks
  # (1) select from database all urls that match the given host name
  # (2) check their state 
  # (3) delete the ones whose state=-1 (failed ingested)
  # (4) write into log 
  for idx,host in enumerate(blacklist):

    host = host.strip() # remove the trailing "\n"
    dbquery = "SELECT id,url,state FROM "+conf.dbt_document+" WHERE host = '"+host+"';"
    print '%(idx)s/%(#)s processing: %(host)s' % {'idx':idx+1,'#':len(blacklist),'host':host}
    #print dbquery
    cursor = connection.cursor()
    cursor.execute(dbquery)
    rows = cursor.fetchall()
  
    # if query results zero results, just log it 
    if not rows:
      msg = doclogger.generator_bl(0,0,host)
      logging.getLogger('delurl.bl').info(msg)
      continue
    else:
      counter_url.addCounter('all',len(rows))
      counter_bl.addCounter('matched')

    # parse query results to obtain dates
    # query results is a big tuple containing lots of small tuples
    ids = []
    urls = []
    states = []
    for row in rows:
      ids.append(row[0])
      urls.append(row[1])
      states.append(row[2])

    # loop over each url and check state
    ndelurl = 0
    for id,url,state in zip(ids,urls,states):
      if state == -1:
        # flag this url, deleted
        flag = 'deleted'
        counter_url.addCounter('deleted')
        
        # delete this url from database, dbt is the database table name
        dbdel = 'DELETE FROM '+conf.dbt_document+' WHERE id='+str(id)
        print dbdel
        cursor.execute(dbdel)
        transaction.commit_unless_managed()
        ndelurl = ndelurl + 1
      else:
        # keep document from database but flag it
        flag = 'kept'

      # write log in both cases
      msg = doclogger.generator_url(flag,id,state,host,url)
      logging.getLogger('delurl.url').info(msg)

    # log for blacklist host, number of matches and deleted
    msg = doclogger.generator_bl(len(ids),ndelurl,host)
    logging.getLogger('delurl.bl').info(msg)
      
  # print counters
  print 'counter_url:'
  pprint (vars(counter_url)) 
  print 'counter_bl(blacklist):'
  pprint (vars(counter_bl))

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

  def generator_url(self,flag,id,state,host,url):
    msg = '%10s %8s %2s %s %s' % (flag,id,state,host,url)
    return msg

  def generator_bl(self,matched,deleted,host):
    msg = '%10s %10s %s' % (matched,deleted,host)
    return msg

startup()

