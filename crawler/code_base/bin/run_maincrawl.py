#!/usr/local/bin/python2.6
import crawler.master
conf_file = 'maincrawl.conf'

m = crawler.master.Master()
m.start(conf_file, None, None)
