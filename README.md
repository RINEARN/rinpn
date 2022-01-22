# RINPn

![Concept Image](./img/signboard.jpg)

RINPn (RINEARN Processor nano) is a free scientific calculator software, having very simple window. The RINPn is available in both GUI (window) and command-lines, and various math functions are available by default. In addition, you can define new functions, and you can also write/execute scripts for taking complex calculations.

&raquo; [Japanese README](./README_JAPANESE.md)

**Note: This README is for users who want to build this software from source code by yourself.
You can also get prebuilt-packages of this software from the following official website.**

### The RINPn Official Website

- English: [https://www.rinearn.com/en-us/rinpn/](https://www.rinearn.com/en-us/rinpn/)</a>
- Japanese:   [https://www.rinearn.com/ja-jp/rinpn/](https://www.rinearn.com/ja-jp/rinpn/)</a>

<hr />



## Index - 目次
- <a href="#version-note">Note</a>
- <a href="#license">License</a>
- <a href="#how-to-build">How to Build</a>
- <a href="#how-to-use">How to Use</a>
- <a href="#architecture">Software Architecture</a>
- <a href="#about-us">About Us</a>



<a id="version-note"></a>
## Note

The RINPn has not officially released yet. 
The current version of the RINPn is a &quot;open beta&quot;.


<a id="license"></a>
## License

This software is released under the MIT License.



<a id="how-to-build"></a>
## How to Build

### Requirements

1. Java&reg; Development Kit (JDK) 8 or later

1. Git


### For Microsoft&reg; Windows&reg; :

1. Clone repositories

		cd <working-directory>
		git clone https://github.com/RINEARN/vnano.git
		git clone https://github.com/RINEARN/vnano-standard-plugin.git
		git clone https://github.com/RINEARN/rinpn.git

1. Build & copy the Vnano Script Engine

		cd vnano
		.\build.bat
		(or:  ant -f build.xml )

		cd ..
		copy .\vnano\Vnano.jar .\rinpn\Vnano.jar

1. Build & copy the Vnano Standard Plug-ins

		cd vnano-standard-plugin
		.\build.bat
		
		cd ..
		xcopy /s .\vnano-standard-plugin\plugin\* .\rinpn\plugin\
		xcopy .\vnano-standard-plugin\*.html .\rinpn\

1. Build the RINPn

		cd rinpn
		.\build.bat
		(or:  ant -f build.xml )

		cd plugin
		javac -encoding UTF-8 ExamplePlugin.java
		cd ..

1. Check

		java -jar RINPn.jar --version
		> RINPn Ver.?.?.?  / with Vnano Ver.?.?.?  (?: numbers)


### For Other OS (Linux&reg;, etc.) :


1. Clone repositories

		cd <working-directory>
		git clone https://github.com/RINEARN/vnano.git
		git clone https://github.com/RINEARN/vnano-standard-plugin.git
		git clone https://github.com/RINEARN/rinpn.git

1. Build & copy the Vnano Script Engine

		cd vnano
		chmod +x ./build.sh          # if necessary: sudo ...
		./build.sh                   # or:  ant -f build.xml
		
		cd ..
		cp ./vnano/Vnano.jar ./rinpn/Vnano.jar

1. Build & copy Vnano Standard Plug-ins

		cd vnano-standard-plugin
		chmod +x ./build.sh          # if necessary: sudo ...
		./build.sh
		
		cd ..
		cp -r ./vnano-standard-plugin/plugin/* ./rinpn/plugin/
		cp ./vnano-standard-plugin/*.html ./rinpn/

1. Build the RINPn

		cd rinpn
		chmod +x ./build.sh          # if necessary: sudo ...
		./build.sh                   # or:  ant -f build.xml

		cd plugin
		javac -encoding UTF-8 ExamplePlugin.java
		cd ..

		cd bin
		chmod +x ./rinpn             # if necessary: sudo ...
		cd ..

1. Check

		java -jar RINPn.jar --version
		> RINPn Ver.?.?.?  / with Vnano Ver.?.?.?  (?: numbers)



<a id="how-to-use"></a>
## How to Use

On Microsoft&reg; Windows&reg;, double-click the batch file "RINPN_\*.\*.\*.bat" (\*: numbers) to execute the RINPn. On other OS (Linux&reg;, etc.), execute by the following command:

    java -jar RINPn.jar

Also, if the path of "bin" folder of the RINPn is registered to the environment variable Path/PATH, you can launch the RINPn by more simple command as follows:

    rinpn

When the RINPn is executed, the calculator window will be displayed:

![Calculator Window](./img/gui_expression.png)

On the above window, you can calculate the value of the inputted expression by typing the "Enter" key, or pressing "=" button. In the expression, various math functions such as sin, cos, and so on are available by default.
The window size, color, opacity, and so on are customizable by modifying values in "Settings.txt".

In addition, the RINPn support the scripting language ["Vnano"](https://www.vcssl.org/en-us/vnano/). You can define new functions in the Vnano script file "ExampleLibrary.vnano" in "lib" folder, and use them in the expression inputted to the RINPn.
You also can execute a Vnano script file performing complicated numerical calculations and so on, by inputting the name / path of the script file.


Also, if the path of "bin" folder of the RINPn is registered to the environment variable Path/PATH, you can calculate in the command-line as follows:

    rinpn "1 + 2"
	> 3

	rinpn "sin( PI / 2 ) + ( 3 / 2 )"
	> 2.5

	rinpn "Example.vnano"
	> 0.8414709848


For more details, see **"RINPn_User_Guide_English.html"** which is attached in this repository. Also, you can see the same guide on the web: 
[https://www.rinearn.com/en-us/rinpn/guide/](https://www.rinearn.com/en-us/rinpn/guide/)</a>


<a id="architecture"></a>
## Software Architecture

The architecture of source code of the RINPn adopts the MVP pattern which consists mainly of 3 core components: Model, View, and Presenter.
Each component is packed as a package.
Also, the component performing numerical operations and script processings is implemented as ["Vnano Engine"](https://www.vcssl.org/en-us/vnano/) (Vnano.jar) independent of components of the RINPn.


For details, see ["Architecture.md"](./Architecture.md) in this repository.
In that document, we explain the internal architecture of this software, which might help you to grasp the global structure of the implementation of this software, before reading source code.


<a id="about-us"></a>
## About Us

The RINPn is developed by a Japanese software development studio: [RINEARN](https://www.rinearn.com/). The author is Fumihiro Matsui.

Please free to contact us if you have any questions, feedbacks, and so on.


---

## Credits

- Oracle and Java are registered trademarks of Oracle and/or its affiliates. 

- Microsoft Windows is either a registered trademarks or trademarks of Microsoft Corporation in the United States and/or other countries. 

- Linux is a trademark of linus torvalds in the United States and/or other countries. 

- Other names may be either a registered trademarks or trademarks of their respective owners. 


