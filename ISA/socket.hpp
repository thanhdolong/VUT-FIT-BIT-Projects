// ISA Project
// Variant: LDAP server
//
// Author: Do Long Thanh
// <xdolon00@stud.fit.vutbr.cz>

#ifndef SERVER_SOCKET_H
#define SERVER_SOCKET_H

#define MAXCONNECTIONS	(2)

#include "tools.hpp"

int creatingsocket(tOptions*);
int setNonblocking(int fd);

#endif //SERVER_SOCKET_H
