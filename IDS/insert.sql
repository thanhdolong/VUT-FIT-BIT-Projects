DROP INDEX mesto_idx;
DROP INDEX pocet_idx;

EXPLAIN PLAN FOR
  SELECT mesto, COUNT(pocet) AS pocet 
  FROM objednavka NATURAL JOIN objednavka_obsahuje NATURAL JOIN produkt
  GROUP BY mesto
  ORDER BY pocet DESC;
SELECT * FROM TABLE(dbms_xplan.display);
  
CREATE INDEX mesto_idx ON objednavka(cislo_objednavky, mesto);
CREATE INDEX pocet_idx ON objednavka_obsahuje(cislo_objednavky, pocet);

EXPLAIN PLAN FOR
  SELECT mesto, COUNT(pocet) AS pocet 
  FROM objednavka NATURAL JOIN objednavka_obsahuje NATURAL JOIN produkt
  GROUP BY mesto
  ORDER BY pocet DESC;
SELECT * FROM TABLE(dbms_xplan.display);
