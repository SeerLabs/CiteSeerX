#!/usr/local/bin/python2.6
# Crawl Document Exporter 
# Python code to export the crawling results (currently only from Heritrix 
# mirror writer) to the crawl database
#
# locate the input file and read the entire log file
# input: full path of log file
# output: a single record of log file
#         this program must be run as a sudo account, otherwise, 
#         the PDFBox command cannot be executed.
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
# check configurations, including the following items
# (*) permission to write into the repository folder
#     This is checked by creating and delete a folder called "9999" 
#     inside the repository folder.
# 
def checkConfig():
    infoprtr = printinfo.printInfo()

    # input directory exists
    if not os.path.exists(runconfig.inputdir):
 	infoprtr.printStatus('inputdir exists','no')
	return False
    else:
	infoprtr.printStatus('inputdir exists','yes')
    
    # check if repository folder exists
    if not os.path.exists(runconfig.outputdir):
	infoprtr.printStatus('outputdir exists','no')
	return False
    else:
 	infoprtr.printStatus('outputdir exists','yes')

    # permission to write into the repository folder 
    if os.path.exists(runconfig.outputdir+'9999'):
	shutil.rmtree(runconfig.outputdir+'9999')
    try:
	os.makedirs(runconfig.outputdir+'9999')
	shutil.rmtree(runconfig.outputdir+'9999')
    except OSError,e:
	print e
	infoprtr.printStatus('Write permission to outputdir','no')
	return False
    
    # check if the log parser is available
    try: 
  	g = create_instance(runconfig.logparser,runconfig.logfile)
    except KeyError,e:
	print e
	infoprtr.printStatus('Log parser avaiable','no')
	return False

    # check if the log directory exists
    if not os.path.exists(runconfig.cdelogdir):
    	os.makedirs(runconfig.cdelogdir)

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

  # create document writer
  writer = output.CiteSeerWriter([runconfig.outputdir,runconfig.crawler])

  # create document type filter
  mimetypefilter = Mime_Type_Filter(runconfig.allow_doc_type)

  # create document content filter
  doccontentfilter = filter_doc.Doc_Content_Filter(runconfig.tempdir)

  # create text extractor 
  textextractor = textextract.Text_Extractor()

  # create document logger (for this middleware)
  doclogger = Doc_Logger(os.getenv('HOSTNAME'),mimetypefilter)

  # create general log configers and config logs
  logconfiger = Log_Configer()
  logconfiger.config_loggers()

  # parse log file
  g = create_instance(runconfig.logparser,runconfig.logfile)
  g.extract_info(logsummaryfile=runconfig.logsummaryfile)

  # number counter
  counters = counter.Counter()
  counters.newCounter('all')
  counters.setCounter('all',g.nline['parsed'])
  counters.newCounter('saved_New') 
  counters.newCounter('saved_Duplicate')
  counters.newCounter('filtered')
  counters.newCounter('filtered_MimetypeFilter')
  counters.newCounter('filtered_DocContentFilter')
  counters.newCounter('failed')
  counters.newCounter('failed_TextExtract')
  counters.newCounter('failed_FileNotFound')    # if inputs are pdf/ps
  counters.newCounter('failed_PDFFileNotFound') # if inputs are gzipped

  # create output directory if it does not exist
  if not os.path.exists(runconfig.outputdir):
      os.makedirs(runconfig.outputdir)

  # create temp directory if it does not exist
  if not os.path.exists(runconfig.tempdir):
      os.makedirs(runconfig.tempdir)

  # a mapping file is automatically generated if only export files 
  # (no db input) 
  if runconfig.toggle_save_doc_separate:
    open(runconfig.tempdir+'mapping.csv','w')

  # if required to visit database, make sure that database and tables 
  # are created
  if runconfig.toggle_save_to_db:
    cdb = crawldb.CrawlDB()
    # print database names
    infoprinter.printPara('Database name',cdb.dbname)
    # create document and parent table if they do not exist
    cdb.createTables()

  # save the current path 
  savedPath = os.getcwd()

  # loop over each information tuple extracted from crawler log file 
  for i in range(0,counters.all):
    print ''
    sys.stdout.write("\r")
    sys.stdout.write("%9d/%-9d  " % (i+1,counters.all))
    sys.stdout.write("\n")
    infoprinter.printPara('URL',g.url[i])

    code = None
    
    # get resource variable "r"
    r = resource.Resource(code,g.parent_url[i],g.url[i],\
        g.is_seed[i],g.hop[i],runconfig.batch,g.anchor_text[i])

    # url length cannot be longer th
    r.crawl_date = g.crawl_date[i]
    r.content_type = g.content_type[i]
    infoprinter.printPara('mime-type',r.content_type)

    # where crawled documents are saved
    # retrieve the local hard copy of document
    # If files are downloaded using "lftp", input file path should be 
    # constructed by appending the relative file path to "conf.inputdir"
    if runconfig.crawler == 'lftp':
        infile = runconfig.inputdir+g.rel_path[i]   
    elif runconfig.crawler == 'heritrix' and runconfig.saver == 'mirror':
        infile = runconfig.inputdir+r.host+r.path   
    else: 
        infile = runconfig.inputdir+g.rel_path[i]

    # apply doctype_filter, which checks document type
    mimetypefilter_ok = mimetypefilter.check(r)
    if not mimetypefilter_ok: 
      msg = doclogger.generator('DocumentTypeNotAccepted',infile,r)
      logging.getLogger('document').info(msg)
      counters.addCounter('filtered_MimetypeFilter')
      infoprinter.printStatus('Accepted document type','no')
      continue
    else:
      infoprinter.printStatus('Accepted document type','yes')

    r.ext = mimetypefilter.ext

    # check if document is already in db
    # if it returns True, log it and skip processing this one
    # if it returns False, continue
    if runconfig.toggle_save_to_db:
        recordExist = cdb.checkRecord(runconfig.dbt_document,md5=r.md5)
	if not recordExist:
	    infoprinter.printStatus('New document','yes')
       	else:
    	    msg = doclogger.generator('saved_Duplicate',infile,r)
    	    logging.getLogger('document').info(msg)
	    counters.addCounter('saved_Duplicate')
       	    infoprinter.printStatus('New document','no')
    	    continue
   
    # check existence of input file, if the name part of "infile" 
    # contains wild card characters e.g., %, 
    # try to recover it to normal 
    # "infile" is the original full file path from crawl log (may contain escape characters and may by in zipped format) 
    # "inpdf" contains original file names saved in disk (no escape characters, and in acceptable file format, e.g., PDF/postscript)
    inpdf = infile # e.g., filepath/file.pdf 
    if '%' in inpdf: 
      inpdf = urllib.unquote(inpdf) #unquote escapes, e.g., %7 -> ~

    # if document file still cannot be found, write into log and skip it
    try: f = open(inpdf)
    except IOError:
      msg = doclogger.generator('FileNotFound',infile,r)
      logging.getLogger('document').info(msg)
      counters.addCounter('failed_FileNotFound')
      infoprinter.printStatus('Document file found','no')
      continue
    else: 
      f.close()
      infoprinter.printStatus('Document file found','yes')
      infoprinter.printPara('Document file path',inpdf)
    
    # If input file is in zipped format, assuming it is a .tar.gz file
    # we do the following things
    # * copy the .tar.gz file to a temp directory 
    # * decompress it using tar -xvzf 
    # * find the .pdf file inside the unzipped 
    # * do whatever we want ...
    # * remove everything in the temp directory 
    cmd_file = 'file -i "'+infile+'"'
    cmdoutput = commands.getoutput(cmd_file)
    t = cmdoutput.split(' ')
    infilemimetype = t[-1]
    if infilemimetype == 'application/x-gzip':
      cmd_rm = 'rm -rf '+runconfig.tempdir+'*'
      cmdoutput = commands.getoutput(cmd_rm)

      cmd_cp = 'cp "'+infile+'" '+runconfig.tempdir
      cmdoutput = commands.getoutput(cmd_cp)

      # sometimes, for some (unknown) reasons, the "-C" option
      # does not work well for "tar" command, so we cd to the
      # temp directory, extract files from the .tar.gz and return
      # to the main directory
      #
      # obtain the file name from the full path: infilename
      infilename = os.path.split(infile)[1]
      os.chdir(runconfig.tempdir)
      cmd_tar = 'tar -xvzf "'+infilename+'"'
      cmdoutput = commands.getoutput(cmd_tar)
      os.chdir(savedPath)
  
      # only look for pdf files
      for root,dirs,files in os.walk(runconfig.tempdir):
        inpdffound = False
        for f in files:
	  if f.endswith('pdf'):
	    inpdf = root+'/'+f
            inpdffound = True
            break
        if inpdffound == True:
          break
      if not inpdffound: 
        msg = doclogger.generator('PDFFileNotFound',infile,r)
        logging.getLogger('document').info(msg)
        counters.addCounter('failed_PDFFileNotFound')
        infoprinter.printStatus('PDF Document file found','no')
        continue
    
    # document file is found
    # check if need to use doc_content_filter
    if runconfig.toggle_doc_content_filter:
      
      # extract text from documents 
      filefmt = mimetypefilter.doctype

      infoprinter.printPara('Mime type',filefmt)
      # acceptable formats: e.g., "application/pdf","application/postscript" 
      textextractmsg = textextractor.extract(inpdf,filefmt) 

      # classify document if text is extracted successfully
      if 'Success' in textextractmsg:
          infoprinter.printStatus('Extract text','success')
          # not a paper, log it and proceed it to the next
          if doccontentfilter.Decider(textextractor.outtxtfile,inpdf) == -1:
	      counters.addCounter('filtered_DocContentFilter')
              msg = doclogger.generator('NotAcademic',infile,r)
              logging.getLogger('document').info(msg)
	      infoprinter.printStatus('Accepted document content','no')
              continue
	  else:
	      infoprinter.printStatus('Accepted document content','yes')
      else: # text extraction fails, report error and write it into log file
          infoprinter.printStatus('Extract text','fail')
	  counters.addCounter('failed_TextExtract')
          msg = doclogger.generator(textextractmsg,infile,r)
          logging.getLogger('document').info(msg)
          continue

    # write document information into database
    # database settings can be found at settings.py
    # read file content and calculate the SHA1 value
    # read PDF document information
    f = open(inpdf,'r')
    data = f.read()
    f.close()

    # If required to save crawled documents separately,
    # do not save to db, only save document to outputdir
    # Files are named using numbers starting from 1
    # A mapping file is automatically generated
    filenamebody = id_to_fname(i+1,r.ext)
    outdoc = runconfig.outputdir+filenamebody
    if runconfig.toggle_save_doc_separate:
      mappingline = outdoc+','+infile # may not be inpdf
      ff = open(outdoc,'w')
      ff.write(data)
      ff.close
      try:
	f = open(outdoc)
        msg = doclogger.generator('saved_New',infile,r)
        logging.getLogger('document').info(msg)
        infoprinter.printStatus('Document saved','yes')
        # number of saved documents 
        counters.addCounter('saved_New')
      except IOError,e:
        infoprinter.printStatus('Document saved','no')
        raise SystemExit(e)

    # if require to save to db,calculate SHA1
    if runconfig.toggle_save_to_db:
      r.content_sha1 = hashlib.sha1(data).hexdigest() 
  
      # read text file converted from pdf
      try:
        # save to db, save doc and metadata
        writer.save(r,data) 
      except IOError,e:
        msg = doclogger.generator('IOErrorSave',infile,r)
        logging.getLogger('document').info(msg)
      except OSError,e:
        msg = doclogger.generator('OSErrorSave',infile,r)
        logging.getLogger('document').info(msg)
      else: # nothing wrong with saving to db
            # and the text is extracted
        if runconfig.toggle_doc_content_filter:
          ft = open(textextractor.outtxt,'r')
          txtdata = ft.read()
          ft.close()
          writer.savetxt(txtdata) # save text file to the same folder 

    # log successful
    # check repository to see if output PDF files are there
    msg = doclogger.generator('saved_New',infile,r)
    logging.getLogger('document').info(msg)
    infoprinter.printStatus('Document saved','yes')
    # number of documents which are written into db
    counters.addCounter('saved_New')
        
  # print counters in a new line
  counters.setCounter('filtered',counters.filtered_MimetypeFilter+counters.filtered_DocContentFilter)
  counters.setCounter('failed',counters.failed_TextExtract+counters.failed_FileNotFound+counters.failed_PDFFileNotFound)
  counters.printCounter()
  counters.printCountertoFile(runconfig.runsummaryfile)

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

