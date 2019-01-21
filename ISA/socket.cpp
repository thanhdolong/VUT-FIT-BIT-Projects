// ISA Project
// Variant: LDAP server
//
// Author: Do Long Thanh
// <xdolon00@stud.fit.vutbr.cz>

#include <sys/socket.h>
#include <netinet/in.h>
#include <iostream>
#include <signal.h>
#include <cstring>
#include <fcntl.h>

#include "socket.hpp"

/**
 * Creating socket for reading and reply incoming messages from LDAP client.
 * @return socket for connection
 */
int creatingsocket(tOptions* ptr) {
    int socket_desc;
    struct sockaddr_in server;

    /**
     * Automatically destroy child processes
     * Prevent the creation of zombies
     */
    struct sigaction sigchldAction;
    sigchldAction.sa_handler = SIG_IGN;
    sigchldAction.sa_flags = 0;
    sigemptyset(&sigchldAction.sa_mask);
    if (sigaction(SIGCHLD, &sigchldAction, NULL) == -1) callError("sigaction()");

    /**
     * Creating socket for connection
     */
    if ((socket_desc = socket(AF_INET, SOCK_STREAM, 0)) == -1) callError("Cannot create socket");

    /**
     * Initiation for bind the socket
     * Configure settings of the source address struct
     */
    memset(&server,0,sizeof(server));

    server.sin_family = AF_INET;
    server.sin_addr.s_addr = htonl(INADDR_ANY);
    server.sin_port = htons(ptr->port);

    if(bind(socket_desc, (struct sockaddr *)&server, sizeof(server)) == -1) callError("Failed to bind socket");

    /**
     * Force the network socket into nonblocking mode
     */
    setNonblocking(socket_desc);

    /**
     * Passive server-side waiting
     * The server waits for a connection
	 */
    if ((listen(socket_desc, MAXCONNECTIONS)) == -1) callError("listen");

    return socket_desc;
}

/**
 * set socket to non-blocking
 * @param fd
 * @return
 */
int setNonblocking(int fd)
{
    int flags;
    if ((flags = fcntl(fd, F_GETFL, 0)) == -1) callError("Cannot set non-blocking socket");
    return fcntl(fd, F_SETFL, flags | O_NONBLOCK);
}