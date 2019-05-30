IIS - Internetový obchod
=============

 Cílem je vytvoření jednoduché aplikace pro internetový obchod s pastelkami a skicáky. Návštěvníci si mohou pomocí internetového rozhraní prohlížet veškerý sortiment obchodu.
    
Pastelky mohou lišit podle typu (obyčejné, progresso, voskovky, ...) a délky, počtu pastelek v balení, atd. Skicáky se dělí podle gramáže, velikosti, počtu papírů, apod.

Pokud má návštěvník zájem o určitý produkt/y, může si jej vybrat (vložením do nákupního košíku). U registrovaných zákazníků, kteří jsou do systému přihlášeni, zůstává informace o vybraném zboží v košíku uložena a při opětovném přihlášení znovu načtena. 

Zákazník si může zboží objednat po zadání potřebných údajů (kontakt, doprava, ...). Zákazníci mohou jednotlivé zboží hodnotit a psát na něj recenze.

V systému jsou uloženy také základní údaje o dodavatelích pro opětovné přiobjednání dalšího zboží. Zaměstnanci mohou nahlédnout do statistik oblíbenosti a prodejnosti zboží.

Web Server Setup
----------------

The simplest way to get started is to start the built-in PHP server in the root directory of your project:

	php -S localhost:8000 -t www

Then visit `http://localhost:8000` in your browser to see the welcome page.

For Apache or Nginx, setup a virtual host to point to the `www/` directory of the project and you
should be ready to go.

It is CRITICAL that whole `app/`, `log/` and `temp/` directories are not accessible directly
via a web browser. See [security warning](https://nette.org/security-warning).


Requirements
------------

PHP 5.6 or higher. To check whether server configuration meets the minimum requirements for
Nette Framework browse to the directory `/checker` in your project root (i.e. `http://localhost:8000/checker`).
