import os

import runconfig
import commands

class Text_Extractor(object):
  def __init__(self):
    self.extractors = {}
    # default command, customizable before calling "extract" function
    self.extractors['pdfbox'] = 'java -jar '+runconfig.pdfboxpath+' ExtractText -encoding UTF8 -force ' #default value
    self.extractors['ps2ascii'] = runconfig.ps2asciipath
    # remove command
    self.rm = 'rm -rf '
    # full path of extracted text file
    self.outtxt = ''
    # name of extracted text file (name only)
    self.outtxtfile = ''
 
  # main function 
  def extract(self,file,format):
    # check if file exits
    if not os.path.exists(file):
	print 'Error: FileNotExist:'+file
        return 'FileNotExist:'+file

    # file must be decompressed before using this module 
    
    # extract file depending on format
    if 'application/pdf' in format: 
        msg = self.pdfboxextract(file)
    elif 'application/postscript' in format:
        msg = self.ps2asciiextract(file)
    elif format == 'unknown':
        msg = "UnknownFileFormat:"+format

    return msg

  def pdfboxextract(self,file):

    # generate output file text path 
    # file is the full path of input file, e.g., /db/example.pdf
    # self.outtxt is the full path of output text file, e.g., temp/example.txt
    # self.outtxtfile is the file name of the output text file, e.g., example.txt
    # filename is the file name of the input file, e.g., example.pdf
    self.outtxt = self.file2textfile(file,runconfig.tempdir) 
    outtxtfiletmp = os.path.split(self.outtxt) 
    self.outtxtfile = outtxtfiletmp[1]
    filetmp = os.path.split(file)
    filename = filetmp[1]

    # delete existing text files
    if os.path.exists(self.outtxt): 
      cmd = self.rm + self.outtxt
      os.system(cmd)
      
    # run PDFBox
    extractcmd = self.extractors['pdfbox']+ '"'+file +'" '+ self.outtxt
    print ">>> converting "+filename+" >> "+self.outtxtfile
    pdfboxcmdout = commands.getoutput(extractcmd) # must run as a sudo account

    # if command not executed successfully, write a log and skip this document
    pdfboxerrmsgs = ['Unable to access jar file',\
                   'ExtractText failed',\
                   'Did not found XRef object at specified startxref position']
    for pdfboxerrmsg in pdfboxerrmsgs:
      if pdfboxerrmsg in pdfboxcmdout:
        return 'PDFBoxFail'

    # if no text file is generated, skip this document
    if not os.path.exists(self.outtxt):
      return 'PDFBoxFail'

    # if output file is < 50 bytes, write to log and skip this document
    if os.path.getsize(self.outtxt) < 50:
      return 'PDFBoxFail'
 
    # return '0' if file is successfully extracted
    return 'PDFBoxSuccess'
      
   
  def ps2asciiextract(self,file):

    # generate output file text path 
    # file is the full path of input file, e.g., /db/example.pdf
    # self.outtxt is the full path of output text file, e.g., temp/example.txt
    # self.outtxtfile is the file name of the output text file, e.g., example.txt
    # filename is the file name of the input file, e.g., example.pdf
    self.outtxt = self.file2textfile(file,runconfig.tempdir) 
    outtxtfiletmp = os.path.split(self.outtxt) 
    self.outtxtfile = outtxtfiletmp[1]
    filetmp = os.path.split(file)
    filename = filetmp[1]

    # delete the existing text file
    if os.path.exists(self.outtxt): 
      cmd = self.rm + self.outtxt
      os.system(cmd)
      
    # run ps2ascii
    extractcmd = self.extractors['ps2ascii']+ '"'+file +'" '+ self.outtxt
    print ">>> converting "+filename+" >> "+self.outtxtfile
    extractcmdout = commands.getoutput(extractcmd) # must run as a sudo account

    # if no text file is generated, skip this document
    if not os.path.exists(self.outtxt):
      return 'ps2ascFail'

    # if output file is < 50 bytes, write to log and skip this document
    if os.path.getsize(self.outtxt) < 50:
      return 'ps2asciiFail'
 
    # return '0' if file is successfully extracted
    return 'ps2asciiSuccess'

  def file2textfile(self,file,outputdir):
    # generate output text file path from an input file name
    # e.g., file = '/db/file.pdf'
    filee = os.path.splitext(file) # ['/db/file','.pdf']
    files = os.path.split(file)    # ['/db,'file.pdf'']
    outtmp = filee[0]+'.txt'       # /db/file.txt
    outtxts = os.path.split(outtmp) # ['/db','file.txt']
    outtxtfile = outtxts[1].replace(' ','') # file.txt, remove space 
    outtxtpath = outputdir + outtxtfile # pdfboxrep/file.txt
   
    return outtxtpath
 
