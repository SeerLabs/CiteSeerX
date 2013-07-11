#
#  Partial CiteSeerX main database spec (Table Search)
#
#  Shuyi
#

USE citeseerx;

CREATE TABLE eTables (
  id SERIAL,
  proxyID VARCHAR(100) NOT NULL,
  inDocID INT,
  caption VARCHAR(200),
  content TEXT,
  footNote VARCHAR(200),
  refText VARCHAR(200),
  paperid VARCHAR(100) NOT NULL,
  pageNum int NOT NULL,
  updateTime TIMESTAMP NOT NULL,
  PRIMARY KEY(id),
  INDEX(paperid),
  INDEX(proxyID),
  FOREIGN KEY(paperid) REFERENCES papers(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;
