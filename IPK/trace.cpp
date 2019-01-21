#include "trace.h"
// 16/20

int main (int argc, char * argv[]) {
    //socklen_t serverlen;
    tOptions trace;
    u sockaddr;
    int fd;

    ///////////// step 1 - initializing ////////////////
    processArg(argc, argv, &trace);
    makesocaddr(&trace, &sockaddr);

    // debug(&trace, &sockaddr);

    ///////////// step 2 - Create sockets for the connections ////////////////

    if(sockaddr.ipv6){
        if ((fd = socket(AF_INET6, SOCK_DGRAM, IPPROTO_UDP)) < 0) callerror("cannot create socket");

        int on = 1;
        if (setsockopt(fd, SOL_IPV6, IPV6_RECVERR, (const char *)&on, sizeof(on))) { //Receve error in this socket
            callerror("ERROR SETSOCKOPT");
        }

    } else {
        if ((fd = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) < 0) callerror("cannot create socket");

        int on = 1;
        if (setsockopt(fd, SOL_IP, IP_RECVERR, (const char *)&on, sizeof(on))) { //Receve error in this socket
            callerror("ERROR SETSOCKOPT");
        }
    }

    ///////////// step 3 - Set the TTL field on the packets. ////////////////

    for (int ttl = trace.firstttl; ttl <= trace.maxttl; ++ttl) {

        if (sockaddr.ipv6) {
            if (setsockopt(fd, IPPROTO_IPV6, IPV6_UNICAST_HOPS, &ttl, sizeof(ttl)) < 0)
                callerror("Setsockopt(IPv6) failed");

            traceipv6(&sockaddr, fd, ttl);
        } else {
            if (setsockopt(fd, IPPROTO_IP, IP_TTL, &ttl, sizeof(ttl)) < 0)
                callerror("Setsockopt(IPv4) failed");

            traceipv4(&sockaddr, fd, ttl);
        }
    }

    return 0;
}

int traceipv6(u* sockaddr, int fd, int ttl) {

    if (sendto(fd, NULL, 0, 0, (struct sockaddr *) &sockaddr->sa_ipv6, sizeof(sockaddr->sa_ipv6)) < 0)
        callerror("Sendto failed\n");

    struct timeval send_time;
    gettimeofday(&send_time, nullptr);

    struct timeval def_maxwaittime;
    def_maxwaittime.tv_sec = 2;
    def_maxwaittime.tv_usec = 0;

    fd_set readset;
    FD_ZERO(&readset);
    FD_SET(fd, &readset);

    int receive;
    //wait for an activity on one of the sockets , timeout is NULL , so wait indefinitely
    receive = select(sizeof(readset), &readset, NULL, NULL, &def_maxwaittime);

    if (receive == -1) callerror("Select error.");
    if (receive == 0) {
        printf("%2d   *\n", ttl);
        return EXIT_SUCCESS;
    }

    char firstbuf[BUFLEN]; //buffer pre odpoveď
    char secondbuf[BUFLEN];
    struct iovec iov; //io štruktúra

    struct msghdr msg; //prijatá správa - môže obsahovať viac control hlavičiek
    struct cmsghdr *cmsg; //konkrétna control hlavička

    // anulovani
    memset(&msg, 0, sizeof(msg));
    memset(&iov, 0, sizeof(iov));
    memset(&firstbuf, 0, sizeof(firstbuf));
    memset(&secondbuf, 0, sizeof(secondbuf));

    iov.iov_base = firstbuf;
    iov.iov_len = sizeof(firstbuf);

    msg.msg_iov = &iov;
    msg.msg_iovlen = sizeof(iov);

    msg.msg_control = secondbuf;
    msg.msg_controllen = sizeof(secondbuf);

    int res = (int) recvmsg(fd, &msg, MSG_ERRQUEUE); //prijme správu
    if (res < 0)  callerror("recvmsg failed");

    struct timeval receive_time;
    gettimeofday(&receive_time, nullptr);

    double delay = (receive_time.tv_sec - send_time.tv_sec) * 1000.0;
    delay += (receive_time.tv_usec - send_time.tv_usec)/1000.0;


    /* lineárne viazaný zoznam - dá sa to napísať aj krajšie... */
    for (cmsg = CMSG_FIRSTHDR(&msg);cmsg; cmsg =CMSG_NXTHDR(&msg, cmsg))
    {
        /* skontrolujeme si pôvod správy - niečo podobné nám bude treba aj pre IPv6 */
        if (cmsg->cmsg_level == IPPROTO_IPV6 && cmsg->cmsg_type == IPV6_RECVERR)
        {
            //získame dáta z hlavičky
            struct sock_extended_err *e = (struct sock_extended_err*) CMSG_DATA(cmsg);

            //bude treba niečo podobné aj pre IPv6 (hint: iný flag)
            if (e && e->ee_origin == SO_EE_ORIGIN_ICMP6) {
                /* získame adresu - ak to robíte všeobecne tak sockaddr_storage */
                /*
                * v sin máme zdrojovú adresu
                * stačí ju už len vypísať viď: inet_ntop alebo getnameinfo
                */
                struct sockaddr_in6 *sin = (struct sockaddr_in6 *) (e + 1);
                createreply6(ttl, sin);

                if ( e->ee_type == ICMP6_DST_UNREACH ) {
                    switch ( e->ee_code ) {

                        // host unreachable
                        case ICMP6_DST_UNREACH_ADDR:
                            printf("H!\n");
                            exit(EXIT_FAILURE);

                            // network unreachable
                        case ICMP6_DST_UNREACH_NOROUTE:
                            printf("N!\n");
                            exit(EXIT_FAILURE);

                            // communication administratively prohibited
                        case ICMP6_DST_UNREACH_ADMIN:
                            printf("X!\n");
                            exit(EXIT_FAILURE);

                        case ICMP6_DST_UNREACH_NOPORT:
                            printf("%0.3f ms\n", delay);
                            printf("tralala");
                            exit(EXIT_SUCCESS);

                        default:
                            callerror("Undefined error code");
                    }

                } else if (e->ee_type == ICMP6_PARAM_PROB && e->ee_code == ICMP_PROT_UNREACH) {
                    // protocol unreachable
                    printf("P!\n");
                    exit(EXIT_FAILURE);
                } else if ( e->ee_type == ICMP6_TIME_EXCEEDED ) {
                    printf("%0.3f ms\n", delay);
                }
            }
        }
    }

    return EXIT_SUCCESS;
}

int traceipv4(u* sockaddr, int fd, int ttl) {

    if (sendto(fd, NULL, 0, 0, (struct sockaddr *) &sockaddr->sa_ipv4, sizeof(sockaddr->sa_ipv4)) < 0)
        callerror("Sendto failed\n");

    struct timeval send_time;
    gettimeofday(&send_time, nullptr);

    struct timeval def_maxwaittime;
    def_maxwaittime.tv_sec = 2;
    def_maxwaittime.tv_usec = 0;

    fd_set readset;
    FD_ZERO(&readset);
    FD_SET(fd, &readset);

    int receive;
    //wait for an activity on one of the sockets , timeout is NULL , so wait indefinitely
    receive = select(sizeof(readset), &readset, NULL, NULL, &def_maxwaittime);

    if (receive == -1) callerror("Select error.");
    if (receive == 0) {
        printf("%2d   *\n", ttl);
        return EXIT_SUCCESS;
    }

    char firstbuf[BUFLEN]; //buffer pre odpoveď
    char secondbuf[BUFLEN];
    struct iovec iov; //io štruktúra

    struct msghdr msg; //prijatá správa - môže obsahovať viac control hlavičiek
    struct cmsghdr *cmsg; //konkrétna control hlavička

    // anulovani
    memset(&msg, 0, sizeof(msg));
    memset(&iov, 0, sizeof(iov));
    memset(&firstbuf, 0, sizeof(firstbuf));
    memset(&secondbuf, 0, sizeof(secondbuf));

    iov.iov_base = firstbuf;
    iov.iov_len = sizeof(firstbuf);

    msg.msg_iov = &iov;
    msg.msg_iovlen = sizeof(iov);

    msg.msg_control = secondbuf;
    msg.msg_controllen = sizeof(secondbuf);

    int res = (int) recvmsg(fd, &msg, MSG_ERRQUEUE); //prijme správu
    if (res < 0)  callerror("recvmsg failed");

    struct timeval receive_time;
    gettimeofday(&receive_time, nullptr);

    double delay = (receive_time.tv_sec - send_time.tv_sec) * 1000.0;
    delay += (receive_time.tv_usec - send_time.tv_usec)/1000.0;

    /* lineárne viazaný zoznam - dá sa to napísať aj krajšie... */
    for (cmsg = CMSG_FIRSTHDR(&msg);cmsg; cmsg =CMSG_NXTHDR(&msg, cmsg))
    {
        /* skontrolujeme si pôvod správy - niečo podobné nám bude treba aj pre IPv6 */
        if (cmsg->cmsg_level == IPPROTO_IP && cmsg->cmsg_type == 11)
        {
            //získame dáta z hlavičky
            struct sock_extended_err *e = (struct sock_extended_err*) CMSG_DATA(cmsg);

            //bude treba niečo podobné aj pre IPv6 (hint: iný flag)
            if (e && e->ee_origin == SO_EE_ORIGIN_ICMP) {
                /* získame adresu - ak to robíte všeobecne tak sockaddr_storage */
                /*
                * v sin máme zdrojovú adresu
                * stačí ju už len vypísať viď: inet_ntop alebo getnameinfo
                */
                struct sockaddr_in *sin = (struct sockaddr_in *) (e + 1);
                createreply4(ttl, sin);

                if ( e->ee_type == ICMP_DEST_UNREACH ) {


                    switch ( e->ee_code ) {

                        // host unreachable
                        case ICMP_HOST_UNREACH:
                            printf("H!\n");
                            exit(EXIT_FAILURE);

                            // network unreachable
                        case ICMP_NET_UNREACH:
                            printf("N!\n");
                            exit(EXIT_FAILURE);

                            // protocol unreachable
                        case ICMP_PROT_UNREACH:
                            printf("P!\n");
                            exit(EXIT_FAILURE);

                            // communication administratively prohibited
                        case ICMP_PKT_FILTERED:
                            printf("X!\n");
                            exit(EXIT_FAILURE);

                        case ICMP_PORT_UNREACH:
                            printf("%0.3f ms\n", delay);
                            exit(EXIT_SUCCESS);

                        default:
                            callerror("Undefined error code");
                    }


                } else if ( e->ee_type == ICMP_TIME_EXCEEDED ) {
                    printf("%0.3f ms\n", delay);
                }
            }
        }
    }

    return EXIT_SUCCESS;
}

void makesocaddr(tOptions *ptr, u *sockaddr){
    struct hostent  *he;

    if(inet_pton(AF_INET, ptr->ipadress.c_str(), &(sockaddr->sa_ipv4.sin_addr)) == 1) {
        sockaddr->sa_ipv4.sin_family = AF_INET;
        sockaddr->sa_ipv4.sin_port  = htons(33434);
    }
    else if (inet_pton(AF_INET6, ptr->ipadress.c_str(), &(sockaddr->sa_ipv6.sin6_addr)) == 1) {
        sockaddr->sa_ipv6.sin6_family = AF_INET6;
        sockaddr->sa_ipv6.sin6_port  = htons(33434);
        sockaddr->ipv6 = true;
    } else if ((he = gethostbyname(ptr->ipadress.c_str())) != NULL ) {
        sockaddr->sa_ipv4.sin_family = AF_INET;
        sockaddr->sa_ipv4.sin_port  = htons(33434);
        bcopy(he->h_addr, &sockaddr->sa_ipv4.sin_addr.s_addr, (size_t) he->h_length);
    }
    else callerror("Unknown host");
}

void createreply4(int ttl,sockaddr_in* sock) {
    char str[INET_ADDRSTRLEN];
    char hbuf[BUFLEN];
    struct sockaddr_in sa;

    // now get it back and print it
    inet_ntop(AF_INET, &(sock->sin_addr), str, INET_ADDRSTRLEN);

    /* For IPv4*/
    sa.sin_family = AF_INET;
    sa.sin_addr = sock->sin_addr;


    if ((getnameinfo((struct sockaddr *) &sa, sizeof(sockaddr_in6), hbuf, sizeof(hbuf), NULL, 0, NI_NAMEREQD)) != 0) {
        strncpy(hbuf,str,BUFLEN);
    }

    printf("%2d   %s   (%s)   ", ttl,hbuf, str);

}

void createreply6(int ttl,sockaddr_in6* sock) {
    char str[INET6_ADDRSTRLEN];
    char hbuf[BUFLEN];
    struct sockaddr_in6 sa;

    // now get it back and print it
    inet_ntop(AF_INET6, &(sock->sin6_addr), str, INET6_ADDRSTRLEN);

    /* For IPv6*/
    sa.sin6_family = AF_INET6;
    sa.sin6_addr = sock->sin6_addr;


    if ((getnameinfo((struct sockaddr *) &sa, sizeof(sockaddr_in6), hbuf, sizeof(hbuf), NULL, 0, NI_NAMEREQD)) != 0) {
        strncpy(hbuf,str,BUFLEN);
    }

    printf("%2d   %s   (%s)   ", ttl,hbuf, str);


}

// funkce pro zpracovani argumentu
int processArg(int argc, char* argv[], tOptions* ptr) {
    bool ipadress = false;

    /* 1. test vstupnich parametru: */
    if (argc > 6) callerror("Too much arguments");

    if (argc == 1) callerror("No arguments");

    int i = 1;

    while (i < argc) {

        std::string temporary = argv[i];
        //std::string::size_type sz;

        if ((temporary == "-f") && (i + 1 < argc)){
            temporary = argv[i+1];
            if (!checkdigit(temporary)) callerror("Min TTL is not integer");

            ptr->firstttl = atoi(temporary.c_str());

            if (ptr->firstttl < 1) callerror("Min TTL is out of range");

            i++;
        } else if ((temporary == "-m") && (i + 1 < argc)){
            temporary = argv[i+1];
            if (!checkdigit(temporary)) callerror("Max TTL is not integer");

            ptr->maxttl = atoi(temporary.c_str());

            if (ptr->maxttl < 1) callerror("Max TTL is  out of range");
            i++;
        } else if (!ipadress) {
            ptr->ipadress = argv[i];
            ipadress = true;
        }
        else callerror("Wrong arguments");

        i++;
    }

    if (!ipadress) callerror("Missing IPv4/IPv6 argument");

    return EXIT_SUCCESS;
}

bool checkdigit(std::string s){
    return s.find_first_not_of( "0123456789" ) == std::string::npos;
}

void callerror(std::string error){
    fprintf(stderr,"ERROR: %s\n", error.c_str());
    exit(EXIT_FAILURE);
}

int debug(tOptions* ptr , u* sockaddr) {
    printf("///////////// DEBUG ////////////////\n");
    printf("result of first ttl: %d \n", ptr->firstttl);
    printf("result of max ttl: %d\n", ptr->maxttl);
    printf("result of ipadress: %s\n", ptr->ipadress.c_str());
    if (!sockaddr->ipv6) printf("hostname: %s\n", inet_ntoa(sockaddr->sa_ipv4.sin_addr));
    else printf("hostname is IPv6\n");
    printf("////////// END DEBUG /////////////\n\n");
    return EXIT_SUCCESS;
}
