#
# Includes tables to support links to external sources fro the document summary page.
#
# Juan Pablo Fernandez Ramirez
#

USE citeseerx;

CREATE TABLE link_types (
  label VARCHAR(50) NOT NULL,
  baseURL VARCHAR(255) NOT NULL,
  PRIMARY KEY (label)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;

CREATE TABLE elinks (
  paperid VARCHAR(100) NOT NULL,
  label VARCHAR(50) NOT NULL,
  url   VARCHAR(255) NOT NULL,
  PRIMARY KEY (paperid, label),
  INDEX(paperid),
  INDEX(label),
  FOREIGN KEY(paperid) REFERENCES papers(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(label) REFERENCES link_types(label)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;
 
INSERT INTO link_types VALUES ('DBLP', 'http://www.informatik.uni-trier.de/~ley/');
INSERT INTO link_types VALUES ('CiteULike ', 'http://www.citeulike.org/article/');
