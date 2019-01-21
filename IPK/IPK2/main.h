//
// Created by Thành Đỗ Long on 13/04/2017.
//

/***** C knihovny *****/
#include <stdio.h>
//#include <ctype.h>
//#include <stdlib.h>
//#include <unistd.h>
//#include <string.h>
//#include <time.h>

//#include <sys/socket.h>
//#include <sys/types.h>
//#include <netinet/in.h>
//#include <netdb.h>
//#include <unistd.h>
//#include <arpa/inet.h>

/***** C++ knihovny ******/
//#include <iostream>
//#include <vector>
//#include <sstream>
//#include <fstream>
#include <string>
#include <cstdlib>

typedef struct {
    int firstttl = 1;
    int maxttl = 30;

    std::string ipadress;
} tOptions;


int processArg(int argc, char* argv[], tOptions*);
int debug(tOptions*);
bool checkdigit(std::string s);
void callerror(std::string error);