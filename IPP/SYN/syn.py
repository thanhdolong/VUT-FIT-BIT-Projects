#!/usr/bin/env python3
# coding=utf-8
# 10/10

# -----------------------------------------------------------------------------
import re  # library for regex
import os  # library for filesystem
import sys  # library for arguments
import codecs  # library for open/close file

arg = {'formatfile': sys.stdin, 'inputfile': sys.stdin, 'outputfile': sys.stdout, 'br': False, 'help': False,
       'emptyformat': False}
formatargs = []

# volani chybove hlaseni
def callerror(text, number):
    sys.stderr.write("Error: " + str(text) + "\n")
    exit(number)

# debuj
def debug():
    print("\n\n-----Now u can see arguments-----\n")
    print("Formatovaci soubor:\t" + str(arg['formatfile']))
    print("Vstupni soubor:\t\t" + str(arg['inputfile']))
    print("Vystupni soubor:\t" + str(arg['outputfile']))
    print("Element < /br>:\t\t" + str(arg['br']))
    print("Empty format:\t\t" + str(arg['emptyformat']))
    print("\n---------------END---------------\n\n")
    pass

# zpracovavani argumentu
def arguments():
    callhelp = False
    format = False
    input = False
    output = False

    i = 1

    for args in sys.argv[1:]:

        if (arg['help'] == True and i > 1) or (args == "--help" and i > 1):
            callerror('Some trouble with --help', 1)

        if args == "--help" or args == "-h":
            if callhelp:
                callerror('Redundant arguments', 1)
            arg['help'] = True
        elif args[0:9] == "--format=":
            if format:
                callerror('Redundant arguments', 1)

            arg['formatfile'] = re.sub(r'^--format=', "", sys.argv[i])
            format = True
        elif args[0:8] == "--input=":
            if input:
                callerror('Redundant arguments', 1)

            arg['inputfile'] = re.sub(r'^--input=', "", sys.argv[i])

            try:
                os.stat(arg['inputfile'])
                os.path.isfile(arg['inputfile'])
                os.path.exists(arg['inputfile'])
                os.access(arg['inputfile'], os.R_OK)
                arg['inputfile'] = codecs.open(arg['inputfile'], 'r', 'utf-8')
            except:
                callerror("Can't open input file", 2)

            input = True
        elif args[0:9] == "--output=":
            if output:
                callerror('Redundant arguments', 1)
            arg['outputfile'] = re.sub(r'^--output=', "", sys.argv[i])

            try:
                arg['outputfile'] = codecs.open(arg['outputfile'], 'w', 'utf-8')
            except:
                callerror("Can't open output file", 3)

            output = True
        elif args == "--br":
            if arg['br']:
                callerror('Redundant arguments', 1)
            arg['br'] = True
        else:
            callerror('Unrecognized arguments', 1)
        i += 1

    return arg

# volani --help
def helptext():
    print("usage: sys.py [-h] [--format=FORMAT] [--input=INPUT] [--output=OUTPUT] [--br]\n")
    print("optional arguments:")
    print("-h, --help       \tshow this help message and exit")
    print("--format FORMAT  \tCesta k formatovacimu souboru")
    print("--input INPUT    \tCesta ku vstupnimu souboru")
    print("--output OUTPUT  \tCesta k vystupnimu souboru")
    print("--br             \tPridani tagu </ br> na konec kazdeho radku vystupniho suboru")
    exit(0)

# generovani tagu
def createtag(tag):
    newtag = []
    opentag = ""
    closetag = ""

    for index in range(len(tag)):
        if tag[index] == "bold":
            opentag = opentag + "<b>"
            closetag = "</b>" + closetag

        elif re.search(r"color:[0-9a-fA-F]{6}", tag[index]):
            tag[index] = re.sub(":", "=#", tag[index], count=1)
            opentag = opentag + "<font " + str(tag[index]) + ">"
            closetag = "</font>" + closetag

        elif tag[index] == "italic":
            opentag = opentag + "<i>"
            closetag = "</i>" + closetag

        elif tag[index] == "teletype":
            opentag = opentag + "<tt>"
            closetag = "</tt>" + closetag

        elif re.search(r"size:[1-7]", tag[index]):
            tag[index] = re.sub(":", "=", tag[index], count=1)
            opentag = opentag + "<font " + tag[index] + ">"
            closetag = "</font>" + closetag

        elif tag[index] == "underline":
            opentag = opentag + "<u>"
            closetag = "</u>" + closetag

    newtag.append(opentag)
    newtag.append(closetag)
    return newtag

# vytvareni formatovaciho listu
def formatlist():
    checkpattern = "^[^\t]+\t+(bold|color:[0-9a-fA-F]{6}|italic|size:[1-7]|teletype|underline)(,[\s\t]*(bold|color:[0-9a-fA-F]{6}|italic|size:[1-7]|teletype|underline)[\s\t]*)*$"
    formatargs = []
    i = 0

    try:
        formaffile = open(arg['formatfile'])
    except:
        arg['emptyformat'] = True
        generateoutput(arg)
        return formatargs

    for line in formaffile:
        if line == "\n": continue
        if re.search(checkpattern, line, re.I | re.M):
            line = re.sub("\t+", ",", line, count=1)
            line = line.split(',')
            formatargs.append(line)

            # Convert regex
            formatargs[i][0] = convertregex(formatargs[i][0])

            # Delete all unnecessary backspaces
            for index in range(len(formatargs[i])):
                if formatargs[i][0] == formatargs[i][index]: continue
                formatargs[i][index] = re.sub(r"\s", "", formatargs[i][index])

            # Find open tags and end tags
            formatargs[i][1:] = createtag(formatargs[i][1:])

        else:
            callerror("Not valid format file", 4)
        i += 1

    return formatargs


def convertregex(regex):
    specialoperator = False

    pcreexpression = ["^", "$", "?", "[", "]", "{", "}", "\\", "-", "/"]
    quantifier = [".", "|", "*", "+", "(", ")"]
    checkquantifier = {".": False, "|": False, "*": False, "+": False, "(": False}
    validation = ["*", "+", ".", "|", ")"]
    negation = ""
    tmpregex = []
    newregex = ""

    for i in range(len(regex)):

        if specialoperator == True:
            specialoperator = False

        elif regex[i] == "!":
            if regex[i] == "!" and len(regex) - 1 == i: callerror("Bad regex", 4)
            if negation == "^": callerror("Bad regex", 4)
            negation = "^"

        elif regex[i] in pcreexpression:
            tmpregex.append('[' + negation + "\\" + regex[i] + ']')
            negation = ""

        elif regex[i] in quantifier:
            if regex[i] == "." and len(regex) - 1 == i:
                callerror("Bad regex", 4)
            elif regex[i] == "." and i == 0:
                callerror("Bad regex", 4)
            elif regex[i] == "." and len(regex) - 1 != i and regex[i + 1] == ".":
                callerror("Bad regex", 4)

            if negation == "^": callerror("Bad regex", 4)
            tmpregex.append(regex[i])

        elif regex[i] == '%':
            if (regex[i] == len(regex) - 1): callerror("Bad regex", 4)

            if (regex[i + 1] == 's'):   newregex += '[' + negation + ' \t\n\r\f\v]'
            elif (regex[i + 1] == 'a'): newregex += '[' + negation + '\s\S]'
            elif (regex[i + 1] == 'd'): newregex += '[' + negation + '0-9]'
            elif (regex[i + 1] == 'l'): newregex += '[' + negation + 'a-z]'
            elif (regex[i + 1] == 'L'): newregex += '[' + negation + 'A-Z]'
            elif (regex[i + 1] == 'w'): newregex += '[' + negation + 'a-zA-Z]'
            elif (regex[i + 1] == 'W'): newregex += '[' + negation + 'a-zA-Z0-9]'
            elif (regex[i + 1] == 't'): newregex += '[' + negation + '\t]'
            elif (regex[i + 1] == 'n'): newregex += '[' + negation + '\n]'
            elif (regex[i + 1] == '.'): newregex += '[' + negation + '\.]'
            elif (regex[i + 1] == '|'): newregex += '[' + negation + '\|]'
            elif (regex[i + 1] == '!'): newregex += '[' + negation + '\!]'
            elif (regex[i + 1] == '*'): newregex += '[' + negation + '\*]'
            elif (regex[i + 1] == '+'): newregex += '[' + negation + '\+]'
            elif (regex[i + 1] == '('): newregex += '[' + negation + '\(]'
            elif (regex[i + 1] == ')'): newregex += '[' + negation + '\)]'
            elif (regex[i + 1] == '%'): newregex += '[' + negation + '\%]'
            else:
                callerror("Bad regex", 4)

            tmpregex.append(newregex)
            newregex = ""
            negation = ""
            specialoperator = True

        elif len(regex[i]) <= 32:
            tmpregex.append('[' + negation + regex[i] + ']')
            negation = ""
        else:
            callerror("Bad regex", 4)

    newregex = ""

    for i in range(len(tmpregex)):
        if tmpregex[i] in quantifier:
            if tmpregex[i] == ")":
                if tmpregex[i - 1] == "(":
                    callerror("Bad format of format file!", 4)
                else:
                    continue
            if len(tmpregex) - 1 == i and tmpregex[i] == "*": break
            if len(tmpregex) - 1 == i and tmpregex[i] == "+": break
            if len(tmpregex) - 1 == i: callerror("Bad format of format file!", 4)

            if checkquantifier["."]:
                checkquantifier["."] = False
                if tmpregex[i] in validation: callerror("Bad format of format file! [\".\"]", 4)
            elif checkquantifier["|"]:
                checkquantifier["|"] = False
                if tmpregex[i] in validation: callerror("Bad format of format file! [\"|\"]", 4)
            elif checkquantifier["*"]:
                if tmpregex[i] == "*" or tmpregex[i] == "+": tmpregex[i] = ""
            elif checkquantifier["+"]:
                if tmpregex[i] == "*" or tmpregex[i] == "+": tmpregex[i - 1] = ""
            elif checkquantifier["("]:
                checkquantifier["("] = False
                if tmpregex[i] in validation: callerror("Bad format of format file! [\"(\"]", 4)

            if tmpregex[i] == "." and not checkquantifier["."]:
                if tmpregex[i + 1] not in validation: tmpregex[i] = ""
                checkquantifier["."] = True
            elif tmpregex[i] == "|" and not checkquantifier["|"]:
                checkquantifier["|"] = True
            elif tmpregex[i] == "*" and not checkquantifier["*"]:
                checkquantifier["*"] = True
            elif tmpregex[i] == "+" and not checkquantifier["+"]:
                checkquantifier["+"] = True
            elif tmpregex[i] == "(" and not checkquantifier["("]:
                checkquantifier["("] = True
        else:
            checkquantifier = {".": False, "|": False, "*": False, "+": False, "(": False}

    newregex = newregex.join(tmpregex)

    try:
        re.compile(newregex)
        re.match(newregex, "")
    except:
        callerror("Bad combination of regex", 4)

    return newregex

# hledani pozic tagu
def generatetagposition(formatargs):
    global string
    if arg['inputfile'] == sys.stdin :
        string = sys.stdin.read()
    else:
        string = arg['inputfile'].read()
        arg['inputfile'].seek(0)

    matches = [""] * (len(string) + 1)

    for index in range(len(formatargs)):
        for match in re.finditer(formatargs[index][0], string, re.DOTALL):
            if match.start() == match.end(): continue
            matches[match.start()] = matches[match.start()] + formatargs[index][1]
            matches[match.end()] = formatargs[index][2] + matches[match.end()]
    return matches

# generovani vysledneho souboru
def generateoutput(tag):
    global string
    if arg['emptyformat']:
        if arg['inputfile'] == sys.stdin:
            string = sys.stdin.read()
        else:
            string = arg['inputfile'].read()
            arg['inputfile'].seek(0)

        if arg['br']:
            string = string.replace("\n", "<br />\n")

        arg['outputfile'].write(string)
        exit(0)

    result = ""
    for i in range(len(string)):
        result = result + tag[i] + string[i]

    # vložit posledni tag v pripade preteceni pres char
    result = result + tag[len(tag) - 1]

    if arg['br']:
        result = result.replace("\n", "<br />\n")

    arg['outputfile'].write(result)
    pass


def main():

    arguments()
    # debug()

    if arg['help']: helptext()

    # Loading format file
    formatargs = formatlist()

    # Loading input file
    matches = generatetagposition(formatargs)

    # Generate output
    generateoutput(matches)

    exit(0)


if __name__ == "__main__":
    main()
