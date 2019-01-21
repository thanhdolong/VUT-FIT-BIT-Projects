CFLAGS=-std=c++11 -Wall -Wextra -pedantic
MAIN=trace.cpp

all:
	g++ -o trace $(MAIN) $(CFLAGS)

clean:
	rm -f trace

zip:
	tar -czvf xdolon00.tgz Makefile trace.cpp trace.h trace.1
