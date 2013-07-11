import os
import sys

os.environ['PYTHON_EGG_CACHE'] = '/var/www/python-eggs'
os.environ['DJANGO_SETTINGS_MODULE'] = 'citeseerx_crawl.settings'

import django.core.handlers.wsgi
application = django.core.handlers.wsgi.WSGIHandler()
