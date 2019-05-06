#!/usr/bin/python3
# copy or scp a list of documents (from an input file) from a remote server 
# to a local directory. An rsa key must be set between the remote
# server and the local machine.
# the input is a text file containing a list of file names in separate lines
# these are files to be copied out from the repository. If only csxdois are 
# provided, this program will copy .pdf files. 
# requires rsync 2.6.7+
# if the "hierarchy" option is set, it leverage the "rsync -R" option, 
# and use "./" to delimete the input path, so only path after "./" is preserved
# e.g., if the command is rsync -R jxw394@csxrepo01:/data/repository/rep1/./10/1/1/123/234/10.1.1.123.234.pdf /data/scp, the output looks like
# /data/scp/10/1/1/123/234/10.1.1.123.234.pdf
# example:
# python3 scpdoc.py /home/jxw394/scp /home/jxw394/scp/paper_ids_text_file.txt -e txt -l

import config
import os
import sys
import counter
import subprocess
import re
import argparse
import logging

def startup(outdir,doclistfile,hierarchy,dryrun=False,ext='pdf'):

    logger = logging.getLogger("startup")

    # create on-screen information print object
    #infoprinter = printinfo.printInfo(statuslength=60)

    # define counters
    counters = counter.Counter()
    counters.newCounter('all')
    counters.newCounter("copied")
    
    # create output directory if it does not exist
    if not os.path.exists(outdir): os.makedirs(outdir)

    # construct the scp command 
    src = config.repousr+"@"+config.reposerver+":"+config.repodir
    cmd = "rsync "
    if hierarchy: cmd = "rsync -R "

    # read the input file
    with open(doclistfile,'r') as f:
        for line in f:
            counters.addCounter('all')

            # remove trailing character and only get the first column
            line = line[:-1]
            csxfile = line.split()[0]
 
            # the default extension is .pdf 
            if not has_extension(csxfile): csxfile = ".".join([csxfile,ext])

            # find relative path based on the csxdoi, e.g., 
            # 10/1/1/123/234/10.1.1.123.234.pdf
            docpath = csxdoitopath(csxfile)

            # construct repository path in the repository server
            repopath = os.path.join(config.repodir,docpath)

            # skip files that were copied
            if not ("*" in csxfile):
                # modify command and source path if preserving dir structure
                if hierarchy: 
                    # the path of the source file
                    docpath = os.path.join("./",docpath)
                    # the path of the destination file
                    dstpath = os.path.join(outdir,docpath)
                else:
                    dstpath = os.path.join(outdir,csxfile)

                if os.path.exists(dstpath):
                    logger.debug('%(1)10s %(2)s'%{'1':"copied",'2':csxfile})
                    counters.addCounter("copied")
                    continue
        
            # construct the scp command, consider two situations
            scpcmd = cmd+os.path.join(src,docpath)+" "+outdir
            scpcmd_removed = cmd+os.path.join(src,docpath+"-removed")+" "+outdir

            # execute the command
            # if the designated file is not available, the file may be 
            # renamed. try to add an extention 
            # e.g., 10.1.1.123.234.pdf -> 10.1.1.123.234.pdf-removed
            logger.debug(scpcmd)
            if not dryrun: 
                try:
                    cmdoutput = subprocess.check_output(scpcmd,shell=True,universal_newlines=True)
                except subprocess.CalledProcessError:
                    cmdoutput = "Permission denied"

                if "No such file" in cmdoutput:
                    logger.warning("file not found: %(1)s. trying alternative: %(2)s"%{"1":csxfile,"2":csxfile+"-removed"})
                    cmdoutput = subprocess.check_output(scpcmd_removed,shell=True,universal_newlines=True)
                if "Permission denied" in cmdoutput:
                    logger.warning("permission denied: %(1)s." % {"1":csxfile})

            # check if file was been copied successfully
            if not dryrun and not ("*" in csxfile):
                if not os.path.exists(dstpath):
                    logger.warning('%(2)s %(1)10s %(2)s'%{'2':"failed",'1':csxfile})
                else:
                    counters.addCounter("copied")
                    logger.info('%(2)s %(1)10s'%{'2':"copied",'1':csxfile})

    # close file 
    if config.scpdoc['repopathfile']:
        fout.close()

    # print out counters
    counters.printCounter()

# convert a citeseerx doi to path relative to the repository path
# e.g, /data/repository/rep1/
# csxdoi = 10.1.1.12.7587.pdf
# return 10/1/1/12/7587/10.1.1.12.7587.pdf
def csxdoitopath(csxfile):
    p = re.search(r"(10\.1\.1\.\d+\.\d+)",csxfile)
    if p:
        csxDOI = p.group()
    else:
        sys.exit("cannot parse csxfile: "+csxfile)
    docpath = '/'.join(csxDOI.split('.'))
    return os.path.join(docpath,csxfile)

# check if a file name contains an extension using regular expression
# only apply to files named with csxdois
# if there's no extension, it return None, otherwise, the extension, e.g., '.pdf'

def has_extension(filename):
    p= re.search(r"\.[a-z,A-Z]+$",filename)
    if p: 
        return p.group()
    else:
        return None
  
# Start main code
if __name__ ==  "__main__":

    # receive input arguments
    parser = argparse.ArgumentParser()
    parser.add_argument("outdir",type=str,help="the directory for output data")
    parser.add_argument("doclistfile",type=str,help="the file containing a list of files to copy from the repository")
    parser.add_argument("-v","--verbose",help="increase output verbosity",action="store_true",dest="verbose",default=False)
    parser.add_argument("-d","--dry-run",help="check file availability without actually transferring them",action="store_true",dest="dryrun",default=False)
    parser.add_argument("-l","--hierarchy",help="output files are organized in hierachical order, e.g., 123.234.345.pdf saved to 123/234/345/123.234.345.pdf",action="store_true",dest="hierarchy",default=False)
    parser.add_argument("-e","--extension",type=str,help="extension to append to csxdois",dest="ext",default="pdf")
    parser.add_argument("-g","--logdir",type=str,help="a directory to save log files",dest="logdir",default="~/Downloads/logs")

    args = parser.parse_args()

    if args.verbose:
        logging_level = logging.DEBUG
    else:
        logging_level = logging.INFO

    if not os.path.exists(args.logdir):
        print(args.logdir)
        makelogdir = raw_input("Create log directory at: {0}? [Y/n]".format(args.logdir))
        if makelogdir == 'n':
            os.exit("Please provide the log directory.")
        elif makelogdir == 'Y':
            os.makedirs(args.logdir)
        else:
            os.exit("Invalid input! Enter Y or n only.")
        
    # logging configurations
    logging.basicConfig(level=logging_level,\
                  filename=os.path.join(args.logdir,"scpdoc.log"),\
                  format="%(asctime)s %(name)-30s %(levelname)-8s %(message)s")
    console = logging.StreamHandler()
    console.setLevel(logging_level)
    formatter = logging.Formatter("%(name)-30s %(levelname)-8s %(message)s")
    console.setFormatter(formatter)
    logging.getLogger("").addHandler(console)


    logging.info("logging configuration done")


#    parser = OptionParser()
#    parser.add_option("-d","--dry-run",\
#                      action="store_true",dest="dryrun",default=False,\
#                      help="check file availability without actually tranferring them")
#    parser.add_option("-q","--quite",\
#                      action="store_false",dest="verbose",default=True,\
#                      help="don't print detailed message to stdout")
#    (options,args) = parser.parse_args()

    startup(args.outdir,args.doclistfile,args.hierarchy,args.dryrun,args.ext)

