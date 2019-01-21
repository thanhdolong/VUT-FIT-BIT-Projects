-- IDS projekt c. 4
-- Thanh Do Long
-- Juraj Kubis

--- drop tables and indexes ---
DROP TABLE uzivatel CASCADE CONSTRAINTS;
DROP TABLE produkt CASCADE CONSTRAINTS;
DROP TABLE baleniepastelek CASCADE CONSTRAINTS;
DROP TABLE skicar CASCADE CONSTRAINTS;
DROP TABLE recenzia CASCADE CONSTRAINTS;
DROP TABLE kosik_obsahuje CASCADE CONSTRAINTS;
DROP TABLE dodavatel CASCADE CONSTRAINTS;
DROP TABLE dodava CASCADE CONSTRAINTS;
DROP TABLE objednavka CASCADE CONSTRAINTS;
DROP TABLE objednavka_obsahuje CASCADE CONSTRAINTS;
DROP TABLE dodaci_adresa CASCADE CONSTRAINTS;

SET serveroutput on;
ALTER SESSION SET NLS_DATE_FORMAT = 'DD.MM.YYYY HH24:MI:SS';

--- create tables ---
CREATE TABLE uzivatel
  (
    login              VARCHAR2(30),
    heslo              VARCHAR2(30) NOT NULL,
    meno               VARCHAR2(30) NOT NULL,
    priezvisko         VARCHAR2(30) NOT NULL,
    email              VARCHAR2(30) NOT NULL,
    telefonne_cislo    VARCHAR2(13) NULL,
    ulica              VARCHAR2(30) NOT NULL,
    popisne_cislo      NUMBER(10,0) NOT NULL,
    psc                NUMBER(5,0)  NOT NULL,
    mesto              VARCHAR2(40) NOT NULL,
    datum_zmeny_kosika DATE         NULL,

    -- nastaveni key a omezeni --
    PRIMARY KEY       (login),
    UNIQUE            (telefonne_cislo, email),
    CHECK             (psc BETWEEN 00001 AND 99999),
    CHECK             (telefonne_cislo LIKE '+420%' AND LENGTH(telefonne_cislo) = 13),
    CHECK             (email LIKE '%@%.%')
  );

CREATE TABLE produkt
  (
    cislo_produktu INT          NOT NULL,
    nazov          VARCHAR2(50) NOT NULL,
    cena           INT          NOT NULL,
    pocet_ks       INT          NOT NULL,

    -- nastaveni key a omezeni --
    PRIMARY KEY       (cislo_produktu),
    CHECK             (cena >=0 AND pocet_ks >= 0)
  );

CREATE TABLE baleniepastelek
  (
    cislo_produktu INT          NOT NULL,
    pocet_v_baleni     VARCHAR2(30) NOT NULL,
    dlzka              VARCHAR2(30) NOT NULL,
    typ                VARCHAR2(30) NOT NULL,

    -- nastaveni key a omezeni --
    FOREIGN KEY (cislo_produktu)   REFERENCES produkt(cislo_produktu),
    UNIQUE      (cislo_produktu)
  );

CREATE TABLE skicar
  (
    cislo_produktu      INT          NOT NULL,
    gramaz         VARCHAR2(30) NOT NULL,
    format_skicaru VARCHAR2(30) NOT NULL,
    pocet_papierov VARCHAR2(30) NOT NULL,

    -- nastaveni key a omezeni --
    FOREIGN KEY (cislo_produktu)   REFERENCES produkt(cislo_produktu),
    UNIQUE      (cislo_produktu)
  );

CREATE TABLE recenzia
  (
    idrecenzie     INT GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
    datum          DATE           NOT NULL,
    text_recenzie  VARCHAR2(240)  NOT NULL,
    hodnotenie     NUMBER(1,0)    NOT NULL,
    login          VARCHAR2(30)   NOT NULL,
    cislo_produktu INT            NOT NULL,

    -- nastaveni key a omezeni --
    PRIMARY KEY (idrecenzie),
    CHECK       (hodnotenie >= 0 AND hodnotenie <= 5),
    FOREIGN KEY (login)            REFERENCES uzivatel(login),
    FOREIGN KEY (cislo_produktu)   REFERENCES produkt(cislo_produktu),
    UNIQUE      (login,cislo_produktu)
  );

CREATE TABLE kosik_obsahuje
  (
    login          VARCHAR2(30) NOT NULL,
    cislo_produktu INT          NOT NULL,
    pocet          INT          NOT NULL,

    -- nastaveni key a omezeni --
    FOREIGN KEY (login)            REFERENCES uzivatel(login),
    FOREIGN KEY (cislo_produktu)   REFERENCES produkt(cislo_produktu)
  );

CREATE TABLE dodavatel
  (
    ico               NUMBER(8,0)  NOT NULL,
    nazov_dodavatele  VARCHAR2(30) NOT NULL,
    telefonne_cislo   VARCHAR2(13) NOT NULL,
    ulica             VARCHAR2(30) NOT NULL,
    popisne_cislo     NUMBER(10,0) NOT NULL,
    psc               NUMBER(5,0)  NOT NULL,
    mesto             VARCHAR2(40) NOT NULL,

    -- nastaveni key a omezeni --
    PRIMARY KEY       (ico),
    UNIQUE            (telefonne_cislo, nazov_dodavatele),
    CHECK             (psc BETWEEN 00001 AND 99999),
    CHECK             (telefonne_cislo LIKE '+420%' AND LENGTH(telefonne_cislo) = 13)
  );

CREATE TABLE dodava
  (
    ico            NUMBER(8,0) NOT NULL,
    cislo_produktu INT         NOT NULL,

    -- nastaveni key a omezeni --
    FOREIGN KEY (ico)              REFERENCES dodavatel(ico),
    FOREIGN KEY (cislo_produktu)   REFERENCES produkt(cislo_produktu),
    UNIQUE      (ico,cislo_produktu)
  );

CREATE TABLE dodaci_adresa
  (
    id_adresy       INT GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
    ulica           VARCHAR2(30) NOT NULL,
    popisne_cislo   NUMBER(10,0) NOT NULL,
    psc             NUMBER(5,0)  NOT NULL,
    mesto           VARCHAR2(40) NOT NULL,

    -- nastaveni key a omezeni --
    PRIMARY KEY       (id_adresy),
    CHECK             (psc BETWEEN 00001 AND 99999)
  );

CREATE TABLE objednavka
  (
    cislo_objednavky      INT GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
    meno                  VARCHAR2(30) NOT NULL,
    priezvisko            VARCHAR2(30) NOT NULL,
    email                 VARCHAR2(30) NOT NULL,
    telefonne_cislo       VARCHAR2(13) NOT NULL,
    ulica                 VARCHAR2(30) NOT NULL,
    popisne_cislo         NUMBER(10,0) NOT NULL,
    psc                   NUMBER(5,0)  NOT NULL,
    mesto                 VARCHAR2(40) NOT NULL,
    zpusob_dopravy        VARCHAR2(30) NOT NULL,
    typ_platby            VARCHAR2(30) NOT NULL,
    datum_vytvorenia      DATE         NOT NULL,
    datum_spatnosti       DATE         NOT NULL,
    datum_prijatia_platby DATE         NOT NULL,
    id_adresy             INT          NULL,
    login                 VARCHAR2(30) NULL,

    -- nastaveni key a omezeni --
    PRIMARY KEY (cislo_objednavky),
    CHECK       (telefonne_cislo LIKE '+420%' AND LENGTH(telefonne_cislo) = 13),
    CHECK       (psc BETWEEN 00001 AND 99999),
    FOREIGN KEY (id_adresy)        REFERENCES dodaci_adresa(id_adresy),
    FOREIGN KEY (login)            REFERENCES uzivatel(login),
    CHECK(datum_vytvorenia >= datum_spatnosti AND datum_vytvorenia >= datum_prijatia_platby AND datum_vytvorenia <= datum_prijatia_platby)
  );

CREATE TABLE objednavka_obsahuje
  (
    cislo_objednavky  INT NOT NULL,
    cislo_produktu    INT NOT NULL,
    cena_za_kus       INT NOT NULL,
    pocet             INT NOT NULL,

    -- nastaveni key a omezeni --
    FOREIGN KEY (cislo_objednavky) REFERENCES objednavka(cislo_objednavky),
    FOREIGN KEY (cislo_produktu)   REFERENCES produkt(cislo_produktu),
    CHECK       (pocet >=0 AND cena_za_kus >= 0),
    UNIQUE      (cislo_objednavky, cislo_produktu)
  );

-- Triggers
DROP SEQUENCE produkt_sequence;
CREATE SEQUENCE produkt_sequence START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER INSERT_PRIMARY_KEY
BEFORE INSERT ON produkt
FOR EACH ROW
BEGIN
  SELECT produkt_sequence.nextval
  INTO :new.cislo_produktu
  FROM dual;
END;
/

--CREATE OR REPLACE TRIGGER PRIMARY_KEY_GENERATOR
--BEFORE INSERT ON Produkt
--FOR EACH ROW
--DECLARE
--  MAXIMUM INTEGER;
--BEGIN
--  SELECT MAX(cislo_produktu) INTO MAXIMUM FROM Produkt;
--  IF MAXIMUM IS NULL THEN
--    SELECT 0 INTO :NEW.CISLO_PRODUKTU FROM DUAL;
--  ELSE
--    SELECT MAXIMUM+1 INTO :NEW.CISLO_PRODUKTU FROM DUAL;
--  END IF;
--END;
--/

create or replace trigger icocheck
before insert or update of ico on dodavatel
for each row
declare
  checknumber  VARCHAR2(8);
  controlsum   NUMBER(5,0);
  helpvalue    INT;

  user_xcep EXCEPTION;
begin
  SELECT :NEW.ICO INTO checknumber FROM DUAL
  WHERE LENGTH(:NEW.ICO) = '8';
  SELECT 0 INTO controlsum FROM DUAL;
  SELECT 1 INTO helpvalue FROM DUAL;

  IF LENGTH(TRIM(TRANSLATE(:NEW.ICO, ' +-.0123456789',' '))) IS NOT NULL THEN
    raise user_xcep;
  END IF;

  WHILE helpvalue <= 7
  LOOP
   controlsum := controlsum + (SUBSTR(checknumber,helpvalue,1) * (9-helpvalue));
   helpvalue := helpvalue + 1;
  END LOOP;

  controlsum := MOD(controlsum,11);

  IF controlsum = 0 THEN
    controlsum := 1;
  ELSIF controlsum = 1 THEN
    controlsum := 0;
  ELSE
    controlsum := 11 - controlsum;
  END IF;

 IF SUBSTR(checknumber,8,1) <> controlsum THEN
    raise user_xcep;
  END IF;

  SELECT checknumber INTO :NEW.ICO FROM DUAL;
end;
/

-- vkladani do tabulky uzivate --

INSERT INTO uzivatel VALUES ('xdolon00', '123456', 'Thanh', 'Do Long', 'xdolon00@vutbr.cz', '+420774943678', 'namesti Augustina Nemejce', 2, 61200, 'Nepomuk', NULL);
INSERT INTO uzivatel VALUES('xjuraj01','654321','Juraj','Kubis','xkubis01@vutbr.cz','+420774943896','Kolej',3,33501,'Brno',SYSDATE);
INSERT INTO uzivatel VALUES('xlojda00','87930','Natrhla','Buben','xlojda01@vutbr.cz','+420774943636','Kolej',25,33501,'Brno',SYSDATE);

-- vkladani do tabulky produkt --

INSERT INTO produkt (nazov, cena, pocet_ks) VALUES('PASTELKY KOH-I-NOOR PROGRESSO', 42, 300);
INSERT INTO produkt (nazov, cena, pocet_ks) VALUES('PASTELKY KOH-I-NOOR MAGIC', 206, 121);
INSERT INTO produkt (nazov, cena, pocet_ks) VALUES('Hirsaluv skicak - Josef Hirsal',222,42);
INSERT INTO produkt (nazov, cena, pocet_ks) VALUES('PASTELKY RAINDBOW',12,291);
INSERT INTO produkt (nazov, cena, pocet_ks) VALUES('PASTELKY Koh-i-Noor 8758', 175, 311);
INSERT INTO produkt (nazov, cena, pocet_ks) VALUES('PASTELKY MAGIC 3408', 279, 145);
INSERT INTO produkt (nazov, cena, pocet_ks) VALUES('BOBO Skicak B5 Duhova kocka Tvorivost',88,63);
INSERT INTO produkt (nazov, cena, pocet_ks) VALUES('Skicak Retro A4 935870',99,271);

INSERT INTO baleniepastelek VALUES(1,'23','12','barevne');
INSERT INTO baleniepastelek VALUES(2,'22','24','cernobile');
INSERT INTO skicar          VALUES(3,'200','A4','60');
INSERT INTO baleniepastelek VALUES(4,'12','34','duhove');
INSERT INTO baleniepastelek VALUES(5,'11','12','barevne');
INSERT INTO baleniepastelek VALUES(6,'22','24','cernobile');
INSERT INTO skicar          VALUES(7,'200','B5','60');
INSERT INTO skicar          VALUES(8,'12','A4','40');

INSERT INTO recenzia (datum, text_recenzie, hodnotenie, login, cislo_produktu)
VALUES (SYSDATE, 'Za tuto cenu takovy pocet barev nema konkurenci. Porizeny pro deti a mohu jen doporucit. Spousta peknych dostatecne sytych barev, tuhy se nam nelamou a cena je velmi prijatelna.',4,'xdolon00', 1);
INSERT INTO recenzia (datum, text_recenzie, hodnotenie, login, cislo_produktu)
VALUES (SYSDATE, 'hodne odstinu, lehce kresli sytou stopu.',4,'xdolon00', 2);
INSERT INTO recenzia (datum, text_recenzie, hodnotenie, login, cislo_produktu)
VALUES (SYSDATE, 'Pastelky nejsou spatne, vzhledem k jejich porizovaci cene. Na obycejne malovani nebo pro deti do skoly jsou dostacujici',3,'xjuraj01', 2);
INSERT INTO recenzia (datum, text_recenzie, hodnotenie, login, cislo_produktu)
VALUES (SYSDATE, 'Hezky sesit',4,'xjuraj01', 3);
INSERT INTO recenzia (datum, text_recenzie, hodnotenie, login, cislo_produktu)
VALUES (SYSDATE, 'nadherne pastelky, kvalitni',5,'xjuraj01', 1);
INSERT INTO recenzia (datum, text_recenzie, hodnotenie, login, cislo_produktu)
VALUES (SYSDATE, 'Nezkousela jsem jinou znacku, takze nemam srovnani, ale s temito pastelkami jsem, zatim, spokojena.',2,'xlojda00', 1);

INSERT INTO dodavatel VALUES(04528760,'Do Long Thanh s.r.o','+420774943691', 'Na koleji', 2, 33501, 'Brno');
INSERT INTO dodavatel VALUES(04528761,'IDS s.r.o','+420771373651', 'Na koleji', 4, 33501, 'Brno');
INSERT INTO dodavatel VALUES(07628762,'Kubica s.r.o','+420384943691', 'Nekdezamostem', 22, 89501, 'Olomouc');
INSERT INTO dodavatel VALUES(09028450,'Burger jako krava s.r.o','+420444943678', 'Na Vavrinech', 2, 29032, 'Praha');
INSERT INTO dodavatel VALUES(07654760,'VUT FIT s.r.o','+420734562312', 'Bozetechova', 2, 33501, 'Brno');
INSERT INTO dodavatel VALUES(05834760,'Apple pen s.r.o','+420774943678', 'Vodnany', 2, 90801, 'Plzen');

INSERT INTO dodava    VALUES (04528760, 1);
INSERT INTO dodava    VALUES (04528760, 2);
INSERT INTO dodava    VALUES (04528760, 3);
INSERT INTO dodava    VALUES (04528761, 4);
INSERT INTO dodava    VALUES (04528761, 5);
INSERT INTO dodava    VALUES (04528761, 7);
INSERT INTO dodava    VALUES (07628762, 3);
INSERT INTO dodava    VALUES (07628762, 5);
INSERT INTO dodava    VALUES (07628762, 4);
INSERT INTO dodava    VALUES (07654760, 6);
INSERT INTO dodava    VALUES (04528760, 4);
INSERT INTO dodava    VALUES (07628762, 7);
INSERT INTO dodava    VALUES (07654760, 7);
INSERT INTO dodava    VALUES (07654760, 4);
INSERT INTO dodava    VALUES (07654760, 5);
INSERT INTO dodava    VALUES (07654760, 1);

INSERT INTO objednavka (meno, priezvisko, email, telefonne_cislo, ulica, popisne_cislo, psc, mesto, zpusob_dopravy, typ_platby, datum_vytvorenia, datum_spatnosti, datum_prijatia_platby)
VALUES('Honza', 'Kubis', 'xkubis@vutbr.cz', '+420774890678', 'Kolejni', 0, 33501, 'Brno', 'letecka spolecnost', 'karta', SYSDATE,SYSDATE,SYSDATE);
INSERT INTO objednavka (meno, priezvisko, email, telefonne_cislo, ulica, popisne_cislo, psc, mesto, zpusob_dopravy, typ_platby, datum_vytvorenia, datum_spatnosti, datum_prijatia_platby)
VALUES('Juraj', 'Kubica', 'xkubic01@vutbr.cz', '+420774890679', 'Kolejni', 1, 33501, 'Brno', 'letecka spolecnost', 'karta', SYSDATE,SYSDATE,SYSDATE);
INSERT INTO objednavka (meno, priezvisko, email, telefonne_cislo, ulica, popisne_cislo, psc, mesto, zpusob_dopravy, typ_platby, datum_vytvorenia, datum_spatnosti, datum_prijatia_platby)
VALUES('David', 'Nevi', 'nevi@vutbr.cz', '+420774890678', 'Kolejni', 0, 33501, 'Praha', 'letecka spolecnost', 'karta', SYSDATE,SYSDATE,SYSDATE);
INSERT INTO objednavka (meno, priezvisko, email, telefonne_cislo, ulica, popisne_cislo, psc, mesto, zpusob_dopravy, typ_platby, datum_vytvorenia, datum_spatnosti, datum_prijatia_platby)
VALUES('Lenka', 'Vtirava', 'vtirava@vutbr.cz', '+420774890678', 'Kolejni', 0, 33501, 'Plzen', 'letecka spolecnost', 'karta', SYSDATE,SYSDATE,SYSDATE);
INSERT INTO objednavka (meno, priezvisko, email, telefonne_cislo, ulica, popisne_cislo, psc, mesto, zpusob_dopravy, typ_platby, datum_vytvorenia, datum_spatnosti, datum_prijatia_platby)
VALUES('Radek', 'Novak', 'nova@vutbr.cz', '+420774890678', 'Kolejni', 0, 33501, 'Praha', 'letecka spolecnost', 'karta', SYSDATE,SYSDATE,SYSDATE);
INSERT INTO objednavka (meno, priezvisko, email, telefonne_cislo, ulica, popisne_cislo, psc, mesto, zpusob_dopravy, typ_platby, datum_vytvorenia, datum_spatnosti, datum_prijatia_platby)
VALUES('Aneta', 'Beranova', 'beranova@vutbr.cz', '+420774890678', 'Kolejni', 0, 33501, 'Brno', 'letecka spolecnost', 'karta', SYSDATE,SYSDATE,SYSDATE);
INSERT INTO objednavka (meno, priezvisko, email, telefonne_cislo, ulica, popisne_cislo, psc, mesto, zpusob_dopravy, typ_platby, datum_vytvorenia, datum_spatnosti, datum_prijatia_platby)
VALUES('Marek', 'Schauer', 'xkubis@vutbr.cz', '+420774890678', 'Kolejni', 0, 33501, 'Brno', 'letecka spolecnost', 'karta', SYSDATE,SYSDATE,SYSDATE);
INSERT INTO objednavka (meno, priezvisko, email, telefonne_cislo, ulica, popisne_cislo, psc, mesto, zpusob_dopravy, typ_platby, datum_vytvorenia, datum_spatnosti, datum_prijatia_platby)
VALUES('David', 'Mertha', 'xkubis@vutbr.cz', '+420774890678', 'Kolejni', 0, 33501, 'Plzen', 'letecka spolecnost', 'karta', SYSDATE,SYSDATE,SYSDATE);
INSERT INTO objednavka (meno, priezvisko, email, telefonne_cislo, ulica, popisne_cislo, psc, mesto, zpusob_dopravy, typ_platby, datum_vytvorenia, datum_spatnosti, datum_prijatia_platby)
VALUES('Jan', 'Morisson', 'xkubis@vutbr.cz', '+420774890678', 'Kolejni', 0, 33501, 'Brno', 'letecka spolecnost', 'karta', SYSDATE,SYSDATE,SYSDATE);
INSERT INTO objednavka (meno, priezvisko, email, telefonne_cislo, ulica, popisne_cislo, psc, mesto, zpusob_dopravy, typ_platby, datum_vytvorenia, datum_spatnosti, datum_prijatia_platby)
VALUES('Monika', 'Sobotova', 'xkubis@vutbr.cz', '+420774890678', 'Kolejni', 0, 33501, 'Plzen', 'letecka spolecnost', 'karta', SYSDATE,SYSDATE,SYSDATE);
INSERT INTO objednavka (meno, priezvisko, email, telefonne_cislo, ulica, popisne_cislo, psc, mesto, zpusob_dopravy, typ_platby, datum_vytvorenia, datum_spatnosti, datum_prijatia_platby)
VALUES('Ondrej', 'Dubaj', 'xkubis@vutbr.cz', '+420774890678', 'Kolejni', 0, 33501, 'Brno', 'letecka spolecnost', 'karta', SYSDATE,SYSDATE,SYSDATE);
INSERT INTO objednavka (meno, priezvisko, email, telefonne_cislo, ulica, popisne_cislo, psc, mesto, zpusob_dopravy, typ_platby, datum_vytvorenia, datum_spatnosti, datum_prijatia_platby)
VALUES('Tomas', 'Doslik', 'xkubis@vutbr.cz', '+420774890678', 'Kolejni', 0, 33501, 'Praha', 'letecka spolecnost', 'karta', SYSDATE,SYSDATE,SYSDATE);
INSERT INTO objednavka (meno, priezvisko, email, telefonne_cislo, ulica, popisne_cislo, psc, mesto, zpusob_dopravy, typ_platby, datum_vytvorenia, datum_spatnosti, datum_prijatia_platby)
VALUES('Patrik', 'Spacek', 'xkubis@vutbr.cz', '+420774890678', 'Kolejni', 0, 33501, 'Brno', 'letecka spolecnost', 'karta', SYSDATE,SYSDATE,SYSDATE);
INSERT INTO objednavka (meno, priezvisko, email, telefonne_cislo, ulica, popisne_cislo, psc, mesto, zpusob_dopravy, typ_platby, datum_vytvorenia, datum_spatnosti, datum_prijatia_platby)
VALUES('David', 'Czernin', 'xkubis@vutbr.cz', '+420774890678', 'Kolejni', 0, 33501, 'Brno', 'letecka spolecnost', 'karta', SYSDATE,SYSDATE,SYSDATE);
INSERT INTO objednavka (meno, priezvisko, email, telefonne_cislo, ulica, popisne_cislo, psc, mesto, zpusob_dopravy, typ_platby, datum_vytvorenia, datum_spatnosti, datum_prijatia_platby)
VALUES('Vladka', 'Vajdova', 'xkubis@vutbr.cz', '+420774890678', 'Kolejni', 0, 33501, 'Brno', 'letecka spolecnost', 'karta', SYSDATE,SYSDATE,SYSDATE);

INSERT INTO objednavka_obsahuje VALUES(15, 8, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(14, 8, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(13, 8, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(1, 7, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(2, 7, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(3, 7, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(4, 7, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(5, 6, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(6, 5, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(7, 5, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(15, 1, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(15, 2, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(15, 3, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(15, 4, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(7, 7, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(8, 6, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(9, 5, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(10, 5, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(11, 3, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(12, 7, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(12, 2, 0, 2);
INSERT INTO objednavka_obsahuje VALUES(10, 1, 0, 2);

-- Procedures
DECLARE
  PROCEDURE vypis_dodavatelov_produktu(id_produktu IN number) IS
    c_ico dodavatel.ico%type;
    c_nazov_dodavatele dodavatel.nazov_dodavatele%type;
    invalid_product_id EXCEPTION;
    i number;
    CURSOR c_dodavatel is
      SELECT ico, nazov_dodavatele
      FROM dodava NATURAL JOIN dodavatel
      WHERE cislo_produktu = id_produktu;
  BEGIN
    SELECT COUNT(cislo_produktu) INTO i FROM produkt WHERE cislo_produktu = id_produktu;

    IF (i = 0) THEN
    RAISE invalid_product_id;
    END IF;

    OPEN c_dodavatel;

    LOOP
      FETCH c_dodavatel INTO c_ico, c_nazov_dodavatele;
      EXIT WHEN c_dodavatel%notfound;
      dbms_output.put_line(c_ico || ' ' || c_nazov_dodavatele);
    END LOOP;

    CLOSE c_dodavatel;
  EXCEPTION
    WHEN invalid_product_id THEN
      dbms_output.put_line('Produkt s danym cislom neexistuje!');
    WHEN others THEN
      dbms_output.put_line('Neznama chyba!');
  END;

  PROCEDURE black_friday(discount IN number) IS
    total_rows number(2);
    invalid_total_rows EXCEPTION;
    invalid_input EXCEPTION;
  BEGIN
    UPDATE produkt
    SET cena = cena - ((cena/100)*discount);

    IF sql%notfound THEN
      dbms_output.put_line('Neexistuje zadny produkt pro nastaveni akce BLACK FRIDAY!');
    ELSIF sql%found THEN
      total_rows := sql%rowcount;
      dbms_output.put_line( total_rows || ' produktu bylo zmeneno');
    END IF;

    EXCEPTION
      WHEN others THEN
        dbms_output.put_line('Neznama chyba!');
  END;

BEGIN
  vypis_dodavatelov_produktu(1);
  black_friday(15);
END;
/

-- Indexs and Explain plan
DROP INDEX cena_idx;

EXPLAIN PLAN FOR
  SELECT nazov, cena FROM produkt ORDER BY cena DESC;
select * from table(dbms_xplan.display);

CREATE INDEX cena_idx ON produkt(cena);

EXPLAIN PLAN FOR
  SELECT nazov, cena FROM produkt ORDER BY cena DESC;
select * from table(dbms_xplan.display);

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

-- Access rights

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

