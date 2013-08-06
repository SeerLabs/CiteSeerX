#!/usr/bin/env python

import gflags
import sys

from mysql_util import init_db, close_db

FLAGS = gflags.FLAGS
gflags.DEFINE_string('drop_keyword', '', '')

def usage(cmd):
  print 'Usage:', cmd, \
      '--drop_keyword="the term to be dropped"'

def check_args(argv):
  try:
    argv = FLAGS(argv)
  except gflags.FlagsError:
    print FLAGS

  if FLAGS.drop_keyword == '':
    usage(argv[0])
    raise Exception('--drop_keyword cannot be empty')

def main(argv):
  check_args(argv)

  db, cursor = init_db()
  try:
    cursor.execute("""DELETE FROM paper_keywords_noun WHERE ngram=%s""", (FLAGS.drop_keyword))
    db.commit()
  except:
    sys.stdout.write("""Error in deleting paper_keywords_noun where ngram = %s""" % (FLAGS.drop_keyword))
    db.rollback()

  close_db(db, cursor)

if __name__ == "__main__":
  main(sys.argv)

