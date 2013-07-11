#
#  CiteSeerX external medatada database spec
#
#  JPFR
#
CREATE DATABASE csx_external_metadata CHARACTER SET utf8 COLLATE utf8_general_ci;

USE csx_external_metadata;

#
# Stores DBLP metadata.
#
CREATE TABLE dblp (
  id SERIAL,
  type VARCHAR(255),
  authors VARCHAR(255),
  numAuthors INTEGER,
  editor VARCHAR(255),
  title VARCHAR(255),
  booktitle VARCHAR(255),
  pages VARCHAR(100),
  year SMALLINT UNSIGNED,
  address VARCHAR(255),
  journal VARCHAR(255),
  volume SMALLINT UNSIGNED,
  number SMALLINT UNSIGNED,
  month VARCHAR(100),
  url VARCHAR(255),
  ee VARCHAR(255),
  cdrom VARCHAR(255),
  cite TEXT,
  publisher VARCHAR(255),
  note TEXT,
  crossref VARCHAR(255),
  isbn VARCHAR(255),
  series VARCHAR(255),
  school VARCHAR(255),
  chapter VARCHAR(255),
  dkey VARCHAR(255),
  numCites INTEGER,
  PRIMARY KEY (id),
  INDEX(title),
  UNIQUE INDEX(dkey)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=MyISAM;

#
# Stores CiteULike metadata.
# Right now we are just storing data from the linkout dataset provided by CiteULike.
# (http://www.citeulike.org/faq/data.adp)
#
CREATE table citeulike (
  citeulikeid BIGINT UNSIGNED NOT NULL,
  citeseerxid VARCHAR(100) NOT NULL,
  PRIMARY KEY (citeulikeid),
  UNIQUE INDEX (citeseerxid)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=MyISAM;

#
# Stores DBLP metadata.
#
CREATE TABLE acm (
  id BIGINT UNSIGNED NOT NULL,
  authors VARCHAR(255),
  title VARCHAR(255),
  year SMALLINT UNSIGNED,
  venue VARCHAR(255),
  url VARCHAR(255),
  pages VARCHAR(100),
  publication VARCHAR(255),
  PRIMARY KEY(id),
  INDEX(title)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=MyISAM;

GRANT ALL ON csx_external_metadata.* TO '$USERNAME$'@'$DOMAIN$' IDENTIFIED BY '$PASSWORD$';
