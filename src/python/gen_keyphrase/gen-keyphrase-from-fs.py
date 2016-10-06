#!/usr/bin/python2.7

import io
import os
import sys

import gen_keyphrase_core

def calc_term_ctr(contents):
    term_ctr = {}
    if contents is not None:
        term_ctr = gen_keyphrase_core.gen_term_ctr(contents, 1)
    return term_ctr


def main(argv):
    article_folder = './articles'
    print "Fetching papers from folder '%s'..." % (article_folder)
    for f in os.listdir(article_folder):
        if not os.path.isfile(os.path.join(article_folder, f)):
            continue
        contents = io.open(os.path.join(article_folder, f), encoding="utf-8").read()
        print '===Keyphrases of %s===' % (os.path.join(article_folder, f))
        #print calc_term_ctr(contents)
        for term, ctr in calc_term_ctr(contents).iteritems():
            print term, ':', ctr


if __name__ == "__main__":
    main(sys.argv)

