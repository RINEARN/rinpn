# RINPn

&raquo; [Japanese README](./README_JAPANESE.md)

&raquo; [Ask the AI for help (ChatGPT account required)](https://chatgpt.com/g/g-Hu225rEdv-rinpn-assistant)

![Concept Image](./img/signboard.jpg)

RINPn is a powerful scientific calculator software featuring a straightforward interface. It is available in both GUI (graphical user interface) and command-line versions, and comes equipped with a variety of mathematical functions. Additionally, you can define new functions and execute scripts to perform complex calculations.

**Note: This README is intended for users who wish to build the software from source. Prebuilt packages of this software can also be obtained from the following official website.**

### RINPn Official Website

- English: [https://www.rinearn.com/en-us/rinpn/](https://www.rinearn.com/en-us/rinpn/)</a>
- Japanese:   [https://www.rinearn.com/ja-jp/rinpn/](https://www.rinearn.com/ja-jp/rinpn/)</a>

<hr />



## Table of Contents
- <a href="#license">License</a>
- <a href="#how-to-build">How to Build</a>
- <a href="#how-to-use">How to Use</a>
- <a href="#architecture">Software Architecture</a>
- <a href="#about-us">About Us</a>



<a id="license"></a>
## License

This software is distributed under the MIT License.



<a id="how-to-build"></a>
## How to Build

### Requirements

1. Java&trade; Development Kit (Version 19 or later)

1. Git


### Building Steps

1. Clone the repository:

		cd <working-directory>
		git clone https://github.com/RINEARN/rinpn.git
		cd rinpn

1. Build the software:


		.\build.bat      # For Microsoft Windows
		./build.sh       # For Linux, etc.

		(or, if you are using Apache Ant:  ant -f build.xml )

1. Test the build:

		java -jar RINPn.jar --version
		> RINPn Ver.?.?.?  / with Vnano Ver.?.?.?  (?: numbers)



<a id="how-to-use"></a>
## How to Use

On Microsoft&reg; Windows&reg;, **double-click the batch file "RINPn_\*.\*.\*.bat" (\*: version numbers) to launch RINPn.**

For other operating systems (Linux&reg;, etc.), use the following command:

    java -jar RINPn.jar

If the "cmd" folder within the RINPn directory is added to your Path/PATH environment variable, you can start RINPn with this simpler command:

    rinpn

Upon launching, the calculator interface will appear as shown below:

![Calculator Window](./img/gui_retractable_en_us.png)

You can toggle the visibility of the key panel by clicking the "▲KEY-PANEL" switch, for a more compact or expanded view. Customize the window's color, opacity, font size, and more by editing the "Settings.txt" file.

To calculate the value of an entered expression, press the "Enter" key on your computer's keyboard or the "=" button on the RINPn interface. To clear the input, use the "Esc" key or the "C" button.

The calculator supports various mathematical functions like sin, cos, etc., by default. You can also define your own functions.

Furthermore, you can execute scripts written in a C-like simple language "[Vnano](https://www.vcssl.org/ja-jp/vnano/)" by entering the script’s file name or path or by selecting a script file through the "Script" button. These scripts are particularly useful for automating complex calculations.



Also, if the "cmd" folder within the RINPn directory is added to your Path/PATH environment variable, you can perform calculations directly from the command line. For example:

    rinpn "1 + 2"
	> 3

	rinpn "sin( PI / 2 ) + ( 3 / 2 )"
	> 2.5

	rinpn "Example.vnano"
	> 0.8414709848

For more detailed instructions, please refer to [the documents in the doc folder](doc/README.md). Additionally, a user guide with almost the same content, formatted in an easy-to-read HTML version, is available on the official RINPn website:

* [RINPn User Guide](https://www.rinearn.com/en-us/rinpn/guide/)</a>

As highlighted in the user guide (and briefly in this README), RINPn offers numerous features, including:

* Defining custom functions and variables
* Executing scripts written in a C-like simple language "[Vnano](https://www.vcssl.org/en-us/vnano/doc/tutorial/language)"
* Invoking processes implemented in Java

among others. Additionally, all built-in functions and variables provided by the Vnano Standard Plug-ins are available by default and listed on the following page:

* [List of built-in functions and variables provided by Vnano Standard Plug-ins](https://www.vcssl.org/en-us/vnano/plugin/)</a>

RINPn is an extremely versatile calculator app; hence, this short README cannot cover all its features. We encourage you to start using RINPn and consult the above documents as needed.

Also, RINPn has an official guide AI, allowing you to ask questions about how to use it:

&raquo; [Ask the AI for help (ChatGPT account required)](https://chatgpt.com/g/g-Hu225rEdv-rinpn-assistant)



<a id="architecture"></a>
## Software Architecture

The software architecture of RINPn follows the "MVP (Model-View-Presenter) pattern" and is primarily composed of three core components: Model, View, and Presenter. Each component is implemented as a class within the [com.rinearn.rinpn](https://github.com/RINEARN/rinpn/blob/main/src/com/rinearn/rinpn/) package. Additionally, while it operates independently of RINPn's implementation, [the Vnano script engine](https://github.com/RINEARN/vnano) is embedded to handle calculations and scripting.


Below is a block diagram that illustrates the relationships between the aforementioned components:

![Block Diagram](./img/architecture.jpg)

As shown in the diagram, the [RINPn](https://github.com/RINEARN/rinpn/blob/main/src/com/rinearn/rinpn/RINPn.java) class acts as the surface layer of the software, where the Model, View, and Presenter components are integrated and function collaboratively. The roles of each component will be discussed further in the following sections.



<a id="architecture-model"></a>
### Model ( [com.rinearn.rinpn.Model](https://github.com/RINEARN/rinpn/blob/main/src/com/rinearn/rinpn/Model.java) class )

The Model component handles the functional aspects of the calculator, independent of the user interface. It receives a calculation expression as input and outputs the result, which is processed using the script engine.

In the command-line interface (CUI) mode, the RINPn class directly invokes the calculation process of the Model on the main thread. In contrast, in the graphical user interface (GUI) mode, the calculation process is initiated by the Presenter as part of an event-driven process.


<a id="architecture-view"></a>
### View ( [com.rinearn.rinpn.View](https://github.com/RINEARN/rinpn/blob/main/src/com/rinearn/rinpn/View.java) class )

The View component serves as the graphical interface of the application, consisting of a window, text fields, and other UI elements.

It is important to note that the View does not handle events triggered by user interactions with the UI components. These events are managed by the Presenter, not the View. The primary role of the View is simply to display and provide access to the UI components.



<a id="architecture-presenter"></a>
### Presenter ( [com.rinearn.rinpn.Presenter](https://github.com/RINEARN/rinpn/blob/main/src/com/rinearn/rinpn/Presenter.java) class )

The Presenter acts as a mediator between the Model and the View.

It includes various event listeners as inner classes. When a user interacts with a UI component of the View, the corresponding event listener in the Presenter is triggered. This listener then initiates the Model's calculation process, and updates the View with the result to display it.


<a id="architecture-engine"></a>
### Script Engine ( [org.vcssl.nano](https://github.com/RINEARN/vnano/blob/main/src/org/vcssl/nano/) package )

This script engine, known as "Vnano," is responsible for performing calculations requested by the Model, executing scripts, and managing communications with plug-ins. It is independently developed for embedded use in general applications. For more information about this engine, please visit the Vnano documentation at: [https://github.com/RINEARN/vnano](https://github.com/RINEARN/vnano)




<a id="about-us"></a>
## About Us

The RINPn is developed by a Japanese software development studio: [RINEARN](https://www.rinearn.com/). The author is Fumihiro Matsui.

Please feel free to contact us if you have any questions, feedback, or other comments.


---

## Credits

- Oracle and Java are registered trademarks of Oracle and/or its affiliates. 

- Microsoft Windows is either a registered trademarks or trademarks of Microsoft Corporation in the United States and/or other countries. 

- Linux is a trademark of linus torvalds in the United States and/or other countries. 

* ChatGPT is a trademark or a registered trademark of OpenAI OpCo, LLC in the United States and other countries.

- Other names may be either a registered trademarks or trademarks of their respective owners. 


