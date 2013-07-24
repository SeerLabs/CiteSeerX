#!/usr/bin/env python

import nltk
import sys

from collections import defaultdict
from mysql_util import init_db, close_db

#from nltk.corpus import stopwords

def leaves(tree):
  """Finds NP (nounphrase) leaf nodes of a chunk tree."""
  for subtree in tree.subtrees(filter = lambda t: t.node=='NP'):
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
  sentence_re = r'''(?x)    # set flag to allow verbose regexps
    ([A-Z])(\.[A-Z])+\.?  # abbreviations, e.g. U.S.A.
    | \w+(-\w+)*      # words with optional internal hyphens
    | \$?\d+(\.\d+)?%?    # currency and percentages, e.g. $12.40, 82%
    | \.\.\.        # ellipsis
    | [][.,;"'?():-_`]    # these are separate tokens
  '''

  lemmatizer = nltk.WordNetLemmatizer()
  #stemmer = nltk.stem.porter.PorterStemmer()

  #Taken from Su Nam Kim Paper...
  grammar = r"""
    NBAR:
      {<NN.*|JJ>*<NN.*>}  # Nouns and Adjectives, terminated with Nouns

    NP:
      {<NBAR>}
      {<NBAR><IN><NBAR>}  # Above, connected with in/of/etc...
  """
  chunker = nltk.RegexpParser(grammar)

  toks = nltk.regexp_tokenize(text, sentence_re)
  postoks = nltk.tag.pos_tag(toks)

  tree = chunker.parse(postoks)

  stopwords = nltk.corpus.stopwords.words('english')
  terms = get_terms(tree, lemmatizer, stopwords)
  return terms

def gen_term_ctr(text, addend):
  term_ctr = defaultdict(int)

  terms = gen_keyphrases(text)
  for term in terms:
    if 1 < len(term) <= 5:
      term_ctr[' '.join(term)] += addend
  return term_ctr

def merge_dictionary(a, b):
  return dict((n, a.get(n,0)+b.get(n,0)) for n in set(a)|set(b))

def save_to_tbl(db, cursor, pid, term_ctr):
  for ngram in term_ctr:
    try:
      cursor.execute("""INSERT INTO paper_keywords_noun (paper_id, ngram, count) VALUES (%s, %s, %s)""", (pid, ngram.decode('utf-8'), term_ctr[ngram]))
      db.commit()
    except:
      sys.stdout.write("\nError in inserting paperkeyords_wiki of paper_id %s, ngram %s, and count %d\n" % (pid, ngram, term_ctr[ngram]))
      db.rollback()

def main(argv):
  db, cursor = init_db()
  sys.stdout.write("Generating paper_keyphrase\n")
  cursor.execute("""SELECT id, title, abstract FROM papers""")
  rows = cursor.fetchall()
  for i, paper_info in enumerate(rows):
    sys.stdout.write("\r%d / %d" % (i+1, len(rows)))
    pid = paper_info[0]
    title = paper_info[1]
    abstract = paper_info[2]

    term_ctr = {}
    if title is not None:
      term_ctr = gen_term_ctr(title, 3)
    if abstract is not None:
      term_ctr = merge_dictionary(term_ctr, gen_term_ctr(abstract, 1))

    save_to_tbl(db, cursor, pid, term_ctr)
  print ''

  close_db(db, cursor)

if __name__ == "__main__":
  main(sys.argv)

from nose.tools import assert_equal

class TestAll():
  def test_merge_dictionary(self):
    d1 = {'a':5, 'b':2, 'c':1}
    d2 = {'b':4, 'c':1, 'd':10}
    d3 = merge_dictionary(d1, d2)
    assert_equal(len(d3), 4)
    assert_equal(d3['a'], 5)
    assert_equal(d3['b'], 6)
    assert_equal(d3['c'], 2)
    assert_equal(d3['d'], 10)


