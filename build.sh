#!/bin/sh

# --------------------------------------------------
# compile source files
# --------------------------------------------------

mkdir bin
cd src
javac @com/rinearn/processornano/sourcelist.txt -d ../bin -encoding UTF-8
cd ..

# --------------------------------------------------
# create a JAR file
# --------------------------------------------------

jar cvfm RINPn.jar src/com/rinearn/processornano/meta/main.mf -C bin com
