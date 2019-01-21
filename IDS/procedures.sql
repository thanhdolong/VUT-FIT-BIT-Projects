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

BEGIN
vypis_dodavatelov_produktu(1);
END;
/


DECLARE  
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
black_friday(15);
END;
/   