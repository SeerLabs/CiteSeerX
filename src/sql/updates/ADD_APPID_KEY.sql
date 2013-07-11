USE myciteseerx;

ALTER TABLE users
  ADD COLUMN appid VARCHAR(255) DEFAULT NULL AFTER updated,
  ADD INDEX(appid)
;
