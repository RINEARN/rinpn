# Step 2 - Using RINPn in CUI Mode (Command-Line Terminal)

&raquo; [Japanese](Step2_Japanese.md)

&raquo; [Ask the AI for help (ChatGPT account required)](https://chatgpt.com/g/g-Hu225rEdv-rinpn-assistant)

In this section, we will explore how to use RINPn in CUI mode. In CUI mode, calculations are performed directly in the command-line terminal, without launching the calculator window.

## How to Calculate in CUI Mode Without Any Settings

To use CUI mode without additional settings, execute "RINPn.jar" by passing an expression as a command-line argument as follows:

    cd (The folder of the RINPn)

    java -jar RINPn.jar   "(1 + 2 ) / 3 - 4 + 5"
    (Result) 2

    java -jar RINPn.jar   "sqrt ( sin( PI / 2 ) + 1 )"
    (Result) 1.414213562

As shown, you can perform the same calculations as in GUI mode directly from the command-line.

## How to Calculate in CUI Mode With Path Settings

Entering the full command-line and setting the current directory to the RINPn folder each time can be cumbersome. To simplify this process, it is useful to **add the "cmd" folder of RINPn to your OS's "PATH" (or "Path") environment variable.** Once done, you can perform calculations more simply using the "rinpn" command, regardless of the current directory:

    rinpn "(1 + 2 ) / 3 - 4 + 5"
    (Result) 2

    rinpn "sqrt ( sin( PI / 2 ) + 1 )"
    (Result) 1.414213562

Modifying the PATH environment variable depends on your operating system. For detailed instructions, search online with keywords like **"how to set path (+ your OS name)." Be cautious when modifying the PATH as incorrect changes can cause serious problems on your PC. If you are unfamiliar with these settings and can seek assistance, it is advisable to do so.**

### How to Register the Path on Microsoft&reg; Windows&reg; 10 and 11

After placing the RINPn folder in a fixed location on your PC, follow the steps below to register the path:

* Press the Start button
* Select the gear icon (Settings)
* Search for "Edit the environment variables" and move to that screen
* In the opened window, select "Path" from the list of "User environment variables" (create it if it doesn't exist) and press the "Edit" button
* In the new window, press the "New" button and enter the path to the "cmd" folder in the RINPn directory. After that, close the windows by pressing "OK", and the process is complete.

Tip: You can copy the path by right-clicking the "cmd" folder while holding down the Shift key and selecting "Copy as path" from the menu.

Please note that if you move the RINPn folder to another location, you will need to update the registered path accordingly.

### How to Register the Path on Linux&reg; and Other Systems

First, place the RINPn folder in a fixed location on your PC. For example, let's assume it's placed in the following directory:

    /usr/local/bin/rinpn/rinpn_?_?_?/
    (The _?_?_? part represents the version numbers.)

Next, use the cd command to navigate to the "cmd" directory inside the above directory, and grant execute permission to the "rinpn" file (no extension) with the following command:

    chmod +x rinpn
    (If you encounter a permission error, use sudo chmod +x rinpn.)

Finally, open one of the following files in your home directory: ".bashrc" (hidden file), ".bash_profile", or ".profile" (the file that is valid depends on your OS or distribution). Then, add the following line at the end of the file:

    export PATH=$PATH:/usr/local/bin/rinpn/rinpn_?_?_?/cmd/
    (Replace _?_?_? with the version number.)

The above applies if your shell is bash. If you're using Ubuntu® or a system with dash as the default shell, surround the right-hand side with double quotes:

    export PATH="$PATH:/usr/local/bin/rinpn/rinpn_?_?_?/cmd/"
    (Replace _?_?_? with the version number.)

Note that the syntax may vary slightly depending on the shell. Please search for the details according to your environment.

That's it!


---

## Credits and Trademarks

* Oracle and Java are registered trademarks of Oracle and/or its affiliates.

* Microsoft Windows is either a registered trademarks or trademarks of Microsoft Corporation in the United States and/or other countries.

* Linux is a trademark of linus torvalds in the United States and/or other countries.

* ChatGPT is a trademark or a registered trademark of OpenAI OpCo, LLC in the United States and other countries.

* Other names may be either a registered trademarks or trademarks of their respective owners.



