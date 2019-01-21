--Editacia produktov
GRANT ALL ON skicar TO xdolon00;
GRANT ALL ON baleniepastelek TO xdolon00;
GRANT ALL ON produkt TO xdolon00;

--Zobrazenie recenzie
GRANT SELECT ON recenzia TO xdolon00;

--Editacia dodavatelov
GRANT ALL ON dodava TO xdolon00;
GRANT ALL ON dodavatel TO xdolon00;

--Zobrazenie objednavok
GRANT SELECT, INSERT ON objednavka_obsahuje TO xdolon00;
GRANT SELECT, INSERT ON objednavka TO xdolon00;
GRANT SELECT, INSERT ON dodaci_adresa TO xdolon00;

-- uzivatel
DROP VIEW uzivatel_view;
CREATE MATERIALIZED VIEW uzivatel_view AS
  SELECT login, meno, priezvisko, email, telefonne_cislo, ulica, popisne_cislo, psc
  FROM uzivatel;

GRANT SELECT ON uzivatel_view TO xdolon00;
