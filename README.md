# JLouis by Michael Whapples: A set of java bindings for liblouis

## Introduction

JLouis is a set of bindings for the liblouis Braille translator. Liblouis
can be found at http://github.com/liblouis/liblouis.

JLouis makes use of JNA, so fits with the general idea of write once, run 
everywhere as it does not contain any C code. The advantage for those who use 
JLouis is that one jar file of JLouis will work on whatever platform you 
choose to use it on, regardless of what system was used to compile it.

## Licensing

JLouis is written by Michael Whapples and is released under the Apache 2.0
license. A copy of the apache 2.0 license should have been included with the
software, so for details of what is permitted please see the file LICENSE.

As the apache 2.0 license allows modified versions to be created and 
distributed, the original author of JLouis wishes to only be represented 
by the original JLouis distribution which can be found at http://bitbucket.org/mwhapples/jlouis/

## Building

JLouis is built using the gradle build tool. The gradle wrapper is included so you do not need gradle installed on your computer. Follow the below steps to build JLouis.

1. Download JLouis from http://bitbucket.org/mwhapples/jlouis
2. If you downloaded JLouis in an archive file, unpack it.
3. In the jlouis directory, run the command: gradlew build

Once you have done this you should find the jlouis jar file in the build directory.

## Problems when using/compiling

JLouis does not include a copy of liblouis, therefore you must have liblouis 
installed on your system. Even if you have liblouis installed, it may still 
be possible that JLouis still cannot find your liblouis installation. In this 
case, you can specify where the library file (eg. dll on windows, so file 
on linux, etc) is to be found by using the java system property 
jlouis.library.path. In this system property just specify the path, do not 
specify the library file itself. So if your liblouis.dll on windows could be found in c:\liblouis the build command would be something like:
gradlew -Djlouis.library.path=c:\jlouis build

Also be aware that the tests will require access to the liblouis tables of your liblouis installation. You may need to set the LOUIS_TABLEPATH environment variable to allow liblouis to find these.
