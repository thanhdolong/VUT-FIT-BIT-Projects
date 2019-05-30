# ISA Project - Network service application

Variant: LDAP server

Author: Do Long Thanh <xdolon00@stud.fit.vutbr.cz>

Usage: myldap {-p <port>} -f <soubor>
    
Example: ./myldap -p 3042 -f ldap.csv

[-p] - optional attribute - port number of the host to connect (default value is 389)

[-f] - required attribute - file path to database

List of files in archive:

    README
    manual.pdf  
    Makefile 
    main.cpp
    tools.cpp
    tools.h
    socket.cpp
    socket.h
    ldap.cpp
    ldap.h
    db.csv
    queries

List of folder in archive:
    tests

Limitations:
    support alphanumeric characters
    not support SASL authentication

Tested on CentOS/Linux (merlin.fit.vutbr.cz)
