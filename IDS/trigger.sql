--- create triggers ---
CREATE OR REPLACE TRIGGER INSERT_PRIMARY_KEY
BEFORE INSERT ON produkt
FOR EACH ROW
BEGIN   
    SELECT produkt_sequence.nextval
    into :new.cislo_produktu 
    from dual;
END;
/

CREATE OR REPLACE TRIGGER PRIMARY_KEY_GENERATOR 
BEFORE INSERT ON Produkt 
FOR EACH ROW
DECLARE
  MAXIMUM INTEGER;
BEGIN
  SELECT MAX(cislo_produktu) INTO MAXIMUM FROM Produkt;
  IF MAXIMUM IS NULL THEN
    SELECT 0 INTO :NEW.CISLO_PRODUKTU FROM DUAL;
  ELSE
    SELECT MAXIMUM+1 INTO :NEW.CISLO_PRODUKTU FROM DUAL;
  END IF;
END;
/

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
