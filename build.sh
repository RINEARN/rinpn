#!/bin/sh

# ==================================================
# A shell script for building the RINPn
# ==================================================

mkdir bin


# --------------------------------------------------
# compile source files of the Vnano Engine
# --------------------------------------------------

cd src
javac @org/vcssl/nano/sourcelist.txt -d ../bin -encoding UTF-8
cd ..

# --------------------------------------------------
# create a JAR file of the Vnano Engine (Vnano.jar)
# --------------------------------------------------

jar cvfm Vnano.jar src/org/vcssl/nano/meta/main.mf -C bin org -C src/org/vcssl/nano/meta META-INF


# --------------------------------------------------
# compile source files of the RINPn
# --------------------------------------------------

cd src
javac @com/rinearn/processornano/sourcelist.txt -d ../bin -encoding UTF-8
cd ..

# --------------------------------------------------
# create a JAR file of the RINPn (RINPn.jar)
# --------------------------------------------------

jar cvfm RINPn.jar src/com/rinearn/processornano/meta/main.mf -C bin com


# --------------------------------------------------
# create plug-ins
# --------------------------------------------------

cd plugin
javac -encoding UTF-8 @org/vcssl/connect/sourcelist.txt
javac -encoding UTF-8 @org/vcssl/nano/plugin/sourcelist.txt
javac -encoding UTF-8 ExamplePlugin.java
cd ..

