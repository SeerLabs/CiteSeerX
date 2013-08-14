####################
# CDI GLOBAL SETUPS
# Setups below are applied in cdi.py. Other scripts may use them. They 
# are also allowed to have their own parameters 
####################
# Django setting module 
django_settings_module = 'settings'

# crawler logfile  
# lines to skip from the log (usually the header)
skip = 0
# number of log lines to read (0 for all)
nloglines = 0
# path of crawl log file
logfile = '/data/heritrix-1.14.4/jobs/job1/logs/crawl.log'

# directory containing crawled files, must add "/" at the end
inputdir = '/data/heritrix-1.14.4/jobs/job1/mirror/'

# CDI log directory (save running logs)
cdilogdir = 'log/'
# general logs: prefixes of all possible log files, e.g., if you log files is
# document.log and url.log, general_logs = ['document','url']
# general logs will be saved at cdilogdir/general_logs
general_logs = ['document','del.url','whitelistgen']

# temporary directory (for text files extracted by PDFBox)
tempdir = 'temp/'

# blacklist file
# e.g., www.example.com
blacklistfile = 'blacklist.dat'

# domain blacklist file
# e.g., example.org
domainblacklistfile = 'blacklist.domain.dat'

# logsummary file: logparser can write parsing results into this file
logsummaryfile = cdilogdir+'logsummary.log'

# running summary, re-print the counting table when script finishes
runsummaryfile = cdilogdir+'runsummary.log'

# crawler name, e.g., Heritrix, SYZ, lftp
crawler = 'heritrix'

# crawler saver name, e.g, mirror, warc, arc, lftp
saver = 'mirror'

# Crawler log parser
logparser = 'logparsers.Heritrix'

# directory where documents are imported to (add the "/")
outputdir = '/data/crawl/rep/'
# testing directory for document output
#outputdir = 'rep/'
# crawl repository
crawlrepo = '/data/crawl/repository/'

# CDI filters
# accepted document types. The doc type filter is always on
# other types: application/vnd.ms-powerpoint,application/postscript
allow_doc_type = ['application/pdf']

# toggle: urlfilter, False = filter off
toggle_urlfilter = True

# toggle: document content filter. False = filter off
toggle_doc_content_filter = False

# toggle: save into db
toggle_save_to_db = True

# toggle: save document separately naming independent of db
# If this is true, documents are not saved to db anyway
# and are numbered incrementally starting from 1 to "outputdir"
toggle_save_doc_separate = False

# toggle: overwrite files. If set to "True", files in the 
# repository will be overwritten even if the SHA1 already exists
# in the database. This is useful to restore a crashed repository
# If set to False, CDI determines whether a files should be saved or not
overwrite_file = False

# PDFBox jar file full path. Only useful when doc_content_filter is on
pdfboxpath = '/usr/local/pdfbox-1.6.0/app/target/pdfbox-app-1.6.0.jar'

# ps2ascii command path. Only useful when doc_content_filter is on 
ps2asciipath = '/usr/bin/ps2ascii'

# table name for the document
dbt_document = 'main_crawl_document'

# table name for the parenturls
dbt_parenturl = 'main_crawl_parenturl'

# default batch number for the "submission_id" column of main_crawl_document
# Used to identify import dataset. For example, "1" for A dataset;
# "2" for B dataset. "-1" for scheduled crawl etc. 
batch = 1

# maximum length of the URL (including)
urlmaxlen = 255

########################
# whitelistgen setups 
########################
		# user-agent: consistent with the crawler user agent
whitelistgen = {"user_agent":"useragent",\
		# whether to check if this URL is alive or not
		"checkurlalive":True,\
		# if request time is longer than this time (s), treat this URL as dead
		"sockettimetout":2}

#######################
# CDE configurations ##
#######################
cde = {"outputdir":"/data/export/",\
	"toggle_export":True,			\
	"summaryfile":"cde.summary",		\
	"dbquery":"SELECT id FROM main_crawl_document limit 10;"}


###########################
# CDILITE configurations ##
###########################
cdilite = {"doclist":"cdilite.list",\
	# directory where PDF files are saved
	"docdir":"cdilite",\
	"logsummaryfile":"cdilite.summary",\
	# log parser
	"logparser":"logparsers.CDILITE",\
	# crawler name, if doc is not from a crawler, use 'na'
	"crawler":"na"
}
