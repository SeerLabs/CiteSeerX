# Reposity API interface
# Douglas Jordan
import web
import os
import errno
import os.path

# TODO: Move to config
# clean up
# use logging lib
# use config parser

repositorypath = '/data/repository/'
api_key = "c1t3s33r"

urls = ('/*', 'index', '/document', 'document')

def mkdir_p(path):
    try:
        os.makedirs(path)
    except OSError, e:
        if e.errno == errno.EEXIST:
            pass
        else:
            raise

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
        try:
            # print(web.ctx.home + web.ctx.homepath + web.ctx.fullpath)
            # Get query string parameters
            #key = web.input().get('key')
            repid = str(web.input().get('repid'))
            doi = str(web.input().get('doi'))
            filetype = str(web.input().get('type'))
            query = str(web.input().get('q'))

            #print web.input()
            # If not all the keys are specified return 404
            if None in (repid, doi):
                return web.webapi.NotFound()

            #if key != api_key:
            #    return web.webapi.NotFound()

            # Main getDocument logic
            filename = repositorypath + repid
            filealias = doi + '.' + filetype

            for x in doi.split('.'):
                filename = os.path.join(filename, x)

            if (query == 'filetypes'):
                files = os.listdir(filename)
                types = set()
                for f in files:
                    extension = os.path.splitext(f)[1]
                    types.add(extension.replace('.', ''))
                return ",".join(types)

            filename = os.path.join(filename, filealias)
            if os.path.isfile(filename):
                try:
                    #print filename
                    getFile = file(filename, 'r')
                    web.webapi.header('Content-Disposition', 'inline; filename=%s' % filealias)
                    web.webapi.header('Content-Type', 'application/%s' % filetype)
                    web.webapi.header('Content-length', os.stat(filename).st_size)

                    #print os.stat(filename).st_size
                    # Return the file data
                    return getFile.read()
                except IOError:
                    return web.webapi.NotFound('File Not Found')

            else:
                return web.webapi.NotFound('File Not Found')
        except:
            print(web.ctx.home + web.ctx.homepath + web.ctx.fullpath)

    def POST(self):
	#['doi', 'type', 'version', 'file', 'repid']
        doi = web.input().get('doi')
        repid = web.input().get('repid')
        File = web.input().get('file')
        Type = web.input().get('type').lower()
	version = web.input().get('version')

        if None in (doi, repid, File, Type):
            return web.webapi.NotFound()

        print "we got post + {0}".format(doi)
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
                # create directory if it doesn't already exist
                mkdir_p(filename)
                #print "Directory %s" % filename
                filename = os.path.join(filename, filealias)
                # no overwrites possible
                if os.path.isfile(filename):
                    return web.webapi.BadRequest("File already exists")
                try:
                    f = open(filename, 'w') #262144)
                    f.write(File)
                    f.close()
                    return web.webapi.OK()
                except IOError, e:
                    print "File write error %s" % e.msg
                    return web.webapi.NotAcceptable()

app = web.application(urls, globals())
application = app.wsgifunc()

if __name__ == "__main__":
    app.run()
