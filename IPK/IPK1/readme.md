Popis:
Vytvořte program trace, který umožní zobrazit průchod paketu sítí od zdroje k cíli. K objevení jednotlivých směrovačů, přes které paket prochází, využijte pole TTL/Hop limit protokolu IPv4/IPv6 a zprávy ICMP TIME_EXCEEDED. Program musí zobrazit IPv4/IPv6 adresu směrovače a dobu odezvy. Pro zaslání testovací zprávy s různě nastaveným TTL/HL využijte protokol UDP.

Krom soketů nejsou povoleny jiné síťové knihovny. Lze použít hlavičkové soubory netinet/*, stejně jako klasické hlavičkové soubory stdio.h atp. Program musí být schopen reagovat na ICMP zprávy host, network, protocol unreachable a communication administratively prohibited. Jako základní timeout použijte 2 sekundy, maximální počet skoků je 30.

Jakákoliv programová shoda s volně dostupnými nástroji pro traceroute sníží hodnocení k 0.

Spuštění aplikace:

Program trace lze spustit s následujícími parametry:

trace [-f first_ttl] [-m max_ttl] <ip-address>
-f first_ttl Specifikuje, jaké TTL se použije pro první paket. Implicitně 1.
-m max_ttl Specifikuje maximální TTL. Implicitně 30.
<ip-address> - IPv4/IPv6 adresa cíle
Příklad výstupu:
~> ./trace 2a00:1450:400d:802::1000 
 1   2001:67c:1220:80c::1   2.993 ms 
 2   2001:67c:1220:f521::aff:4901   1.060 ms 
 3   2001:67c:1220:f712::aff:4801   1.029 ms 
 4   2001:67c:1220:f712::aff:4801   1.643 ms 
 5   2001:718:0:c003::1   8.440 ms 
 6   2001:718:0:600:0:119:148:11   11.036 ms 
 7   2001:7f8:3c::29   5.836 ms 
 8   2001:7f8:3c::29   5.848 ms 
 9   2001:7f8:3c::35   12.518 ms 
10   2001:798:14:10aa::e   12.235 ms 
11   2001:4860::1:0:d0c5   13.783 ms 
12   2001:4860::1:0:446f   30.859 ms 
13   2001:4860::1:0:446f   30.841 ms 
14   2a00:1450:8000:12::6   21.444 ms
~> ./trace 172.217.23.238 
 1   147.229.12.1   7.583 ms 
 2   147.229.254.217   1.027 ms 
 3   147.229.252.90   1.056 ms 
 4   147.229.252.82   1.166 ms 
 5   147.229.253.180   2.922 ms 
 6   147.229.253.180   2.890 ms 
 7   195.113.235.109   9.652 ms 
 8   195.113.157.70   5.842 ms 
 9   108.170.238.155   5.852 ms 
10   108.170.238.155   5.886 ms 
11   172.217.23.238   5.805 ms

Timeout je místo času označen *, ICMPv4/v6 zprávy host, network, protocol unreachale, communication administratively prohibited jsou označny místo času H!, N!, P!, X!.

Testování:

Implementace bude testována na standardní instalaci distribuce CentOS7. Můžete použít image pro CentOS dostupný zde (Přihlašovací údaje: user/user4lab, root/root4lab).