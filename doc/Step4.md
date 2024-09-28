# Step 4 - How to Add Functions/Variables by Scripts (Library Scripts)

&raquo; [Japanese](Step4_Japanese.md)

&raquo; [Ask the AI for help (ChatGPT account required)](https://chatgpt.com/g/g-Hu225rEdv-rinpn-assistant)

You can define variables and functions in script files within the "lib" folder and use them in expressions from Step-1 and Step-2, as well as in scripts from Step-3. These scripts are referred to as **"library scripts"** in both RINPn and Vnano.

## How to Use Library Scripts

An example library script, "ExampleLibrary.vnano," is located in the "lib" folder and is set to be loaded by default. Here is the content of this script:

    (Content of "ExampleLibrary.vnano" in the "lib" folder)

    float libvar = 2.0;

    float libfun(float x) {
        float result = libvar * x + 1;
        return result;
    }

Important Note: In Vnano, the precision of the "float" type is 64-bit, the same as "double."

In the expressions or scripts entered into RINPn (as outlined in [Step-1](Step1.md), [Step-2](Step2.md), and [Step-3](Step3.md)), you can use the variables and functions defined in "ExampleLibrary.vnano" as shown below:

    INPUT:
    1 + libvar

    OUTPUT:
    3

    INPUT:
    libfun(1.23)

    OUTPUT:
    3.46

## How to Add New Library Scripts

If desired, you can create additional library script files and define new functions and variables within them.

When you create or add new library script files, include their file paths in "VnanoLibraryList.txt" within the "lib" folder to ensure they are loaded.

---

## Credits and Trademarks

* ChatGPT is a trademark or a registered trademark of OpenAI OpCo, LLC in the United States and other countries.

* Other names may be either a registered trademarks or trademarks of their respective owners.
