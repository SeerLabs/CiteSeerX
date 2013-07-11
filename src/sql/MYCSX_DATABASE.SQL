#
#  MyCiteSeerX database spec
#
#  IGC
#

CREATE DATABASE myciteseerx CHARACTER SET utf8 COLLATE utf8_general_ci;

USE myciteseerx;


CREATE TABLE users (
  userid VARCHAR(100) NOT NULL,
  password VARCHAR(255) NOT NULL,
  firstName VARCHAR(100) NOT NULL,
  middleName VARCHAR(100),
  lastName VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL,
  affil1 VARCHAR(255),
  affil2 VARCHAR(255),
  enabled TINYINT NOT NULL DEFAULT 1,
  country VARCHAR(100),
  province VARCHAR(100),
  webPage VARCHAR(255),
  internalid BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  appid VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY(userid),
  INDEX(firstName),
  INDEX(lastName),
  INDEX(email),
  INDEX(enabled),
  INDEX(country),
  INDEX(province),
  INDEX(webPage),
  INDEX(internalid),
  INDEX(appid)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE authorities (
  id SERIAL,
  userid VARCHAR(100) NOT NULL,
  authority VARCHAR(100) NOT NULL,
  PRIMARY KEY(id),
  INDEX(userid),
  INDEX(authority),
  FOREIGN KEY(userid) REFERENCES users(userid)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE submissionJobs (
  JID VARCHAR(255) NOT NULL,
  UID VARCHAR(100) NOT NULL,
  URL VARCHAR(255) NOT NULL,
  depth INT UNSIGNED NOT NULL DEFAULT 1,
  time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status INT NOT NULL DEFAULT -1,
  statusTime TIMESTAMP NOT NULL,
  PRIMARY KEY(JID),
  INDEX(UID),
  INDEX(URL),
  INDEX(time),
  INDEX(status),
  FOREIGN KEY(UID) REFERENCES users(userid)
    ON DELETE RESTRICT ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE submissionComponents (
  id SERIAL,
  JID VARCHAR(255) NOT NULL,
  URL VARCHAR(255) NOT NULL,
  status INT NOT NULL,
  time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  DID VARCHAR(100),
  PRIMARY KEY(id),
  INDEX(JID),
  INDEX(URL),
  INDEX(status),
  INDEX(DID),
  FOREIGN KEY(JID) REFERENCES submissionJobs(JID)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE activation (
  id SERIAL,
  userid VARCHAR(100) NOT NULL,
  code VARCHAR(100) NOT NULL,
  created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  INDEX(userid),
  INDEX(code),
  INDEX(created),
  FOREIGN KEY(userid) REFERENCES users(userid)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE invitations (
  id SERIAL,
  ticket VARCHAR(50) NOT NULL,
  PRIMARY KEY(id),
  INDEX(ticket)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE configuration (
  param VARCHAR(100) NOT NULL,
  value VARCHAR(100) NOT NULL,
  PRIMARY KEY(param),
  INDEX(value)
);



CREATE TABLE collections (
  id SERIAL,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255),
  UID VARCHAR(100) NOT NULL,
  deleteAllowed BOOLEAN NOT NULL DEFAULT 1,
  PRIMARY KEY(id),
  INDEX(UID),
  FOREIGN KEY(UID) REFERENCES users(userid)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE collection_notes (
  id SERIAL,
  CID BIGINT UNSIGNED NOT NULL,
  UID VARCHAR(100) NOT NULL,
  note TEXT NOT NULL,
  PRIMARY KEY(id),
  INDEX(CID),
  FOREIGN KEY(CID) REFERENCES collections(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(UID) REFERENCES users(userid)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE papers_in_collection (
  CID BIGINT UNSIGNED NOT NULL,
  PID VARCHAR(100) NOT NULL,
  UID VARCHAR(100) NOT NULL,
  PRIMARY KEY(CID, PID),
  FOREIGN KEY(CID) REFERENCES collections(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(UID) REFERENCES users(userid)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE paper_notes (
  id SERIAL,
  CID BIGINT UNSIGNED NOT NULL,
  PID VARCHAR(100) NOT NULL,
  UID VARCHAR(100) NOT NULL,
  note TEXT NOT NULL,
  PRIMARY KEY(id),
  INDEX(CID),
  INDEX(PID),
  FOREIGN KEY(CID, PID) REFERENCES papers_in_collection(CID, PID)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(UID) REFERENCES users(userid)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE monitors (
  id SERIAL,
  userid VARCHAR(100) NOT NULL,
  paperid VARCHAR(100) NOT NULL,
  PRIMARY KEY(id),
  INDEX(userid),
  INDEX(paperid),
  FOREIGN KEY(userid) REFERENCES users(userid)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE tags (
  id SERIAL,
  userid VARCHAR(100) NOT NULL,
  paperid VARCHAR(100) NOT NULL,
  tag VARCHAR(50) NOT NULL,
  added TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  INDEX(userid),
  INDEX(paperid),
  INDEX(tag),
  INDEX(added),
  FOREIGN KEY(userid) REFERENCES users(userid)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;

CREATE TABLE groups (
  id SERIAL,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255),
  owner VARCHAR(100) NOT NULL,
  authority VARCHAR(100) NOT NULL,
  PRIMARY KEY(id),
  FOREIGN KEY(owner) REFERENCES users(userid)
    ON DELETE CASCADE ON UPDATE CASCADE
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;

CREATE TABLE group_members (
  groupid BIGINT UNSIGNED NOT NULL,
  userid VARCHAR(100) NOT NULL,
  validating BOOLEAN NOT NULL DEFAULT true,
  PRIMARY KEY (groupid, userid),
  FOREIGN KEY(groupid) REFERENCES groups(id)
    ON DELETE CASCADE,
  FOREIGN KEY(userid) REFERENCES users(userid)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;

CREATE TABLE acl_sid (
  id SERIAL,
  sid VARCHAR(100) NOT NULL,
  principal BOOLEAN NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY(sid, principal)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;

CREATE TABLE acl_class (
 id SERIAL,
 class VARCHAR(100) NOT NULL,
 PRIMARY KEY(id),
 UNIQUE KEY(class)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;

CREATE TABLE acl_object_identity (
  id SERIAL,
  object_id_class BIGINT UNSIGNED NOT NULL,
  object_id_identity BIGINT UNSIGNED NOT NULL,
  parent_object BIGINT UNSIGNED,
  owner_sid BIGINT UNSIGNED,
  entries_inheriting BOOLEAN NOT NULL,
  PRIMARY KEY(id),
  UNIQUE KEY(object_id_class, object_id_identity),
  INDEX(parent_object),
  INDEX(object_id_class),
  INDEX(owner_sid),
  FOREIGN KEY(parent_object) REFERENCES acl_object_identity(id),
  FOREIGN KEY(object_id_class) REFERENCES acl_class(id),
  FOREIGN KEY(owner_sid) REFERENCES acl_sid(id)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;

CREATE TABLE acl_entry (
  id SERIAL,
  acl_object_identity BIGINT UNSIGNED NOT NULL,
  ace_order INTEGER NOT NULL,
  sid BIGINT UNSIGNED NOT NULL,
  mask INTEGER NOT NULL,
  granting BOOLEAN NOT NULL,
  audit_success BOOLEAN NOT NULL,
  audit_failure BOOLEAN NOT NULL,
  UNIQUE KEY(acl_object_identity, ace_order),
  INDEX(acl_object_identity),
  INDEX(sid),
  FOREIGN KEY(acl_object_identity) REFERENCES acl_object_identity(id),
  FOREIGN KEY(sid) REFERENCES acl_sid(id)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;

CREATE TABLE indexTime (
    param VARCHAR(255) NOT NULL,
    lastupdate TIMESTAMP NOT NULL,
    PRIMARY KEY(param)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;

DELIMITER //

# Procedure to be used by ACL module to retrieve the id of an inserted row
CREATE PROCEDURE identity()
    SELECT LAST_INSERT_ID();//

CREATE TRIGGER updateSubmissionStatusTime BEFORE UPDATE ON submissionJobs
    FOR EACH ROW BEGIN
        IF NEW.status != OLD.status THEN
            SET NEW.statusTime=CURRENT_TIMESTAMP;
        END IF;
    END;//

DELIMITER ;


INSERT INTO configuration VALUES ('newAccountsEnabled', 'true');
INSERT INTO configuration VALUES ('urlSubmissionsEnabled', 'false');
INSERT INTO configuration VALUES ('correctionsEnabled', 'false');
INSERT INTO configuration VALUES ('groupsEnabled', 'false');
INSERT INTO configuration VALUES ('peopleSearchEnabled', 'false');
INSERT INTO configuration VALUES ('personalPortalEnabled', 'false');


GRANT ALL ON myciteseerx.* TO '$USERNAME$'@'$DOMAIN$' IDENTIFIED BY '$PASSWORD$';
