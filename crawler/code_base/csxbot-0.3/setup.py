#!/usr/bin/env python

from distutils.core import setup

setup(name='csxbot',
      version='0.3',
      description='CSX Crawler (a crawler extension and a django project)',
      author='Shuyi Zheng',
      author_email='zhengshuyi@gmail.com',
      packages=['csxcrawler', 'citeseerx_crawl', 'citeseerx_crawl.main_crawl']
      )
