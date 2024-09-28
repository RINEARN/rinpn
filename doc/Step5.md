# Step 5 - Step 5 - Implement Functions/Variables in Java&trade; (Plug-in Development)

&raquo; [Japanese](Step5_Japanese.md)

&raquo; [Ask the AI for help (ChatGPT account required)](https://chatgpt.com/g/g-Hu225rEdv-rinpn-assistant)

You can implement new built-in variables and functions using the Java&trade; programming language. Within RINPn and Vnano, we refer to Java&trade; programs that provide built-in functions or variables as "**plugins**."

Utilizing Java offers advanced functionality compared to defining variables/functions in script code (as in [step-4](Step4.md)).

However, implementing code in Java requires the Java Development Kit (JDK) to compile the code. We assume that JDK is installed on your PC and that the "javac" command is available. For installation details, you can search online with keywords like "OpenJDK install (your OS name)".

## How to Use Plugins

To use plugins with RINPn, place them in the "plugin" folder of RINPn. A simple example of a plugin, "ExamplePlugin.java," is located in this folder:

    (Content of "ExamplePlugin.java" in the "plugin" folder)

    public class ExamplePlugin {

        public double pivar = 1.0;

        public double pifun(double arg) {
            return arg * 2.0;
        }
    }

This plugin is compiled by default and set to be loaded (details on setting this up will be explained later).

The variable "pivar" and the method "pifun(arg)" can be used as built-in variables/functions in the calculator (see [Step-1](Step1.md) and [2](Step2.md)) and in script code (see [Steps-3](Step3.md) and [4](Step4.md)).

For example:

    INPUT:
    1 + pivar

    OUTPUT:
    2

    INPUT:
    pifun(2)

    OUTPUT:
    4

## How to Compile Plugins

When you modify a plugin's code, it needs to be recompiled as follows:

    cd (directory of RINPn)
    javac ExamplePlugin.vnano

Some compiled class files of special plugin interfaces (XFCI1, XNCI1, etc.) supported by the Vnano Engine are included in the "plugin/org/vcssl/connect" folder. You can import these interfaces in your plugin and compile them similarly.

## How to Add New Plugins

To add a new plugin, place it in the "plugin" folder and compile it. Then, add the path of the compiled class file to the "VnanoPluginList.txt" file within the "plugin" folder. All plugins listed in "VnanoPluginList.txt" will be loaded by RINPn.

## Vnano Standard Plugins

In the "plugin/org/vcssl/nano/plugin" folder, "Vnano Standard Plugins," which provide basic features like math and utility functions, are bundled and loaded by default (as specified in "VnanoPluginList.txt"). You can use their functions and variables by default.

For a comprehensive list and detailed specifications of Vnano Standard Plugins, visit the "[Vnano Standard Plugins](https://www.vcssl.org/en-us/vnano/plugin/)" page on the official Vnano website.

Additionally, the RINPn frequently uses functions/variables, and simplified explanations of these are listed in the section: [Appendix - List of Built-in Functions/Variables](Appendix.md).



---

## Credits and Trademarks

* Oracle and Java are registered trademarks of Oracle and/or its affiliates.

* ChatGPT is a trademark or a registered trademark of OpenAI OpCo, LLC in the United States and other countries.

* Other names may be either a registered trademarks or trademarks of their respective owners.


