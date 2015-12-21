#!/usr/bin/python2.7

import multiprocessing
import nltk
import random
import sys
import time

from collections import defaultdict
import mysql_util

#from nltk.corpus import stopwords

def leaves(tree):
  """Finds NP (nounphrase) leaf nodes of a chunk tree."""
#  for subtree in tree.subtrees(filter = lambda t: t.node=='NP'):
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
  sentence_re = r'''(?x)    # set flag to allow verbose regexps
    (?:[A-Z])(?:\.[A-Z])+\.?  # abbreviations, e.g. U.S.A.
    | \w+(?:-\w+)*      # words with optional internal hyphens
    | \$?\d+(?:\.\d+)?%?    # currency and percentages, e.g. $12.40, 82%
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
  retries = 1
  while True:
    try:
      cursor.execute("""DELETE FROM paper_keywords_noun WHERE paper_id=%s""", (pid,))
      for ngram in term_ctr:
        cursor.execute("""INSERT INTO paper_keywords_noun (paper_id, ngram, count) VALUES (%s, %s, %s)""", (pid, ngram.decode('utf-8'), term_ctr[ngram]))
      db.commit()
    except Exception as e:
      sys.stdout.write("\nError in inserting keyphrases of paper_id %s to paper_keywords_noun\n" % (pid,))
      print e
      db.rollback()
      # wait for a while then retry
      retries -= 1
      if retries >= 0:
        time.sleep(random.randint(1, 10))
        continue
    break

def calc_term_ctr(paper_info):
  pid, title, abstract = paper_info

  term_ctr = {}
  if title is not None:
    term_ctr = gen_term_ctr(title, 3)
  if abstract is not None:
    term_ctr = merge_dictionary(term_ctr, gen_term_ctr(abstract, 1))

  return (pid, term_ctr)

def worker(worker_id, cmd_queue, job_queue):
  db, cursor = mysql_util.init_db()

  try:
    while True:
      # get data to process
      cmd_queue.put(('GET', None))
      paper_info = job_queue.get()
      if paper_info == None:
        break

      # process data
      pid, term_ctr = calc_term_ctr(paper_info)
      save_to_tbl(db, cursor, pid, term_ctr)

      # report the the dispatcher that we have done one
      cmd_queue.put(('DONE', pid))
  except KeyboardInterrupt as e:
    pass
  finally:
    mysql_util.close_db(db, cursor)
    print
    print 'Worker %d leaves' % worker_id

def dispatch_work(cmd_queue, job_queue, papers):
  print "Generating keyphrases..."
  num_done = 0
  dispatched_index = 0
  while num_done < len(papers):
    # waiting for worker's command (or report)
    cmd, data = cmd_queue.get()
    if cmd == 'DONE':
      # worker finished one paper
      num_done += 1
      print "\r%s (%d / %d)" % (data, num_done, len(papers)),
      sys.stdout.flush()
    elif cmd == 'GET':
      # worker wants a paper to process
      if dispatched_index < len(papers):
        paper_info = papers[dispatched_index]
        dispatched_index +=1
        job_queue.put(paper_info)
      else:
        job_queue.put(None) # signal the worker to leave

  job_queue.put(None) # signal the last worker to leave

def main(argv):
  NUM_PROCESSES = multiprocessing.cpu_count() * 2

  cmd_queue = multiprocessing.Queue()
  job_queue = multiprocessing.Queue()

  print "Create %d workers..." % NUM_PROCESSES
  for i in range(NUM_PROCESSES):
    multiprocessing.Process(target=worker, args=(i, cmd_queue, job_queue)).start()

  # fetch data from database
  db, cursor = mysql_util.init_db()
  try:
    if not mysql_util.does_table_exist(db, cursor, 'paper_keywords_noun'):
      cursor.execute('CREATE TABLE paper_keywords_noun ('
          'id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, '
          'paper_id varchar(100), '
          'ngram varchar(255), '
          'count int(11), '
          'INDEX (paper_id))')

    print "Fetching papers from database..."
    cursor.execute("""SELECT id, title, abstract FROM papers""")
    papers = cursor.fetchall()

    dispatch_work(cmd_queue, job_queue, papers)
  except KeyboardInterrupt as e:
    pass
  finally:
    mysql_util.close_db(db, cursor)
    print "Main process leaves"

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
