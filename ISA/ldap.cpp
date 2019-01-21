// ISA Project
// Variant: LDAP server
//
// Author: Do Long Thanh
// <xdolon00@stud.fit.vutbr.cz>

#include <unistd.h>
#include <algorithm>
#include <regex>

#include "ldap.hpp"
#include "tools.hpp"


std::set<std::shared_ptr<person>> universe;
size_t position = 0;

/**
 * Parse messages incoming from LDAP client.
 * The implementation is based on finite-state automaton.
 * @param fd Socket for sending message
 */
void service(int fd) {
    char buf[BUFFER];
    int n;
    while ((n = (int) read(fd, buf, 1)) > 0) {
        // Check if it is LDAP message
        if (buf[0] == 0x30) {

            FILE *stream = fdopen(fd, "r+");
            if (stream == nullptr) callError("fdopen error");

            //get length of LDAP message
            getLength(stream);

            unsigned char actualChar = (unsigned char) fgetc(stream);
            unsigned char lengthOfMessageId = (unsigned char) fgetc(stream);
            if (actualChar == 0x02 && (lengthOfMessageId >= 0x01 || lengthOfMessageId <= 0x04)) {

                // Reading MessageID
                for (int i = 0; i < (int) lengthOfMessageId; ++i) {
                    fgetc(stream);
                }
            }

            actualChar = (unsigned char) fgetc(stream);

            switch (actualChar) {
                /**
                * 0x60 - BindRequest
                */
                case BIND_REQUEST: {
                    ldapResponse(fd, LDAP_BIND_RESPONSE);
                    break;
                }

                /**
                 * 0x63 - SearchRequest
                 */
                case SEARCH_REQUEST: {
                    size_t len = 0;
                    int sizeLimit = parseSearchResponseHeader(fd, stream);
                    std::shared_ptr<std::set<std::shared_ptr<person>>> results = processSearchRequest(stream, &len, fd);
                    searchResultEntry(results, fd, sizeLimit);
                    break;
                }

                /**
                 * 0x42 - UnbindRequest
                 */
                case UNBIND_REQUEST: break;

                default:
                    ldapResponse(fd, LDAP_PROTOCOL_ERROR);
                    break;
            }
        }
    } if (n == -1) callError("read()");
}

/**
 * Parse header from SearchRequest
 * @param fd Socket for sending message
 * @param stream The pointer to a FILE object that identifies the stream
 * @return The maximum number of entries that should be returned as a result of search.
 */
int parseSearchResponseHeader(int fd, FILE *stream) {
    int sizeLimit = 0;

    for (int state = 0; state < 6; ++state) {
        switch (state) {

            /**
             * Only the base object is examined
             * Base object is not supported
             */
            case BASE_OBJECT: {
                getLength(stream);
                if (fgetc(stream) != 0x04) {
                    state = SEARCH_REQUEST_ERROR;
                    break;
                }

                fgetc(stream);
                break;
            }

            /**
             * Specifies how deep within the DIT to search from the base object
             * Scope is not supported
             */

            case SCOPE: {
                unsigned char actualChar = (unsigned char) fgetc(stream);
                unsigned char nextChar = (unsigned char) fgetc(stream);

                if((actualChar != 0x0A) && (nextChar != 0x01)) {
                    state = SEARCH_REQUEST_ERROR;
                    break;
                }

                fgetc(stream);
                break;
            }


            /**
             * Specifies if aliases are dereferenced
             * Alias dereferencing is not supported
             */
            case DEREF_ALIASES: {

                unsigned char actualChar = (unsigned char) fgetc(stream);
                unsigned char nextChar = (unsigned char) fgetc(stream);

                if((actualChar != 0x0A) && (nextChar != 0x01)) {
                    state = SEARCH_REQUEST_ERROR;
                    break;
                }

                fgetc(stream);
                break;
            }

            /**
             * The maximum number of entries that should be returned as a result of search.
             * SizeLimit is supported
             */
            case SIZE_LIMIT: {
                if (fgetc(stream) != 0x02) {
                    state = SEARCH_REQUEST_ERROR;
                    break;
                }

                unsigned char numberOfChar = (unsigned char) fgetc(stream);
                if (numberOfChar > 0x04 || numberOfChar < 0x01) {
                    state = SEARCH_REQUEST_ERROR;
                    break;
                }

                for (int i = 0; i < numberOfChar; ++i) {
                    sizeLimit = (sizeLimit << 8) | (fgetc(stream) & 0xff);
                }

                break;
            }

            /**
             * The maximum number of seconds allowed to perform the search
             * TimeLimit is not supported
             */
            case TIME_LIMIT: {
                if(fgetc(stream) != 0x02) {
                    state = SEARCH_REQUEST_ERROR;
                    break;
                }

                unsigned char numberOfChar = (unsigned char) fgetc(stream);
                if (numberOfChar > 0x04 || numberOfChar < 0x01) {
                    state = SEARCH_REQUEST_ERROR;
                    break;
                }

                for (int i = 0; i < numberOfChar; ++i) {
                    fgetc(stream);
                }

                break;
            }
            /**
             * Specifies typesonly
             * typesonly is not supported
             */
            case TYPES_ONLY: {
                unsigned char actualChar = (unsigned char) fgetc(stream);
                unsigned char nextChar = (unsigned char) fgetc(stream);
                if ((actualChar != 0x01) && (nextChar != 0x01)) {
                    state = SEARCH_REQUEST_ERROR;
                    break;
                }

                fgetc(stream);
                break;
            }
            case SEARCH_REQUEST_ERROR: {
                ldapResponse(fd, LDAP_PROTOCOL_ERROR);
                break;
            }

            default: break;
        }
    }

    return sizeLimit;
}

/**
 * Find match a given set of criteria.
 * @param stream The pointer to a FILE object that identifies the stream
 * @param len
 * @param fd Socket for sending message
 * @return zero or more entries
 */
std::shared_ptr<std::set<std::shared_ptr<person>>> processSearchRequest (FILE* stream, size_t* len, int fd) {
    auto begin = position;
    unsigned char op = (unsigned char) customFgetc(stream);
    size_t local_len = (size_t) getLength(stream);

    std::shared_ptr<std::set<std::shared_ptr<person>>> set = nullptr;
    switch(op) {
        case SUBSTRINGS_FILTER: {
            //warning, size may be coded in more bytes
            customFgetc(stream); //should be 04
            size_t l = (size_t) getLength(stream);
            std::string column;
            column.reserve(l+1);
            for (unsigned i = 0; i < l; ++i)
                column.push_back(customFgetc(stream));

            customFgetc(stream); //should be 30

            l = (size_t) getLength(stream);

            std::string pattern = "";
            while (l > 0) {
                convertSubstringToRegex(stream, &l, pattern, fd);
            }

            std::regex regex(pattern);

            set = std::make_shared<std::set<std::shared_ptr<person>>>();
            for (auto & element: universe) {
                std::string* member = nullptr;
                if (column == "cn")
                    member = &(element->cn);
                else if (column == "uid")
                    member = &(element->uid);
                else if (column == "mail")
                    member = &(element->email);
                else break;

                if (std::regex_match(*member, regex))
                    set->insert(element);
            }
        } break;

        case EQUALITY_MATCH_FILTER: {
            //warning, size may be coded in more bytes
            customFgetc(stream); //should be 04
            size_t l = (size_t) getLength(stream);
            std::string column;
            column.reserve(l+1);
            for (unsigned i = 0; i < l; ++i)
                column.push_back(customFgetc(stream));

            customFgetc(stream); //should be 04

            l = (size_t) getLength(stream);
            std::string value;
            value.reserve(l+1);
            for (unsigned i = 0; i < l; ++i)
                value.push_back(customFgetc(stream));

            set = std::make_shared<std::set<std::shared_ptr<person>>>();
            for (auto & element: universe) {
                std::string* member = nullptr;
                if (column == "cn")
                    member = &(element->cn);
                else if (column == "uid")
                    member = &(element->uid);
                else if (column == "mail")
                    member = &(element->email);
                else break;

                if (*member == value)
                    set->insert(element);
            }
        } break;

        case NOT_FILTER: {
            set = std::make_shared<std::set<std::shared_ptr<person>>>();
            auto s = processSearchRequest(stream, &local_len, fd);
            std::set_difference(universe.begin(), universe.end(), s->begin(), s->end(), std::inserter(*set, set->begin()));
        } break;

        case OR_FILTER: {
            set = processSearchRequest(stream, &local_len, fd);
            while (local_len > 0) {
                auto second = processSearchRequest(stream, &local_len, fd);
                auto result = std::make_shared<std::set<std::shared_ptr<person>>>();
                std::set_union(set->begin(), set->end(), second->begin(), second->end(), std::inserter(*result, result->begin()));
                set = result;
            }
        } break;

        case AND_FILTER: {
            set = processSearchRequest(stream, &local_len, fd);
            while (local_len > 0) {
                auto second = processSearchRequest(stream, &local_len, fd);
                auto result = std::make_shared<std::set<std::shared_ptr<person>>>();
                std::set_intersection(set->begin(), set->end(), second->begin(), second->end(), std::inserter(*result, result->begin()));
                set = result;
            }
        } break;

        default:
            ldapResponse(fd, LDAP_PROTOCOL_ERROR);
            break;
    }

    *len -= position - begin;
    return set;
}

/**
 * Each LDAPMessage searchResEntry() is a result from the ldap server.
 * It will contain at least the DN of the entry, and may contain zero or more attributes.
 * @param results
 * @param fd Socket for sending message
 * @param sizeLimit
 */
void searchResultEntry(std::shared_ptr<std::set<std::shared_ptr<person>>> results, int fd, int sizeLimit) {
    int numberOfSendEntry = 0;
    int numberOfAnswers = (int) results->size();

    for (auto user = results->begin(); user != results->end() ; user++) {
        std::string searchResponse = {};

        std::string uid = (*user)->uid;
        std::string cn = (*user)->cn;
        std::string email = (*user)->email;

        searchResponse += 0x30;
        searchResponse += cn.length() + email.length() + uid.length() + 35;
        searchResponse += {0x02, 0x01, 0x02, 0x64};
        searchResponse += cn.length() + email.length() + uid.length() + 30;
        searchResponse += 0x04;
        searchResponse += uid.length() + 4;
        searchResponse += {"uid"};
        searchResponse += 0x3d;

        while(!uid.empty()) {
            searchResponse += uid.front();
            uid.erase(0,1);
        }

        searchResponse += 0x30;
        searchResponse += cn.length() + email.length() + 22;
        searchResponse += 0x30;
        searchResponse += cn.length() + 8;
        searchResponse += {0x04, 0x02};
        searchResponse += {"cn"};
        searchResponse += 0x31;
        searchResponse += cn.length() + 2;
        searchResponse += 0x4;
        searchResponse += cn.length();

        while(!cn.empty()) {
            searchResponse += cn.front();
            cn.erase(0,1);
        }

        searchResponse += 0x30;
        searchResponse += email.length() + 10;
        searchResponse += {0x04, 0x04};
        searchResponse += {"mail"};
        searchResponse += 0x31;
        searchResponse += email.length() + 2;
        searchResponse += 0x04;
        searchResponse += email.length();

        while(!email.empty()) {
            searchResponse += email.front();
            email.erase(0,1);
        }

        if ((sizeLimit == 0) || (numberOfSendEntry < sizeLimit)) {
            numberOfSendEntry++; int r = (int) write(fd, searchResponse.c_str(), searchResponse.length());

            if (r == -1) callError("write()");
            if (r != (int) searchResponse.length()) callError("write(): Buffer written just partially");
        } else results->end();

    }

    /**
     * If user set size limit, it is neccessary to check if it is send incomplete results.
     */
    if(numberOfSendEntry < numberOfAnswers) ldapResponse(fd, LDAP_SIZELIMIT_EXCEEDED);
    else ldapResponse(fd, LDAP_SUCCESS);
}

/**
 * Responses from LDAP server
 * @param fd
 * @param reply
 */
void ldapResponse(int fd, int reply){
    std::string searchResponse;

    switch(reply){
        case LDAP_SUCCESS:
            searchResponse = {0x30, 0x0C, 0x02, 0x01, 0x02, 0x65, 0x07, 0x0A, 0x01, 0x00, 0x04, 0x00, 0x04, 0x00};
            break;
        case LDAP_SIZELIMIT_EXCEEDED:
            searchResponse = {0x30, 0x0C, 0x02, 0x01, 0x02, 0x65, 0x07, 0x0A, 0x01, 0x04, 0x04, 0x00, 0x04, 0x00};
            break;
        case LDAP_PROTOCOL_ERROR:
            searchResponse = {0x30, 0x0C, 0x02, 0x01, 0x02, 0x65, 0x07, 0x0A, 0x01, 0x02, 0x04, 0x00, 0x04, 0x00};
            break;
        case LDAP_BIND_RESPONSE:
            searchResponse = {0x30, 0x0c, 0x02, 0x01, 0x01, 0x61, 0x07, 0x0a, 0x01, 0x00, 0x04, 0x00, 0x04, 0x00};
            break;
        default:break;
    }

    int r = (int) write(fd, searchResponse.c_str(), searchResponse.length());

    if (r == -1) callError("write()");
    if (r != (int) searchResponse.length()) callError("write(): Buffer written just partially");
}