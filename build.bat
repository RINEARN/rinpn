:: --------------------------------------------------
:: compile source files
:: --------------------------------------------------

mkdir bin
cd src
javac @sourcelist.txt -d ../bin -encoding UTF-8
cd ..

:: --------------------------------------------------
:: create a JAR file
:: --------------------------------------------------

jar cvfm RINPn.jar src/main.mf -C bin com
