USE citeseerx;

ALTER TABLE citecharts
  ADD COLUMN citechartData TEXT AFTER lastNcites
;
