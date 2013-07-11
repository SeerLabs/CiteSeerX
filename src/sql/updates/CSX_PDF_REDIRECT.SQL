#
# Includes tables to support redirects of download links to other repositories.
#
# Pradeep Teregowda
#

USE citeseerx;


CREATE TABLE redirecttemplates (
        label VARCHAR(20),
        urltemplate VARCHAR(1024),
        PRIMARY KEY (label)
)

CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;





# paper id (our id), label (repository label), externalrepoid (id in the other repo), url (if url is provided), paperlink (if url is to the paper)
# externalrepoid or url can be used (or both) - label is important if we have a generate url scenario ( http://www.ieee.org/?paperid=X ), we need
# templates for this ?

CREATE TABLE redirectpdf (
  paperid VARCHAR(100) NOT NULL,
  label VARCHAR(20) NOT NULL,
  externalrepoid VARCHAR(255),
  url VARCHAR(1024),
  PRIMARY KEY (paperid),
  INDEX(paperid),
  INDEX(externalrepoid),
  FOREIGN KEY(paperid) REFERENCES papers(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(label) REFERENCES redirecttemplates(label)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;

INSERT INTO redirecttemplates VALUES ('IEEE', 'http://www.ieee.org/?_ID_');
