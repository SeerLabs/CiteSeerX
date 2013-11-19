
#!/usr/bin/python
# Reposity API interface

import web
import os
import errno

#if re.compile('^\d{1,4}.\d{1,4}.\d{1,4}.\d{1,4}.\d{1,4}$').match(doi):
# TODO: Move to config

repositorypath = '/data/repository/'
api_key = "c1t3s33r"

urls = ('/', 'index',
        '/document', 'document')

# http://stackoverflow.com/questions/600268/mkdir-p-functionality-in-python


def mkdir_p(path):
    try:
        os.makedirs(path)
    except OSError, e:
        if e.errno == errno.EEXIST:
            pass
        else:
            raise

# reading chunks
def fbuffer(f, chunk_size=262144):
    while True:
        chunk = f.read(chunk_size)
        if not chunk:
            break
        yield chunk

class index:
    def GET(self):
        return "Repository RESTful API"

class document:
    # Used for retreiving documents
    # example: http://csxrepo01.ist.psu.edu/document?key=api_key&repid=rep1&doi=10.1.1.130.157&type=pdf
    def GET(self):
        # Get query string parameters
        #key = web.input().get('key')
        repid = web.input().get('repid')
        doi = web.input().get('doi')
        filetype = web.input().get('type')
        filetype = 'pdf'
        query = web.input().get('q')
	if (query == 'filetypes'):
            return "pdf"
	print web.input()
        # If not all the keys are specified return 404
        if None in (repid, doi, filetype):
            return web.webapi.NotFound()

        #if key != api_key:
        #    return web.webapi.NotFound()

        # Main getDocument logic
        filename = repositorypath + repid
        filealias = doi + '.' + filetype

        for x in doi.split('.'):
            filename = os.path.join(filename, x)

        filename = os.path.join(filename, filealias)
        if os.path.isfile(filename):
            try:
                getFile = file(filename, 'r')
                web.webapi.header('Content-Disposition', 'attachment; filename=%s' % filealias)
                web.webapi.header('Content-Type', 'application/%s' % filetype)
		web.webapi.header('Content-length', os.stat(filename).st_size)

		print os.stat(filename).st_size
                # Return the file data
                return getFile.read()
            except IOError:
                return web.webapi.NotFound('File Not Found')

        else:
            return web.webapi.NotFound('File Not Found')

    def POST(self):
#['doi', 'type', 'version', 'file', 'repid']
        doi = web.input().get('doi')
        repid = web.input().get('repid')
        File = web.input().get('file')
        Type = web.input().get('type').lower()
	version = web.input().get('version')

        if None in (doi, repid, File, Type):
            return web.webapi.NotFound()

        allowedTypes = ['pdf', 'ps', 'xml', 'header', 'met', 'parscit', 'txt']
        if Type in allowedTypes:
            if version == None:
                #upload_version(File, doi, Type, version) INGESTION
                return web.input().keys()
            else:
                filealias = doi + 'v%s.%s' % (version, Type)
                filename = repositorypath + repid
                for x in doi.split('.'):
                    filename = os.path.join(filename, x)
                # create directory if it does'nt already exist
                mkdir_p(filename)
                print "Directory %s" % filename
                filename = os.path.join(filename, filealias)
                # no overwrites possible
                if os.path.isfile(filename):
                    return web.webapi.BadRequest("File already exists")
                try:
                    f = open(filename, 'w') #262144)
                    f.write(File)
                    f.close()
                    #fsize = os.path.getsize(filename)
                    #chk = open(filename, 'r')
                    #hsha1 = hashlib.sha1()
                    #hsha1.update(chk.read())
                    #chksum = hsha1.hexdigest()
                    #chk.close()
                    #update_repository_add(doi, type, chksum, fsize)
                    return web.webapi.OK()
                except IOError, e:
                    print "File write error %s" % e.msg
                    return web.webapi.NotAcceptable()

if __name__ == "__main__":
    app = web.application(urls, globals())
    app.run()
