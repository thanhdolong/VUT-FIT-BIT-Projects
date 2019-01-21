-- spojeni dvou tabulek
-- Dotaz, ktery spojuje tabulky "produkt" a "baleni pastelek".
-- krome nazvu pastelky a ceny ziskavame informace o pocet ks, pocet v baleni, dlouzky a typu pastelky
SELECT *
FROM produkt
NATURAL JOIN baleniepastelek;

-- spojeni dvou tabulek
-- Dotaz, ktery spojuje tabulky "produkt" a "baleni pastelek".
-- krome nazvu pastelky a ceny ziskavame informace o pocet ks, gramaz, format skicaru a pocet papiru ve skicaru
SELECT *
FROM produkt
JOIN skicar USING (cislo_produktu);

-- spojeni tri tabulek
-- spojeni tabulky "dodavatel", "dodava" a "produkt"
-- pomoci tohoto dotazu ziskavame tabulku, ktera nam ukazuje dodavatele s dodavanymi zbozi
SELECT ico AS "ico", nazov_dodavatele AS "nazov dodatavetele", cislo_produktu AS "kod produktu", nazov AS "nazov produktu"
FROM dodavatel NATURAL JOIN dodava NATURAL JOIN  produkt
ORDER BY ico, cislo_produktu;

-- dotaz s klauzulí GROUP BY
-- pomoci tohoto dotazu ziskavame tabulku, ktera nam ukazuje dodavatele s poctem dodavanymi zbozi
SELECT ico, nazov_dodavatele AS "nazov dodavatele", count(ico) AS "pocet odebiranych produktu"
FROM dodavatel NATURAL JOIN dodava NATURAL JOIN  produkt
GROUP BY ico, nazov_dodavatele
ORDER BY count(ico) DESC;

-- dotaz s klauzulí GROUP BY
-- pomoci tohoto dotazu ziskavame tabulku, ktera nam ukazuje uzivatele a jejich pocet ohodnocenych produktu
SELECT login, count(login) AS "pocet napsanych recenzi"
FROM recenzia NATURAL LEFT JOIN uzivatel
GROUP BY login
ORDER BY count(login) DESC;

-- dotaz s predikátem IN  s vnořeným selectem
-- tabulka obsahuje rezence od uzivatele xjuraj01
SELECT login, text_recenzie, nazov
FROM uzivatel NATURAL JOIN recenzia NATURAL JOIN  produkt
WHERE login IN (
  SELECT login FROM  uzivatel WHERE login = 'xjuraj01'
);

-- dotaz obsahující predikát EXISTS
-- tabulka obsahuje osobni informace vsech uzivatelu, kteri napsali recenzi
SELECT login, meno, priezvisko, email, telefonne_cislo
FROM uzivatel
WHERE EXISTS(
  SELECT * FROM recenzia
  WHERE recenzia.login = uzivatel.login
);