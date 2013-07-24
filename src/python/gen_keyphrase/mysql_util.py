import MySQLdb
import MySQLdb.cursors

def init_db():
  db_info = get_db_info()
  db = MySQLdb.connect(host=db_info['host'], user=db_info['user'], \
      passwd=db_info['passwd'], unix_socket=db_info['socket'])
  cursor = db.cursor()
  if not is_db_exists(db, cursor, db_info['db']):
    cursor.execute("CREATE DATABASE " + db_info['db'] + " DEFAULT CHARACTER SET utf8")
  db.select_db(db_info['db'])

  db.set_character_set('utf8')
  cursor.execute('SET NAMES utf8;')
  cursor.execute('SET CHARACTER SET utf8;')
  cursor.execute('SET character_set_connection=utf8;')
  return db, cursor

def close_db(db, cursor):
  cursor.close()
  db.close()

def is_this_a_setting_line(line):
  line = line.strip()
  if line == '':
    return False
  if line[0] == '#':
    return False
  return True

def get_db_info():
  db_info = { }
  f = open('settings/mysql_settings', 'r')
  for line in f:
    if not is_this_a_setting_line(line):
      continue
    field, val = line.strip().split(':')
    field = field.strip()
    val = val.strip()
    if (val[0] == '"' and val[-1] == '"') or (val[0] == "'" and val[-1] == "'"):
      val = val[1:-1]
    db_info[field] = val
  f.close()
  return db_info

def is_db_exists(db, cursor, db_name):
  cursor.execute("""SELECT schema_name FROM information_schema.schemata """ + \
     """WHERE schema_name = %s""", (db_name))
  row = cursor.fetchone()
  return row != None

def is_tbl_exists(db, cursor, table_name):
  db_info = get_db_info()
  cursor.execute("""SELECT * FROM information_schema.tables """ + \
      """WHERE table_schema = %s AND table_name = %s""", (db_info['db'], table_name))
  row = cursor.fetchone()
  return row != None

def drop_tbl(db, cursor, table_name):
  if is_tbl_exists(db, cursor, table_name):
    cursor.execute("""DROP TABLE """ + table_name)


