#!/usr/bin/python
# generate the log file of microsoft academy crawl 
# The original download does not come with a 
# log file. For each downloaded pdf file, there is an associated
# '.txt' file, which only gives the document URL
# This script is designed to concatenate these '.txt' files into
# a single file with these columns
# filename		URL
# 2013-02-20-18-48-58_12.pdf		http://jvi.asm.org/content/76/23/11920.full.pdf
import os
import fnmatch
import re
import counter
def startup(verbal=False):
    # initial setups
    # "crawldir" contains all crawled documents including ".pdf" and ".txt" files
    crawldir = '/msa/crawl/directory/crawler-out'
    # "outputlogpath" is the output file path
    outputlogpath = "/msa/crawl/directory/crawl.log"

    # open output file
    fout = open(outputlogpath,'w')   
    
    # create counters
    counters = counter.Counter()
    counters.newCounter('txt')
    counters.newCounter('pdf')

    # walk through the crawl directory for all .txt files
    for root,dirs,files in os.walk(crawldir):
        for f in files:
	    if f.endswith('.txt'):
	    	counters.addCounter('txt')
		# load the txt file
		lines = file(os.path.join(root,f)).readlines()
		for line in lines:
		    line = line.strip('\n')
		    if line.startswith("Fetched"):
			url_fetched = line[9:]
			break

		# write filename and URL into output file
		pdfpath = os.path.splitext(f)[0] + '.pdf'
		lineout = ' '.join([pdfpath,url_fetched])+'\n'
		fout.write(lineout)
		if verbal: print lineout
	    elif f.endswith('.pdf'):
	    	counters.addCounter('pdf')
	    else:
	    	print 'unknown file extension: ',file
	
    fout.close()
    counters.setCounter('all',counters.txt+counters.pdf)
    counters.printCounter()
    print 'file output to: '+outputlogpath

startup(verbal=True)
