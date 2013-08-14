#!/usr/bin/python
# Crawl Document Importer Light Edition: used to import documents from the
# local given a directory of PDF files. 
# The URLs of these files and parent URLs are not important and can be made up 
# and written into the .met file. The purpose is to simplify the document
# retrieval process so that instead of retrieving documents information from
# a remote server through an API, the documents can be retrieved directly
# from a local directory and ready for text extraction. 
# 
# The code generates a .met file for each pdf file. It also generates a .xml file
# that used to be returned by the crawler API. 
#
# input: full path of the directory which contains the PDF file
# output: .met file for each PDF file output to the same directory as the PDF files
#         .xml file that used to be returned by the crawler API, which contains
#         document name and path information, read by text extraction script
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
from subprocess import call
import commands
import string 
import glob
import urllib
import logparsers # log parser module
import counter 
import printinfo
import shutil
from exception import BadResourceError
from xml.dom.minidom import getDOMImplementation

def startup(verbal=False):

  # record start time 
  tic = time.time()

  # create on-screen information print object
  infoprinter = printinfo.printInfo()

  # create document writer
  writer = output.CiteSeerWriter([runconfig.cdilite['docdir'],runconfig.cdilite['crawler']])

  # create document logger (for this middleware)
  doclogger = Doc_Logger(os.getenv('HOSTNAME'))

  # create general log configers and config logs
  logconfiger = Log_Configer()
  logconfiger.config_loggers()

  # parse log file
  g = create_instance(runconfig.cdilite['logparser'],runconfig.cdilite['doclist'])
  g.extract_info(logsummaryfile=runconfig.cdilite['logsummaryfile'])


  # prepare to write xml file
  impl = getDOMImplementation()
  xDoc = impl.createDocument(None, "response", None)
  root = xDoc.documentElement
  root.setAttribute("location", runconfig.cdilite['docdir'])



  # number counter
  counters = counter.Counter()
  counters.newCounter('all')
  counters.setCounter('all',g.nline['parsed'])
  counters.newCounter('failed_BadURL')
  counters.newCounter('failed_FileNotFound')

  # save the current path 
  currentPath = os.getcwd()

  # loop over each information tuple extracted from document list file 
  # each tuple contains the name of the pdf files
  if verbal: print "counters.all = ",counters.all
  for i in range(0,counters.all):
    print ''
    sys.stdout.write("\r")
    sys.stdout.write("%9d/%-9d  " % (i+1,counters.all))
    sys.stdout.write("\n")
    infoprinter.printPara('URL',g.rel_path[i])

    code = None
    
    # get resource variable "r"
    if verbal: print 'g.parent_url[i] = ',g.parent_url[i]
    if verbal: print 'g.url[i] = ',g.url[i]
    try:
        r = resource.Resource(code,g.parent_url[i],g.url[i],\
            g.is_seed[i],g.hop[i],runconfig.batch,g.anchor_text[i])
    except BadResourceError,e:
	infoprinter.printStatus('URL Parse','fail')
	counters.addCounter('failed_BadURL')
	continue

    r.crawl_date = g.crawl_date[i]
    r.content_type = g.content_type[i]
    infoprinter.printPara('mime-type',r.content_type)

    # where crawled documents are saved
    # retrieve the local hard copy of document
    infile = os.path.join(currentPath,runconfig.cdilite['docdir'],g.rel_path[i])

    inpdf = infile # e.g., filepath/file.pdf 
    if '%' in inpdf: 
      inpdf = urllib.unquote(inpdf) #unquote escapes, e.g., %7 -> ~

    # try to remove the last back slash from the full path 
    # or try to see if fullpath/index.html exists, maybe that is the file
    # if document file still cannot be found, write into log and skip it
    inpdfpath = inpdf
    if not os.path.exists(inpdfpath):
	msg = doclogger.generator('FileNotFound',infile,r)
  	logging.getLogger('document').info(msg)
	counters.addCounter('failed_FileNotFound')
	infoprinter.printStatus('Document file found','no')

    # inpdfpath is the "corrected" file path
    inpdf = inpdfpath
    infoprinter.printStatus('Document file found','yes')

    # load pdf file content to calculate encryption
    f = open(inpdf,'r')
    data = f.read()
    f.close()

    # calculate SHA1
    r.content_sha1 = hashlib.sha1(data).hexdigest() 
  
    try:
        # only save metadata file
        writer.save_met(r,inpdf) 
    except IOError,e:
        msg = doclogger.generator('IOErrorSave',infile,r)
        logging.getLogger('document').info(msg)
    except OSError,e:
        msg = doclogger.generator('OSErrorSave',infile,r)
        logging.getLogger('document').info(msg)

    doc_node = xDoc.createElement("doc")
    doc_node.setAttribute("id", str(i+1))
    doc_node.appendChild(xDoc.createTextNode(inpdf))
    root.appendChild(doc_node)

  # write xml data into a file
  fxml = open("cdilite.xml",'w')
  fxml.write(xDoc.toxml())
  fxml.close()

  # print counters in a new line
  counters.printCounter()

  # record end time to calculate processing time
  # because strftime() will truncate the time string when converting to the
  # user-defined time format, we add "1" second to compensate this loss. 
  toc = time.time()
  processingtime = time.strftime('%H:%M:%S',time.gmtime(toc-tic+1))
  infoprinter.printPara('Processing time: ',processingtime)

# Add node to xml file
def add_node(tag, value):
      node = xDoc.createElement(tag)
      node.appendChild(xDoc.createTextNode(value))
      root.appendChild(node)
      return node

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

    for name in ['cdilite']:
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
  def __init__(self,hostname):
      self.hostname = hostname

  def generator(self,flag,filepath,r):
    msg = '%20s %10s %s %s %s %s' % (flag,"application/pdf",\
                                r.crawl_date,r.url,self.hostname,filepath)
    return msg

startup(verbal=True)

