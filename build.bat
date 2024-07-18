:: ==================================================
:: A batch file for building the RINPn
:: ==================================================

mkdir bin
mkdir bin_vnano


:: --------------------------------------------------
:: compile source files of the Vnano Engine
:: --------------------------------------------------

cd src_vnano
javac -d ../bin_vnano -encoding UTF-8 @org/vcssl/connect/sourcelist.txt
javac -d ../bin_vnano -encoding UTF-8 @org/vcssl/nano/sourcelist.txt
cd ..

:: --------------------------------------------------
:: create a JAR file of the Vnano Engine (Vnano.jar)
:: --------------------------------------------------

jar cvfm Vnano.jar src_vnano/org/vcssl/nano/meta/main.mf -C bin_vnano org -C src_vnano/org/vcssl/nano/meta META-INF


:: --------------------------------------------------
:: compile source files of the RINPn
:: --------------------------------------------------

cd src
javac -Xlint:all -cp .;../Vnano.jar -d ../bin -encoding UTF-8 @com/rinearn/rinpn/sourcelist.txt 
cd ..

:: --------------------------------------------------
:: create the JAR file of the RINPn (RINPn.jar)
:: --------------------------------------------------

jar cvfm RINPn.jar src/com/rinearn/rinpn/meta/main.mf -C bin com


:: --------------------------------------------------
:: compile plug-ins
:: --------------------------------------------------

cd plugin
javac -encoding UTF-8 @org/vcssl/connect/sourcelist.txt
javac -encoding UTF-8 @org/vcssl/nano/plugin/sourcelist.txt
javac -encoding UTF-8 ExamplePlugin.java
cd ..

