#
#  CiteSeerX citation cluster and graph database spec.
#
#  IGC
#

CREATE DATABASE csx_citegraph CHARACTER SET utf8 COLLATE utf8_general_ci;

USE csx_citegraph;


CREATE TABLE clusters (
	id SERIAL,
	size INT UNSIGNED,
	incollection TINYINT NOT NULL DEFAULT 0,
	cauth TEXT,
	ctitle VARCHAR(255),
	cvenue VARCHAR(255),
	cventype VARCHAR(20),
	cyear INT UNSIGNED,
	cpages VARCHAR(20),
	cpublisher VARCHAR(100),
	cvol INT UNSIGNED,
	cnum INT UNSIGNED,
	ctech VARCHAR(100),
	observations MEDIUMTEXT,
	selfCites INT UNSIGNED NOT NULL DEFAULT 0,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(id),
	INDEX(size),
	INDEX(incollection),
	INDEX(ctitle),
	INDEX(cvenue),
	INDEX(cventype),
	INDEX(cyear),
	INDEX(updated)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE keymap (
	id SERIAL,
	ckey varchar(100) NOT NULL,
	cid BIGINT UNSIGNED NOT NULL,
	PRIMARY KEY(id),
	INDEX(ckey),
	INDEX(cid)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE citations (
	id BIGINT UNSIGNED NOT NULL,
	cluster BIGINT UNSIGNED NOT NULL,
	paperid VARCHAR(100) NOT NULL,
	PRIMARY KEY(id),
	INDEX(cluster),
	INDEX(paperid)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE papers (
	id VARCHAR(100) NOT NULL,
	cluster BIGINT UNSIGNED NOT NULL,
	PRIMARY KEY(id),
	INDEX(cluster)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE citegraph (
	id SERIAL,
	citing BIGINT UNSIGNED NOT NULL,
	cited BIGINT UNSIGNED NOT NULL,
	firstContext TEXT,
	selfCite TINYINT NOT NULL DEFAULT 0,
	PRIMARY KEY(id),
	INDEX(citing),
	INDEX(cited),
	INDEX(selfCite)
)
CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE=INNODB;


CREATE TABLE infupdates (
	id SERIAL,
	lastupdate TIMESTAMP NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(id) REFERENCES clusters(id)
	  ON DELETE CASCADE ON UPDATE CASCADE
)
ENGINE=INNODB;


CREATE TABLE deletions (
    id BIGINT UNSIGNED NOT NULL,
    deleted TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(id),
    INDEX(deleted)
)
ENGINE=INNODB;


CREATE TABLE indexTime (
    param VARCHAR(255) NOT NULL,
    lastupdate TIMESTAMP NOT NULL,
    PRIMARY KEY(param)
)
ENGINE=INNODB;


DELIMITER //

#
# Basic citation accounting triggers that link cluster size to the number
# of edges in the citegraph table.
#

CREATE TRIGGER count_inscite AFTER INSERT ON citegraph
    FOR EACH ROW BEGIN
        UPDATE clusters SET size=size+1 WHERE clusters.id=NEW.cited;
        UPDATE clusters SET selfCites=selfCites+NEW.selfCite WHERE clusters.id=NEW.cited;
        UPDATE clusters SET updated=current_timestamp where clusters.id=NEW.cited;
    END;//


CREATE TRIGGER count_updcite AFTER UPDATE ON citegraph
    FOR EACH ROW BEGIN
        IF NEW.cited != OLD.cited THEN
            UPDATE clusters SET size=size+1 WHERE clusters.id=NEW.cited;
            UPDATE clusters SET selfCites=selfCites+NEW.selfCite WHERE clusters.id=NEW.cited;
            UPDATE clusters SET updated=current_timestamp where clusters.id=NEW.cited;
            UPDATE clusters SET size=size-1 WHERE clusters.id=OLD.cited;
            UPDATE clusters SET selfCites=selfCites-OLD.selfCite where clusters.id=OLD.cited;
            UPDATE clusters SET updated=current_timestamp where clusters.id=OLD.cited;
        ELSE
            IF NEW.selfCite != OLD.selfCite THEN
                UPDATE clusters SET selfCites=selfCites+NEW.selfCite-OLD.selfCite where clusters.id=NEW.cited;
            END IF;
        END IF;
    END;//


CREATE TRIGGER count_delcite AFTER DELETE ON citegraph
    FOR EACH ROW BEGIN
        UPDATE clusters SET size=size-1 WHERE clusters.id=OLD.cited;
        UPDATE clusters SET selfCites=selfCites-OLD.selfCite where clusters.id=OLD.cited;
        UPDATE clusters SET updated=current_timestamp where clusters.id=OLD.cited;
    END;//


#
# Triggers that link cluster citation counts to the papers table in the main
# citeseerx database - this requires that the two databases be maintained within
# the same database server.
#

CREATE TRIGGER linkcites_ins AFTER INSERT ON clusters
    FOR EACH ROW BEGIN
        UPDATE citeseerx.papers SET ncites=NEW.size, selfCites=NEW.selfCites WHERE cluster=NEW.id;
    END;//


CREATE TRIGGER linkcites_upd AFTER UPDATE ON clusters
    FOR EACH ROW BEGIN
        IF NEW.size != OLD.size OR NEW.selfCites != OLD.selfCites THEN
            UPDATE citeseerx.papers SET ncites=NEW.size, selfCites=NEW.selfCites WHERE cluster=NEW.id;
        END IF;
    END;//

CREATE TRIGGER linkcites_del AFTER DELETE ON clusters
    FOR EACH ROW BEGIN
        UPDATE citeseerx.papers SET ncites=0, selfCites=0 WHERE cluster=OLD.id;
        INSERT INTO deletions VALUES (OLD.id, CURRENT_TIMESTAMP);
    END;//

#
# Triggers that link cluster IDs to the papers and citations tables
# in the main citeseerx database - this requires that the two databases
# be maintained within the same database server.
#

CREATE TRIGGER linkcluster_ins AFTER INSERT ON papers
    FOR EACH ROW BEGIN
        UPDATE citeseerx.papers SET cluster=NEW.cluster WHERE id=NEW.id;
        UPDATE citeseerx.papers, clusters SET citeseerx.papers.ncites=clusters.size, citeseerx.papers.selfCites=clusters.selfCites WHERE clusters.id=citeseerx.papers.cluster AND clusters.id=NEW.cluster;
    END;//
    
CREATE TRIGGER linkcluster_upd AFTER UPDATE ON papers
    FOR EACH ROW BEGIN
        UPDATE citeseerx.papers SET cluster=NEW.cluster WHERE id=NEW.id;
        UPDATE citeseerx.papers, clusters SET citeseerx.papers.ncites=clusters.size, citeseerx.papers.selfCites=clusters.selfCites WHERE clusters.id=citeseerx.papers.cluster AND clusters.id=NEW.cluster;
    END;//
    

CREATE TRIGGER linkcluster_del AFTER DELETE ON papers
    FOR EACH ROW BEGIN
        UPDATE citeseerx.papers SET cluster=NULL, ncites=0, selfCites=0 WHERE id=OLD.id;
    END;//


CREATE TRIGGER linkccluster_ins AFTER INSERT ON citations
    FOR EACH ROW BEGIN
        UPDATE citeseerx.citations SET cluster=NEW.cluster WHERE id=NEW.id;
    END;//
    
CREATE TRIGGER linkccluster_upd AFTER UPDATE ON citations
    FOR EACH ROW BEGIN
        UPDATE citeseerx.citations SET cluster=NEW.cluster WHERE id=NEW.id;
    END;//
    

CREATE TRIGGER linkccluster_del AFTER DELETE ON citations
    FOR EACH ROW BEGIN
        UPDATE citeseerx.citations SET cluster=NULL WHERE id=OLD.id;
    END;//

delimiter ;


GRANT ALL ON csx_citegraph.* TO '$USERNAME$'@'$DOMAIN$' IDENTIFIED BY '$PASSWORD$';
