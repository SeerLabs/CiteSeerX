#!/usr/bin/python
# write a CDI compatible CSV file based on mkcrawler results
# mkcrawler does not output any log file, it only outputs 
# an associated '.txt' file, which gives two URLs.
# The one after Url is just the one the crawler attempted to fetch.
# The one after "fetched" is the real Url after redirection. 
# Most of time, they are the same, but they could be different
# Usage:
# run: python convert_mkcrawler_csv.py -h to see parameters and keywords

import logging
import os
import sys
import gc
import re
#import mysql.connector
#from config import MySQLConf as mc
#from conf import Config_mascrawlloggen
import time
from datetime import datetime
#import json
import csv
import counter
import argparse
from printlib import print_prog
#from printlib import print_prog

def main(confc):
    logger = logging.getLogger("main")

    #conf = Config_mascrawlloggen()

    # create counters
    counters = counter.Counter()

    # create output file
    fcsv = open(confc["outputfile"],'w')
    csvwriter = csv.writer(fcsv,quoting=csv.QUOTE_ALL)
    header = ("id","fullpath","url","parenturl","datetime","hop","content_type")
    csvwriter.writerow(header)

    # work through the download directory for all .txt files
    pdfi = 0
    logger.info("traversing data directory")
    for root,dirs,files in os.walk(confc["datadir"]):
        for f in files:
            if f.endswith('.txt'):
                url_fetched = get_url_fetched(os.path.join(root,f))
                if not url_fetched: continue
                pdfpath = os.path.join(root,os.path.splitext(f)[0]+'.pdf')
                print_prog(pdfi,len(files),step=1000,comments=pdfpath)

                if not os.path.exists(pdfpath):
                    logger.warning("pdf file not found: "+pdfpath)
                    continue
                counters.addCounter('all')
                pdfi += 1

                # first try to get date time from file name
                # if not work, try to get it from the file itself
                datetimestr = f.split("_")[0]
                try:
                    dt = datetime.strptime(datetimestr,"%Y-%m-%d-%H-%M-%S")
                except ValueError:
                    (f_mode, f_ino, f_dev, f_nlink, f_uid, f_gid, f_size, f_atime, f_mtime, f_ctime) = os.stat(pdfpath)
                    f_mtime = time.ctime(f_mtime)
                    dt = datetime.strptime(f_mtime,"%a %b %d %H:%M:%S %Y")
                parenturl = ""
                hop = 0
                content_type = 'application/pdf'
                outline = (pdfi,pdfpath,url_fetched,parenturl,str(dt),hop,content_type)
                csvwriter.writerow(outline)
            
    fcsv.close()
    logger.info("file output to: "+fcsv.name)
            
def get_url_fetched(txtfilepath):
    logger = logging.getLogger("get_url_fetched")
    lines = file(txtfilepath).readlines()
    url_fetched = ""
    for line in lines:
        line = line.strip("\n")
        if line.startswith("Fetched"):
            url_fetched = line[9:]
            break
    if not url_fetched:
        logger.warning("Fetched Url not found : "+txtfilepath)
        
    return url_fetched

if __name__ == "__main__":
    # accept input parameters
    parser = argparse.ArgumentParser()
    #parser = OptionParser()
    parser.add_argument("-v","--verbose",dest="verbose",\
                     action="store_true",default=False,\
                     help="Set the logging mode to logging.DEBUG")
    parser.add_argument("datadir",type=str,metavar="DATADIR",\
                     help="Top level data directory")
    args = parser.parse_args()
    logging_level = logging.DEBUG if args.verbose else logging.INFO

    # logging configurations
    logging.basicConfig(level=logging_level,\
                        filename="logs/convert_mkcrawler_csv.log",\
                        format="%(asctime)s %(name)-20s %(levelname)-8s %(message)s",
                        mode="w")
    console = logging.StreamHandler()
    console.setLevel(logging.INFO)
    formatter = logging.Formatter("%(name)-20s %(levelname)-8s %(message)s")
    console.setFormatter(formatter)
    logging.getLogger("").addHandler(console)
    logging.info("logging configuration done")

    # construct output file
    s = args.datadir.split("/")
    outputfile = s[-1] if s[-1] else s[-2]
    outputfile = os.path.join(args.datadir,outputfile+'.csv')
    confc = {"datadir":args.datadir,"outputfile":outputfile}
    main(confc)
