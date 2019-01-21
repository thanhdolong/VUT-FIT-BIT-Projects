CC=g++
CFLAGS=-std=c++11 -Wall -Wextra -pedantic
SRC_LDAP=main.cpp ldap.cpp socket.cpp tools.cpp

all:
	$(CC) -o myldap $(SRC_LDAP) $(CFLAGS)

clean:
	rm -f server

zip:
	tar -cvf xdolon00.tar Makefile README manual.pdf main.cpp ldap.cpp ldap.hpp socket.cpp socket.hpp tools.cpp tools.hpp tests