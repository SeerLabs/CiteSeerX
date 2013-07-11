#!/usr/bin/env python

import cPickle
import unittest
import sys
import time
import logging

sys.path.insert(0, '..')

from crawler.due import MercatorDue
from crawler.resource import Resource

class Receiver(object):
    def __init__(self):
        self.results = ''
        
    def put(self, r):
        logging.debug('+ %s.%d ' % (r.md5, r.batch))
        self.results += '%s.%d ' % (r.md5, r.batch)
        
class TestMercatorDue(unittest.TestCase):

    def test_merge_check(self):
        params = ['/tmp/mercator.due', 3]
        due = MercatorDue(params)
        due.receiver = Receiver()
        due.keep_file = True
        
        md5_list = [
            # verify immediate-mode
            'B3.0', 
            'B2.1',
            'B2.2', # verify immediate-mode same md5, larger batch (B2.1 was also forwarded) 
            'B2.0', # verify immediate-mode buffer hit
            'B1.0',
                    
            # verify sorting
            'A3.0',
            'A2.0',
            'A1.0', 
            
            'A4.1',
            'A4.2', # verify buffer hit with larger batch (A4.1 will not be forwarded)
            'A4.0', # verify buffer hit with smaller batch 
            'A3.1', # verify merge with same md5, larger batch
            'A2.0', # verify merge with same md5, same batch 
            
            'A5.0', # verify merge with different md5
            'A3.0', # verify merge with same md5, smaller batch
            'A0.0'
            ]
        for m in md5_list:
            r = Resource(None, None, 'http://www.abc.om', False, 0, 0)
            
            # replace original md5/batch with given fake value
            r.md5 = m.split('.')[0]
            r.batch = int(m.split('.')[1])

            due.put(r)
        
        time.sleep(1) # wait for the merging to finish
        
        """
        for i in range(1,5):
            print '--- data.%d ---' % i
            f = open('/tmp/mercator.due/data.%d' % i, 'r')
            while True:    
                try:
                    print cPickle.load(f)
                except EOFError:
                    break
            f.close()
        """
        
        print due        
        
        self.assertEqual(due.file_version, 4)
        self.assertEqual(len(due._buffer), 0)
        
        self.assertEqual(due.receiver.results, 'B3.0 B2.1 B2.2 B1.0 A1.0 A2.0 A3.0 A3.1 A4.2 A0.0 A5.0 ')
        


if __name__ == '__main__':
    logger = logging.getLogger('')
    logger.setLevel(logging.WARNING)
    #logger.setLevel(logging.DEBUG)
    h = logging.StreamHandler(sys.stderr)       
    logger.addHandler(h) 

    unittest.main()
    