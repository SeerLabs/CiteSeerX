#
#  DOI server database spec
#
#  IGC
#

CREATE DATABASE csxdoi;

USE csxdoi;


CREATE TABLE doi_granted (
	id SERIAL,
	doi_type INT NOT NULL,
	bin INT UNSIGNED NOT NULL,
	rec INT UNSIGNED NOT NULL,
	assigned TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(id),
	INDEX(doi_type),
	INDEX(bin),
	INDEX(rec),
	INDEX(assigned)
);


CREATE TABLE configuration (
	deployment VARCHAR(15),
	site_id INT UNSIGNED NOT NULL,
	deployment_id INT UNSIGNED NOT NULL
);


GRANT ALL ON csxdoi.doi_granted TO '$USERNAME$'@'$DOMAIN$' IDENTIFIED BY '$PASSWORD$';

GRANT SELECT ON csxdoi.configuration TO '$USERNAME$'@'$DOMAIN$' IDENTIFIED BY '$PASSWORD$';

INSERT INTO configuration VALUES ("DEPLOYMENT", $SITEID$, $DEPID$);
