// ISA Project
// Variant: LDAP server
//
// Author: Do Long Thanh
// <xdolon00@stud.fit.vutbr.cz>

#ifndef PROC_REQ
#define PROC_REQ

#include <set>
#include <memory>
#include <string>

#define BUFFER	(1024)

#define BASE_OBJECT (0)
#define SCOPE (1)
#define DEREF_ALIASES (2)
#define SIZE_LIMIT (3)
#define TIME_LIMIT (4)
#define TYPES_ONLY (5)
#define SEARCH_REQUEST_ERROR (6)

#define BIND_REQUEST (0x60)
#define SEARCH_REQUEST (0x63)
#define UNBIND_REQUEST (0x42)

#define AND_FILTER (0xA0)
#define OR_FILTER (0xA1)
#define NOT_FILTER (0xA2)
#define EQUALITY_MATCH_FILTER (0xA3)
#define SUBSTRINGS_FILTER (0xA4)

#define LDAP_SUCCESS (0)
#define LDAP_PROTOCOL_ERROR (2)
#define LDAP_BIND_RESPONSE (3)
#define LDAP_SIZELIMIT_EXCEEDED (4)

struct person {
    std::string cn;
    std::string uid;
    std::string email;
};

extern std::set<std::shared_ptr<person>> universe;
extern size_t position;

void service(int fd);
void ldapResponse(int fd, int reply);
int parseSearchResponseHeader(int fd, FILE *stream);
void searchResultEntry(std::shared_ptr<std::set<std::shared_ptr<person>>> results, int fd, int sizeLimit);
std::shared_ptr<std::set<std::shared_ptr<person>>> processSearchRequest (FILE* stream, size_t* len, int fd);

#endif
