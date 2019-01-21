DROP INDEX cena_idx;

EXPLAIN PLAN FOR
  SELECT nazov, cena FROM produkt ORDER BY cena DESC;
select * from table(dbms_xplan.display);
  
CREATE INDEX cena_idx ON produkt(cena);

EXPLAIN PLAN FOR
  SELECT nazov, cena FROM produkt ORDER BY cena DESC;
select * from table(dbms_xplan.display);

---------------------------------------------------------------
DROP INDEX mesto_idx;

EXPLAIN PLAN FOR
  SELECT mesto, COUNT(pocet) AS pocet 
  FROM objednavka NATURAL JOIN objednavka_obsahuje
  GROUP BY mesto
  ORDER BY pocet DESC;
SELECT * FROM TABLE(dbms_xplan.display);
  
CREATE INDEX mesto_idx ON objednavka(cislo_objednavky, mesto);

EXPLAIN PLAN FOR
  SELECT mesto, COUNT(pocet) AS pocet 
  FROM objednavka NATURAL JOIN objednavka_obsahuje
  GROUP BY mesto
  ORDER BY pocet DESC;
SELECT * FROM TABLE(dbms_xplan.display);