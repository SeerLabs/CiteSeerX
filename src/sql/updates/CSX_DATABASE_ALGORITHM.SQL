#
#  CiteSeerx Database Specification for Algorithm Search
#
#  Sumit Bhatia
#

USE citeseerx;

CREATE TABLE eAlgorithms (
  id SERIAL NOT NULL,
  proxyID VARCHAR(100) NOT NULL,
  inDocID INT,
  caption VARCHAR(500),
  synopsis VARCHAR(2000),
  reftext VARCHAR(500),
  paperid VARCHAR(100) NOT NULL,
  pageNum int NOT NULL,
  PRIMARY KEY(id),
  ncites int,
  year int,
  INDEX(paperid),
  FOREIGN KEY(paperid) REFERENCES papers(id)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;
