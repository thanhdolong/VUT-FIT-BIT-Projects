## [IOS] Directory analyzer

The aim of project is to search through a directory and print a small report, 
including number of files, directory depth, list of file types and size of files.

### Parameters
```sh
$ ./dirstat.sh [-i FILE_ERE] [DIR]
```

`FILE_ERE` - regular expression for skipping files (cannot cover the name of root folder)

`DIR` - directory to search in

### Report Format
```sh
Root directory: DIR
Directories: ND
Max depth: DD
Average no. of files: AF
All files: NF
  Largest file: LF
  Average file size: AS
  File size median: MS
File extensions: EL
Files .EXT: NEXT
  Largest file .EXT: LEXT
  Average file size .EXT: AEXT
  File size median .EXT: MEXT
```
For more info, check [zadani.pdf](ios/ios-proj1/zadani.pdf).

### Run Example
```sh
$ ./dirstat.sh /etc 
Root directory: /etc 
Directories: 405 
Max depth: 4 
Average no. of files: 5 
All files: 2258
  Largest file: 6961203 
  Average file size: 12743 
  File size median: 634 
File extensions: cnf,conf,desktop,repo,sh 
Files .cnf: 3 
  Largest file .cnf: 10923 
  Average file size .cnf: 3833 
  File size median .cnf: 345 
Files .conf: 400 
  Largest file .conf: 82209 
  Average file size .conf: 1888 
  File size median .conf: 619 
Files .desktop: 26 
  Largest file .desktop: 11420 
  Average file size .desktop: 3683 
  File size median .desktop: 4137
Files .repo: 14 
  Largest file .repo: 1328 
  Average file size .repo: 1037 
  File size median .repo: 1217 
Files .sh: 34 
  Largest file .sh: 2703 
  Average file size .sh: 642 
  File size median .sh: 388
```