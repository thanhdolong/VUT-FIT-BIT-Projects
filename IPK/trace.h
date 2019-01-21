//
// Created by Thành Đỗ Long on 13/04/2017.
//

/***** C knihovny *****/
#include <stdio.h>
#include <stdlib.h>
#include <netinet/icmp6.h>
#include <string.h>
#include <linux/icmp.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <linux/errqueue.h>
#include <sys/select.h>
#include <sys/time.h>

#include <sys/types.h>

/***** C++ knihovny ******/
#include <string>
#include <cstdlib>

#define BUFLEN 1024
#define ICMP6_PARAM_PROB 4

typedef struct {
    int firstttl = 1;
    int maxttl = 30;
    std::string ipadress;
} tOptions;

typedef struct{
    struct sockaddr_in  sa_ipv4;
    struct sockaddr_in6 sa_ipv6;
    bool ipv6 = false;
} u;

int processArg(int argc, char* argv[], tOptions*);
int debug(tOptions*, u*);
void makesocaddr(tOptions *, u*);
bool checkdigit(std::string s);
int traceipv6(u*, int fd, int ttl);
int traceipv4(u*,int fd, int ttl);
void createreply4(int ttl, sockaddr_in*);
void createreply6(int ttl, sockaddr_in6*);
void callerror(std::string error);