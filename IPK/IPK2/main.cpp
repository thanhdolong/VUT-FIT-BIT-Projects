#include "main.h"

int main (int argc, char * argv[]) {
    //socklen_t serverlen;
    tOptions trace;

    ///////////// step 1 - initializing ////////////////
    processArg(argc, argv, &trace);
    debug(&trace);


    return 0;
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

            if (ptr->firstttl < 1 || ptr->firstttl > 30) callerror("Min TTL is out of range");

            i++;
        } else if ((temporary == "-m") && (i + 1 < argc)){
            temporary = argv[i+1];
            if (!checkdigit(temporary)) callerror("Max TTL is not integer");

            ptr->maxttl = atoi(temporary.c_str());

            if (ptr->maxttl < 1 || ptr->maxttl > 30) callerror("Max TTL is  out of range");
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

int debug(tOptions* ptr) {
    printf("///////////// DEBUG ////////////////\n");
    printf("result of first ttl: %d \n", ptr->firstttl);
    printf("result of max ttl: %d\n", ptr->maxttl);
    printf("result of ipadress: %s\n", ptr->ipadress.c_str());
    printf("////////// END DEBUG /////////////\n\n");
    return EXIT_SUCCESS;
}
