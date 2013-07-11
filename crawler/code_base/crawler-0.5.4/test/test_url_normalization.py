#!/usr/bin/env python

import unittest
import sys

sys.path.insert(0, '..')

from crawler.url_normalization import get_canonical_url

class TestUrlNormalization(unittest.TestCase):
    def testget_canonical_url(self):
        pairs = [
            # Converting the scheme and host to lower case.
            ('HTTP://www.GOOGLE.com/', 'http://www.google.com/'),
            
            # Adding trailing /
            ('http://www.google.com', 'http://www.google.com/'),
            ('http://www.google.com/', 'http://www.google.com/'),
            ('http://www.google.com/abc', 'http://www.google.com/abc/'),
            ('http://www.google.com/abc.index', 'http://www.google.com/abc.index'),
            ('http://www.google.com/abc.index/', 'http://www.google.com/abc.index/'),
            ('http://www.google.com:80', 'http://www.google.com:80/'),
            
            # special characters in the path should be quoted
            # space is converted to %20, not '+'
            # '+' is quoted too
            # single '%' will be quoted, the one in '%xx' format will not  
            ('http://www.google.com/a b+c/~ccc/', 'http://www.google.com/a%20b%2Bc/%7Eccc/'),
            ('http://www.google.com/a%b/', 'http://www.google.com/a%25b/'),
            ('http://www.google.com/a%20b/%7Eccc/', 'http://www.google.com/a%20b/%7Eccc/'),            
            
            # query parameters are encoded & sorted
            # space (also %20) is converted to '+'
            # '+' is not quoted
            # single '%' will be quoted, the one in '%xx' format will not (except %20)
            ('http://www.google.com/?q=y&a=~x', 'http://www.google.com/?a=%7Ex&q=y'),
            ('http://www.google.com/?q=a.+b c%20d', 'http://www.google.com/?q=a.+b+c+d'),
            ('http://www.google.com/?q=a%b', 'http://www.google.com/?q=a%25b'),
            ('http://www.google.com/?q=a%20b%7Ec', 'http://www.google.com/?q=a+b%7Ec'),            
            
            # Capitalizing letters in escape sequences.
            ('http://www.google.com/a%c2%b1b/', 'http://www.google.com/a%C2%B1b/'),
            
            # Removing the fragment
            ('http://www.google.com/?a=x#b', 'http://www.google.com/?a=x'),
            
            # Removing dot-segments.
            ('http://www.example.com/../a/b/../c/./d.html', 'http://www.example.com/a/c/d.html'),
                        
            ('http://www.google.com/..', 'http://www.google.com/'),
            ('http://www.google.com/../', 'http://www.google.com/'),
            ('http://www.google.com/abc/abc/..', 'http://www.google.com/abc/'),
            ('http://www.google.com/abc/abc/../', 'http://www.google.com/abc/'),                        
            ('http://www.google.com/abc/../abc', 'http://www.google.com/abc/'),
            ('http://www.google.com/abc/../abc/', 'http://www.google.com/abc/'),
            ('http://www.google.com/index.htm/abc/..', 'http://www.google.com/index.htm/'),
            ('http://www.google.com/index.htm/abc/../', 'http://www.google.com/index.htm/'),            
            ('http://www.google.com/abc/../index.htm', 'http://www.google.com/index.htm'),
            ('http://www.google.com/abc/../index.htm/', 'http://www.google.com/index.htm/'),

            ('http://www.google.com/.', 'http://www.google.com/'),
            ('http://www.google.com/./', 'http://www.google.com/'),
            ('http://www.google.com/abc/.', 'http://www.google.com/abc/'),
            ('http://www.google.com/abc/./', 'http://www.google.com/abc/'),                        
            ('http://www.google.com/index.htm/.', 'http://www.google.com/index.htm/'),
            ('http://www.google.com/index.htm/./', 'http://www.google.com/index.htm/'),
            ('http://www.google.com/abc/./abc', 'http://www.google.com/abc/abc/'),
            ('http://www.google.com/abc/./abc/', 'http://www.google.com/abc/abc/'),
            ('http://www.google.com/abc/./index.htm', 'http://www.google.com/abc/index.htm'),
            ('http://www.google.com/abc/./index.htm/', 'http://www.google.com/abc/index.htm/'),
            
            # add / to host when ? is following the host
            ('http://www.google.com?q=y', 'http://www.google.com/?q=y'),                        
            
            # remove blank parameters
            ('http://www.google.com/?q=&a', 'http://www.google.com/'),
            
            # Removing the "?" when the querystring is empty
            ('http://www.google.com/?', 'http://www.google.com/'),                        
            ]

        for (x,y) in pairs:
            u = get_canonical_url(x)
            self.assertEqual(u, y)

if __name__ == '__main__':
    unittest.main()
    