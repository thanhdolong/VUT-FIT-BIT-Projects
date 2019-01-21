// ISA Project
// Variant: LDAP server
//
// Author: Do Long Thanh
// <xdolon00@stud.fit.vutbr.cz>

#include <fstream>
#include <sstream>
#include <getopt.h>
#include <regex>

#include "tools.hpp"
#include "ldap.hpp"

/**
 * Parses arguments and stores them in the class attributes.
 * @param argc number of arguments
 * @param argv array of arguments
 * @param ptr save information from parsing in structure
 * @return parsing is succesful
 */
int processArg(int argc, char* argv[], tOptions* ptr) {
    int c;
    struct option longOpts[] = {
            {"help", no_argument, NULL, 'h'},
            { NULL, 0, NULL, 0 }
    };

    int longIndex = 0;
    while ((c = getopt_long(argc, argv, "p:f:h", longOpts, &longIndex)) != -1)
        switch (c)
        {
            case 'p':
                for (unsigned int i=0; i<strlen(optarg); i++) {
                    if (!isdigit(optarg[i])) callError("Value is not an integer");
                }

                ptr->port = (int)atol(optarg);
                break;

            case 'f':
                ptr->file = optarg;
                break;

            case 'h':
                help();

            case 0:
                help();

            default:
                help();
        }

    if(ptr->file.empty()) {
        printf("File is required\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}

/**
 * Load file and creating database of users
 * Some parts of code are adapted from URL below.
 * https://stackoverflow.com/questions/6089231/getting-std-ifstream-to-handle-lf-cr-and-crlf
 * @param ptr path to file and port
 */
void loadFile(tOptions* ptr) {
    std::ifstream input(ptr->file);
    if (!input.is_open()) callError("Unable to open file");

    std::string line;
    while (std::getline(input, line))
    {
        if (line.size() && line[line.size() - 1] == '\r') {
            line = line.substr(0, line.size() - 1);
        }

        std::istringstream linestream(line);
        std::string item;
        auto newPerson = std::make_shared<person>();

        getline (linestream, item, ';');
        newPerson->cn = item;

        getline (linestream, item, ';');
        newPerson->uid = item;

        getline (linestream, item);
        newPerson->email = item;

        universe.insert(newPerson);
    }

    input.close();
}

/**
 * Returns the character currently pointed
 * custom fgetc to store position for searchRequest
 * @param stream The pointer to a FILE object that identifies the stream
 * @return Current character in the object
 */
int customFgetc(FILE *stream) {
    ++position;
    return fgetc(stream);
}

/**
 * Method for get length
 * @param stream The pointer to a FILE object that identifies the stream
 * @return length
 */
size_t getLength(FILE *stream) {
    int l = customFgetc(stream);
    if (l > 0x80) {
        int numbers = l - 0x80;
        l = 0;
        while (numbers > 0) {
            size_t octet = customFgetc(stream);
            octet = octet << (--numbers * 8);
            l += octet;
        }
    }
    return l;
}

/**
 * Convert request to the regex
 * @param stream The pointer to a FILE object that identifies the stream
 * @param pattern Creating regex request
 * @param fd Socket for sending message
 */
void convertSubstringToRegex(FILE *stream, size_t *len, std::string &pattern, int fd) {
    auto begin = position;
    unsigned char op = (unsigned char) customFgetc(stream);

    if (op < 0x80 || op > 0x82) ldapResponse(fd, LDAP_PROTOCOL_ERROR);

    size_t local_len = (size_t) getLength(stream);

    if (op == 0x81 || op == 0x82)
        pattern += ".*";

    // Found dot and make it non-regex
    for (size_t i = 0; i < local_len; i++) {
        auto c = customFgetc(stream);

        if (c == '.') pattern.push_back('\\');
        pattern.push_back((char) c);
    }

    if (op == 0x80 || op == 0x81)
        pattern += ".*";

    *len -= position - begin;
}

/**
 * Handle with errors
 */
void callError(std::string error){
    std::cerr << "ERROR: " << error << std::endl;
    exit(EXIT_FAILURE);
}

/**
 * Prints help and exits the program.
 */
void help() {
    std::cout << R"(LDAP server)" << std::endl
              << R"(Usage: ./myldap {-p <port>} -f <file>)" << std::endl
              << R"(Example: ./myldap -p 3042 -f ldap.csv)" << std::endl << std::endl
              << R"([-p] - optional attribute - port number of the host to connect (default value is 389))" << std::endl
              << R"([-f] - required attribute - file path to database)" << std::endl
              << R"(Author: Do Long Thanh <xdolon00@stud.fit.vutbr.cz>)" << std::endl;
    exit(EXIT_SUCCESS);
}
