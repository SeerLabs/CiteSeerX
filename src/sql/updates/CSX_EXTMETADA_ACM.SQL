USE csx_external_metadata;

ALTER TABLE citeulike DROP INDEX citeseerxid;

ALTER TABLE citeulike ADD INDEX (citeseerxid);

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