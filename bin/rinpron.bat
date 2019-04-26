@echo off
setlocal

if "%2" == "" (
	rem
) else (
	echo Error:    Too many arguments.
	echo Solution: Enclose the expression by double-quotations, e.g.  "1+2".
	exit /b
)

cd /d %~dp0
cd ..

java -jar RinearnProcessorNano.jar %1


