#!/usr/bin/python
import runconfig
from exception import BadResourceError
import url_normalization
import urlparse

class URLFilter(object):

    def __init__(self,blacklistfile=None,domainblacklistfile=None):
 	# reason to reject a url
	self.rejectreason = None
	
	# blacklist URLs
	self.blacklist = []

	print 'loading blacklist file: ',blacklistfile
	if blacklistfile:
	    try:
   		with open(blacklistfile,'r') as blf:
        	    for line in blf:
            		# remove trailing character
            		line = line.strip('\n')
			self.blacklist.append(line)
	    except IOError,e:
		print 'File not exist: ',blacklistfile
		raise e
		
	# domain blacklist URLs
	self.domainblacklist = []

	print 'loading domainblacklistfile: ',domainblacklistfile
	if domainblacklistfile:
	    try:
		with open(domainblacklistfile,'r') as dblf:
		    for line in dblf:
		    	# remove trailing character
		    	line = line.strip('\n')
		    	self.domainblacklist.append(line)
	    except IOError,e:
		print 'File not exist: ',domainblacklistfile
		raise e
		
    def check(self,url):
	self.rejectreason = None

	# normalize URL
	norm_url = url_normalization.get_canonical_url(url)

	# deal with URL code
	if isinstance(norm_url,unicode):
	    norm_url = norm_url.encode('utf8')
	try:
	    norm_url.decode('utf8')
	except:
	    print 'cannot decode norm_url: %s' % norm_url
	    self.rejectreason = 'failtodecode'
	    return False

	# apply urllengthrule
	if not self.urllengthrule(url):
	    self.rejectreason = 'urllengthrule'
	    return False

	# apply blacklistrule
	if not self.blacklistrule(url):
	    self.rejectreason = 'blacklistrule'
	    return False
	
	# return true
	return True


    def blacklistrule(self,url):
	# check blacklist
	scheme,host,path,query,fragment = urlparse.urlsplit(url)
        for blurl in self.blacklist:
            if url.startswith(blurl):
                return False

	# check domain blacklist
	for dblurl in self.domainblacklist:
	    if host.endswith(dblurl):
		return False

        return True

    def urllengthrule(self,url,urlmaxlen=0):

	# if urlmax length is not specified, use the default one at runconfig.py
	if urlmaxlen == 0:
	    urlmaxlen = runconfig.urlmaxlen	

	if url == '':
	    return False

	if len(url) >= urlmaxlen:
	    return False

	return True

# Testing module
if __name__ == '__main__':
    urlfilter = URLFilter(blacklistfile=runconfig.blacklistfile,domainblacklistfile=runconfig.domainblacklistfile)
    urls = ['http://ieeexplore.ieee.org/ielx5/6294/4804034/04804053.pdf?tp=&arnumber=4804053&isnumber=4804034','http://www.wartsila.com/ss/Satellite?blobcol=urldata&blobheader=JPG&blobkey=id&blobtable=MungoBlobs&blobwhere=1278606917069&ssbinary=true','http://www.wartsila.com/ss/Satellite?blobcol=urldata&blobheader=JPG&blobkey=id&blobtable=MungoBlobs&blobwhere=1278606917069&ssbinary=trueaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa']
    for url in urls:
        print urlfilter.check(url), urlfilter.rejectreason
	

