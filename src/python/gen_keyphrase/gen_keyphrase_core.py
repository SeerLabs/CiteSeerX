#!/usr/bin/env python
# -*- coding: utf-8 -*-

# Hung-Hsuan Chen <hhchen@ncu.edu.tw>
# Creation Date : 10-06-2016

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import collections
import nltk

def leaves(tree):
    """Finds NP (nounphrase) leaf nodes of a chunk tree."""
#    for subtree in tree.subtrees(filter = lambda t: t.node=='NP'):
    for subtree in tree.subtrees(filter = lambda t: t.label()=='NP'):
        yield subtree.leaves()


def normalize(word, lemmatizer):
    """Normalises words to lowercase and stems and lemmatizes it."""
    word = word.lower()
    #word = stemmer.stem_word(word)
    word = lemmatizer.lemmatize(word)
    return word


def acceptable_word(word, stopwords):
    """Checks conditions for acceptable word: length, stopword."""
    accepted = bool(2 <= len(word) <= 40 and word.lower() not in stopwords)
    return accepted


def get_terms(tree, lemmatizer, stopwords):
    for leaf in leaves(tree):
        term = [ normalize(w, lemmatizer) for w,t in leaf if acceptable_word(w, stopwords) ]
        yield term


def gen_keyphrases(text):
    # Used when tokenizing words
    sentence_re = r'''(?x)        # set flag to allow verbose regexps
        (?:[A-Z])(?:\.[A-Z])+\.?    # abbreviations, e.g. U.S.A.
        | \w+(?:-\w+)*            # words with optional internal hyphens
        | \$?\d+(?:\.\d+)?%?        # currency and percentages, e.g. $12.40, 82%
        | \.\.\.                # ellipsis
        | [][.,;"'?():-_`]        # these are separate tokens
    '''

    lemmatizer = nltk.WordNetLemmatizer()
    #stemmer = nltk.stem.porter.PorterStemmer()

    #Taken from Su Nam Kim Paper...
    grammar = r"""
        NBAR:
            {<NN.*|JJ>*<NN.*>}    # Nouns and Adjectives, terminated with Nouns

        NP:
            {<NBAR>}
            {<NBAR><IN><NBAR>}    # Above, connected with in/of/etc...
    """
    chunker = nltk.RegexpParser(grammar)

    toks = nltk.regexp_tokenize(text, sentence_re)
    postoks = nltk.tag.pos_tag(toks)

    tree = chunker.parse(postoks)

    stopwords = nltk.corpus.stopwords.words('english')
    terms = get_terms(tree, lemmatizer, stopwords)
    return terms


def gen_term_ctr(text, addend):
    term_ctr = collections.defaultdict(int)

    terms = gen_keyphrases(text)
    for term in terms:
        if 1 < len(term) <= 5:
            term_ctr[' '.join(term)] += addend
    return dict(term_ctr)
