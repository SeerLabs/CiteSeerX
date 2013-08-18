import datetime
import time
import os
import counter
import runconfig
import sys
class Heritrix(object):

  def __init__(self,logfile):
    self.logfile = logfile
    self.url = []
    self.parent_url = []
    self.is_seed = []
    self.hop = []
    self.crawl_date = []
    self.content_type = []
    self.anchor_text = []
    self.nline = {'total':0,'skipped':0,'parsed':0}
    self.counters = counter.Counter()
    self.counters.newCounter('Accepted')
    self.counters.newCounter('Accepted_isSeed')

  def extract_info(self,logsummaryfile=None,skip=0,nloglines=0):
    # open log file
    print 'loading file: '+self.logfile+' ...'
    try:
      lines = file(self.logfile).readlines()
    except IOError: 
      print ">>> File not found: ",self.logfile
      return 0

    # if nloglines >0, e.g., 100, use the first 100 lines, 
    # otherwise, use all lines
    if nloglines >0:
	lines = lines[skip:nloglines]

    self.nline['total'] = len(lines) 
    self.counters.all = self.nline['total']
    print 'total lines read: ',self.nline['total']
    print ''

    # loop over each line and extract information
    for line in lines:
      # split each row 
      s = line.split()
      # refer to Heritrix User Manual Section 8.2.1 for meanings of each column
      # refer to http://crawler.archive.org/articles/user_manual/glossary.html
      # for Discovery path. Each URI has a discovery path. The path contains one
      # character for each link or embed following from the seed
      # (*) R - Redirect 
      # (*) E - Embed
      # (*) X - Speculative embed (aggressive/Javascript link extraction)
      # (*) L - Link
      # (*) P - Prerequisite (as for DNS or robots.txt before another URI)
      # The discovery path of seeds is an empty string. 
      # hop
      # check fetch state code
      # exclude fetch state code = [1,404] and negative fetch code
      # 1 - dns
      # 404 - not found
      # 403 - forbidden 
      # 1001: HTTPPing to URL did not return the expected HTTP status code
      # dpath can also tell us information about status
      # In addition, URL length cannot exceed runconfig.urlmaxlen
      # It should be noted that the values of these counters for whatever
      # reason the record is excluded cannot be treated as "exclusive" because
      # the record could have other unacceptable properties. For example, 
      # a URL can be stamped with "skip_longURL", but it could also 
      # be identified to have a bad "dpath" then the countername is changed to
      # "skip_discoveryPath[]" and the "skip_longURL" information is lost. 
      try:
        fetchstatecode = int(s[1])
      except ValueError,e:
	countername = "skip_badstatecode"
        if self.counters.isanewCounter(countername):
	    self.counters.newCounter(countername)
	self.counters.addCounter(countername)
	self.nline['skipped'] = self.nline['skipped'] + 1
	continue
	
      try:
        dpath = s[4]
      except IndexError,e:
 	countername = "skip_baddepth"
	if self.counters.isanewCounter(countername):
	    self.counters.newCounter(countername)
 	self.counters.addCounter(countername)
	self.nline['skipped'] = self.nline['skipped'] + 1
	continue

      url = s[3]
      urlsplit = url.split('/')

      countername = None
      if len(url) >= runconfig.urlmaxlen:
	countername = 'skip_longURL'
      if len(s[5]) >= runconfig.urlmaxlen:
 	countername = 'skip_longParentURL'
      if dpath == 'P':
	countername = 'skip_discoveryPath['+dpath+']'
      elif dpath == 'E':
	countername = 'skip_discoveryPath['+dpath+']'
      elif fetchstatecode < 0 or fetchstatecode in [1,403,404,1001]:
        # ignore DNS/robots.txt and embeded links (HTML page above for details)
	countername = 'skip_stateCode['+str(fetchstatecode)+']'
      #elif "?" in urlsplit[-1]:
      #  if s[6] == 'application/pdf':
      #      print '? is detected: '+url 
        # the last section of the url cannot contain question mark "?"
        # e.g., http://www.example.com/var/file?format.pdf is not accepted
#	countername = 'skip_urlContain[?]'

      # count each type of skipped document
      if countername:
        if self.counters.isanewCounter(countername):
	    self.counters.newCounter(countername)
	self.counters.addCounter(countername)
        self.nline['skipped'] = self.nline['skipped'] + 1
	continue

      if dpath == '-':
        self.hop.append(0)
      else:
        self.hop.append(len(dpath))

      # url
      self.url.append(url)
     
      # is_seed
      is_seed = 0
      if dpath == '-':
        is_seed = 1
        # parent_url is None for seeds
        self.parent_url.append(None)
        self.counters.addCounter('Accepted_isSeed')
      else:
        self.parent_url.append(s[5])

      self.is_seed.append(is_seed)

      # anchor_text 
      self.anchor_text.append('')

      # crawl_date
      datestr = s[0] 
      datestrs = datestr.split(".")# remove second decimals
      try:
      	datetup = datetime.datetime(*(time.strptime(datestrs[0],"%Y-%m-%dT%H:%M:%S")[0:6])) # convert from string to datetime.datetime format
      except ValueError,e:
	countername = "skip_baddateformat"
        if self.counters.isanewCounter(countername):
	    self.counters.newCounter(countername)
	self.counters.addCounter(countername)
	self.nline['skipped'] = self.nline['skipped'] + 1
	continue
	    
      self.crawl_date.append(datetup)

      # content_type 
      self.content_type.append(s[6])

      # number of parsed documents
      self.counters.addCounter('Accepted')
      self.nline['parsed'] = self.nline['parsed'] + 1
    
    # print the counters to a file
    # "printcounters" is the full path of the log parser summary file
    if logsummaryfile:
      self.counters.printCountertoFile(logsummaryfile) 

class PMC(object):

  def __init__(self,logfile):
    self.logfile = logfile
    self.url = []
    self.parent_url = []
    self.is_seed = []
    self.hop = []
    self.crawl_date = []
    self.content_type = []
    self.anchor_text = []
    self.rel_path = []
    self.nline = {'total':0,'skipped':0,'parsed':0}

  def extract_info(self,logsummaryfile=None,skip=0):
    # open log file
    # use "ftp.ncbi.nlm.nih.gov/pub/pmc/file_list.csv" as log file
    print 'loading file: '+self.logfile+' ...'
    try:
      lines = file(self.logfile).readlines()
      # skip the header
      lines = lines[skip:]
      self.nline['total'] = len(lines) 
    except IOError: 
      print ">>> File not found: ",self.logfile
      return 0
    print ''

    # base URL, document URLs are generated by appending the first column in 
    # the log file to the base URL
    # e.g., 
    baseurl = 'ftp://ftp.ncbi.nlm.nih.gov/'
    # loop over each line and extract information
    print 'extracting information from file: '+self.logfile+' ...'
    for line in lines:
      # split each row 
      s = line.split(',')
      # refer to ftp.ncbi.nlm.nih.gov/pub/pmc/readme.txt for meanings of each column
      #
      # we include all lines, and none of them is skipped 

      # Crawling depth is always 3, 
      # depth 0: ftp.ncbi.nlm.nih.gov/
      # depth 1: ftp.ncbi.nlm.nih.gov/pub/
      # depth 2: ftp.ncbi.nlm.nih.gov/pub/pmc/
      # 
      self.hop.append(3)

      # relative path is the path relative to the "inputdir" variable in "cde.conf"
      rel_path = s[0]
      self.rel_path.append(rel_path)

      # URL is ftp://ftp.ncbi.nlm.nih.gov/pub/pmc/[first_col_in_log] 
      # url
      url = baseurl+'pub/pmc/'+rel_path
      self.url.append(url)
      
      # parent URL is generated by trimming the file name off from the document URL
      # e.g., if a document URL is ftp://ftp.ncbi.nlm.nih.gov/pub/pmc/73/7c/BMC_Emerg_Med_2004_Jul_13_4_2.tar.gz
      # its parent URL is ftp://ftp.ncbi.nlm.nih.gov/pub/pmc/73/7c/
      parent_url = os.path.split(url)[0]
      self.parent_url.append(parent_url)

      # no URL in this log is seed
      is_seed = 0
      self.is_seed.append(is_seed)

      # no anchor_text 
      self.anchor_text.append('')

      # crawl_date is set to be May 15, 2012, 00:00:00 this is the date where we started up 
      # downloading files from the ftp site
      datestr = '2012-05-15T00:00:00'
      datetup = datetime.datetime(*(time.strptime(datestr,"%Y-%m-%dT%H:%M:%S")[0:6])) # convert from string to datetime.datetime format
      self.crawl_date.append(datetup)

      # content_type is always application/pdf
      content_type = 'application/pdf'
      self.content_type.append(content_type)

      # number of parsed documents
      self.nline['parsed'] = self.nline['parsed'] + 1

class ARXIV(object):

  def __init__(self,logfile):
    self.logfile = logfile
    self.url = []
    self.parent_url = []
    self.is_seed = []
    self.hop = []
    self.crawl_date = []
    self.content_type = []
    self.anchor_text = []
    self.rel_path = []
    self.nline = {'total':0,'skipped':0,'parsed':0}
    self.urlbase = 'http://arxiv.org/pdf/'
    self.parenturlbase = 'http://arxiv.org/abs/'

  def extract_info(self,logsummaryfile=None):
    # open log file
    # use "/export/csxcrawl/arxiv/files.txt" as log file
    print 'loading file: '+self.logfile+' ...'
    try:
      lines = file(self.logfile).readlines()
      self.nline['total'] = len(lines) 
    except IOError: 
      print ">>> File not found: ",self.logfile
      return 0
    print ''

    # document URLs are generated so that if a record is like
    # ./pdf/arxiv/0711/0711.2217v1.pdf
    # the URL and parent URL should be
    # http://arxiv.org/pdf/0711.2217v1.pdf
    # http://arxiv.org/abs/0711.2217v1
    # 
    # ./pdf/chem-ph/9502/9502008v1.pdf
    # the URL and parent URL should be
    # http://arxiv.org/pdf/chem-ph/9502008v1.pdf
    # http://arxiv.org/abs/chem-ph/9502008v1
    #
    # ./pdf/astro-ph/0602/0602464v1.pdf
    # the URL and parent URL should be
    # http://arxiv.org/pdf/astro-ph/9502008v1.pdf
    # http://arxiv.org/abs/astro-ph/9502008v1

    # loop over each line and extract information
    print 'extracting information from file: '+self.logfile+' ...'
    for line in lines:
      line = line.strip('\n')
      # split each row 
      s = line.split(' ')
      # e.g., if s[0] = './pdf/astro-ph/0602/0602464v1.pdf'
      # s0s = ['.', 'pdf', 'astro-ph', '0602', '0602464v1.pdf']
      s0s = s[0].split('/')

      # Crawling depth is always 1, 
      self.hop.append(1)

      # relative path (rel_path) is the path relative to the 
      # "inputdir" variable in "runconfig.py"
      # e.g., rel_path = '/astro-ph/0602/0602464v1.pdf'
      rel_path = '/'.join(s[0].split('/')[2:])
      self.rel_path.append(rel_path)
      full_path = runconfig.inputdir+rel_path

      # URL is constructed depending on the subject category 
      # the base url is 'http://arxiv.org/pdf/'
      # the base url for the parent url is 'http://arxiv.org/abs/' 
      if s0s[2] == 'arxiv':
	url = self.urlbase+s0s[-1]
	# remove the ".pdf" from the parenturl to avoid confusion
	parent_url = self.parenturlbase+s0s[-1][0:-4]
      else:
        url = self.urlbase+'/'.join([s0s[2],s0s[-1]])
	# remove the ".pdf" from the parenturl to avoid confusion
	parent_url = self.parenturlbase+'/'.join([s0s[2],s0s[-1][0:-4]])
     
      self.url.append(url)
      self.parent_url.append(parent_url)

      # no URL in this log is seed
      is_seed = 0
      self.is_seed.append(is_seed)

      # no anchor_text 
      self.anchor_text.append('')

      # crawl_date is set to be the "last modified time" of this file, 
      
      (f_mode, f_ino, f_dev, f_nlink, f_uid, f_gid, f_size, f_atime, f_mtime, f_ctime) = os.stat(full_path)
      f_mtime = time.ctime(f_mtime)
      
      # directory was initially created.
      datetup = datetime.datetime(*(time.strptime(f_mtime,"%a %b %d %H:%M:%S %Y")[0:6])) # convert from string to datetime.datetime format
      self.crawl_date.append(datetup)

      # content_type is always application/pdf
      content_type = 'application/pdf'
      self.content_type.append(content_type)

      # number of parsed documents
      self.nline['parsed'] = self.nline['parsed'] + 1

# This class deals with PDF input to CDILITE, for consistency, the input argument is still 
# called "logfile", but it is not an actual log file but just a list of
# one-column text which are document paths ("doclist" in runconfig). 
# Beccause CDILITE is initially designed for testing text extraction module without
# installing the crawler db and rep, the parameters we can obtain from the "logfile" are
# only file names. The URL, parent URLs and other parameters are retained but can be 
# assigned with arbitury values, because we still need to write the .met file to the input 
# repository.  
class CDILITE(object):
    
    def __init__(self,logfile):
        self.logfile = logfile
	# These parameters are assigned with arbituary values
        self.url = []
        self.parent_url = []
        self.is_seed = []
        self.hop = []
        self.crawl_date = []
        self.content_type = []
        self.anchor_text = []
	# this is the path of PDF document
        self.rel_path = []
        self.nline = {'total':0,'skipped':0,'parsed':0}
	
    # This method does the actual job. 
    # The input is just the name of the log summary file, which gives
    # count numbers 
    def extract_info(self,logsummaryfile=None):

    	# open log file
    	print 'loading file: '+self.logfile+' ...'
    	try:
      	    lines = file(self.logfile).readlines()
      	    self.nline['total'] = len(lines)
    	except IOError:
      	    print ">>> File not found: ",self.logfile
      	    return 0
    	print ''

    	# loop over each line and extract information
    	print 'extracting information from file: '+self.logfile+' ...'
    	for line in lines:
      	    line = line.strip('\n')
      	    # split each row 
      	    s = line.split(' ')

	    # "s" should have up to three components. 
	    # if len(s)=3, it should be filename, URL, parentURL, hop = 1
	    # if len(s)=2, it should be filename, URL, hop = 0
	    # if len(s)=1, it should be filename, hop = 0
	    hop = 0
	    if len(s) == 3: hop = 1
      	    self.hop.append(hop)

      	    # relative path (rel_path) is the path relative to the 
      	    # "inputdir" variable in "runconfig.py"
      	    # e.g., rel_path = '/astro-ph/0602/0602464v1.pdf'
      	    rel_path = s[0]
      	    self.rel_path.append(rel_path)
      	    full_path = os.path.join(runconfig.cdilite['docdir'],rel_path)

            # URL is arbituary if it is not provided
            if len(s) == 1:
		url = 'http://unknown.source.com/my.pdf'
        	parent_url = 'http://unknown.source.com/'
      	    elif len(s) == 2:
        	url = s[1]
        	parent_url = 'http://unknown.source.com/'
	    elif len(s) == 3:
		url = s[1]
		parent_url = s[2]

      	    self.url.append(url)
      	    self.parent_url.append(parent_url)

      	    # no URL in this log is seed
            is_seed = 0
      	    self.is_seed.append(is_seed)
                                                                      
      	    # no anchor_text    
            self.anchor_text.append('')  

      	    # crawl_date is set to be the "last modified time" of this file, 
      	    (f_mode, f_ino, f_dev, f_nlink, f_uid, f_gid, f_size, f_atime, f_mtime, f_ctime) = os.stat(full_path)
      	    f_mtime = time.ctime(f_mtime)
            datetup = datetime.datetime(*(time.strptime(f_mtime,"%a %b %d %H:%M:%S %Y")[0:6])) # convert from string to datetime.datetime format    
      	    self.crawl_date.append(datetup)
                
      	    # content_type is always application/pdf
      	    content_type = 'application/pdf'
      	    self.content_type.append(content_type)
                
      	    # number of parsed documents
      	    self.nline['parsed'] = self.nline['parsed'] + 1

# parse microsoft academic crawling log (performed by Madian Khabsa)
# The original crawling does not output any log file. For each downloaded pdf
# file, there is an associated '.txt' file, which only gives the document URL
# Jian Wu wrote a Python script to concatenate these '.txt' files into a single
# file with three columns
# file-name				URL
# 2013-02-20-18-48-58_12.pdf		http://jvi.asm.org/content/76/23/11920.full.pdf
# the following parser will parse this file.
# PS: there are two URLs in the .txt file, the one after 'URL:' is just the one 
# the crawler attempted to fetch. The one after 'Fetched:' is the real URL 
# after redirection. Though most of time, they are the same, they could be 
# different
class MSA(object):

    def __init__(self,logfile):
        self.logfile = logfile
	# These parameters are assigned with arbituary values
        self.url = []
        self.parent_url = []
        self.is_seed = []
        self.hop = []
        self.crawl_date = []
        self.content_type = []
        self.anchor_text = []
	# this is the path of PDF document
        self.rel_path = []
        self.nline = {'total':0,'skipped':0,'parsed':0}

    # This method does the actual text extraction job. 
    # The input is just the name of the log summary file, which gives
    # count numbers 
    def extract_info(self,logsummaryfile=None,skip=0,nloglines=0):

    	# open log file
    	print 'loading file: '+self.logfile+' ...'
    	try:
      	    lines = file(self.logfile).readlines()
      	    self.nline['total'] = len(lines)
    	except IOError:
      	    print ">>> File not found: ",self.logfile
      	    return 0
    	print ''

    	# loop over each line and extract information
    	print 'extracting information from file: '+self.logfile+' ...'
    	for line in lines:
      	    line = line.strip('\n')
      	    # split each row 
      	    s = line.split(' ')

	    # "s" should have three components. There is NO parentURL 
	    # filename, crawldate, URL, hop = 0
	    hop = 0

      	    # relative path (rel_path) is the path relative to the 
      	    # "inputdir" variable in "runconfig.py"
      	    # e.g., rel_path = '2013-02-28-06-35-42_171.pdf'
      	    rel_path = s[0]
      	    full_path = os.path.join(runconfig.inputdir,rel_path)

            # URL is arbituary if it is not provided
	    url = s[1]
	    parent_url = None

            if len(url) > runconfig.urlmaxlen:
	   	print 'URL too long: ',len(url)
		continue

      	    self.rel_path.append(rel_path)
      	    self.hop.append(hop)
      	    self.url.append(url)
      	    self.parent_url.append(parent_url)

      	    # no URL in this log is seed
            is_seed = 0
      	    self.is_seed.append(is_seed)
                                                                      
      	    # no anchor_text    
            self.anchor_text.append('')  

	    # crawl_date is obtained from the file name
	    # e.g., 2013-02-22-05-34-44_200.pdf
 	    # is resolved to 2013-02-22-5:34:44
	    datetimestr = rel_path.split('_')[0]
            datetup = datetime.datetime(*(time.strptime(datetimestr,"%Y-%m-%d-%H-%M-%S")[0:6])) # convert from string to datetime.datetime format
      	    self.crawl_date.append(datetup)
                
      	    # content_type is always application/pdf
      	    content_type = 'application/pdf'
      	    self.content_type.append(content_type)
                
      	    # number of parsed documents
      	    self.nline['parsed'] = self.nline['parsed'] + 1

