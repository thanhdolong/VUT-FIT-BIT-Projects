// ISA Project
// Variant: LDAP server
//
// Author: Do Long Thanh
// <xdolon00@stud.fit.vutbr.cz>

#ifndef SERVER_TOOLS_H
#define SERVER_TOOLS_H

#include <iostream>

typedef struct {
    int port = 389;
    std::string file;
} tOptions;

void callError(std::string error);
int processArg(int argc, char* argv[], tOptions* ptr);
void loadFile(tOptions* ptr);
void help();
size_t getLength(FILE *stream);

int customFgetc(FILE *stream);

void convertSubstringToRegex(FILE *stream, size_t *len, std::string &pattern, int fd);

#endif //SERVER_TOOLS_H
