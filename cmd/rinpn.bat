@echo off
setlocal

if "%2" == "" (
	rem
) else (
	echo Error:    Too many arguments.
	echo Solution: Enclose the expression by double-quotations, e.g.  "1+2".
	exit /b
)

set CALLER_DIR=%CD%

cd /d %~dp0
cd ..

java -jar RINPn.jar --dir "%CALLER_DIR%" %1

