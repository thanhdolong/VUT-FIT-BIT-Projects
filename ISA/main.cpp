// ISA Project
// Variant: LDAP server
//
// Author: Do Long Thanh
// <xdolon00@stud.fit.vutbr.cz>

#include "socket.hpp"
#include "ldap.hpp"

#include <unistd.h>
#include <signal.h>
#include <netdb.h>

/**
 * Callback handling SIGINT signal.
 */
void handleSignal(int) {
    exit(EXIT_SUCCESS);
}

/**
 * The main function.
 * Control TCP connection with a client.
 * @param argc The number of parameters
 * @param argv Array containing the parameters
 */
int main(int argc, char * argv[]) {
    int connectfd;
    tOptions variable;
    pid_t pid;

    // Set signal handler
    signal(SIGINT, handleSignal);

    processArg(argc, argv, &variable);
    loadFile(&variable);

    int socket_desc = creatingsocket(&variable);

    /**
     * Infinite cycle of listening, parsing and responding
     * Some parts of code are adapted from ISA source code examples (URL below)
     * https://wis.fit.vutbr.cz/FIT/st/course-files-st.php?file=%2Fcourse%2FISA-IT%2Fexamples&cid=12191
     */
    while ( true ) {
        /**
         * SERVER IS ACTIVE AND IS LISTENING TO INCOMMING MESSAGES
         * Socket is set as non-blocking
         */
        if((connectfd = accept(socket_desc, NULL, NULL)) == -1) {
            /**
             *  If no data can be read or written, they return -1 and set errno to EAGAIN (or EWOULDBLOCK).
             */
            if ((errno != EAGAIN) || (errno != EWOULDBLOCK)) callError("Server cannot listen messages");
        } else {
            if ((pid = fork()) > 0) {
                close(connectfd);
            } else if (pid == 0) {
                close(socket_desc);
                service(connectfd);
                close(connectfd);
                exit(EXIT_SUCCESS);
            } else callError("fork() failed");
        }
    }

    close(socket_desc);
    return 0;
}