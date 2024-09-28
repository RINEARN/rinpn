# Step 6 - How to Embed into Other Software

&raquo; [Japanese](Step6_Japanese.md)

&raquo; [Ask the AI for help (ChatGPT account required)](https://chatgpt.com/g/g-Hu225rEdv-rinpn-assistant)

You can integrate the scripting and calculation engine used in RINPn, known as the "[Vnano Engine](https://www.vcssl.org/en-us/vnano/)," into your own Java™ applications. Let's get started!

## Example Code

Here is an example code that calculates the value of the expression "1.2 + 3.4" using settings similar to those in RINPn:

    import org.vcssl.nano.VnanoEngine;
    import org.vcssl.nano.VnanoException;
    import org.vcssl.nano.interconnect.ScriptLoader;
    import org.vcssl.nano.interconnect.PluginLoader;
    import java.util.Map;
    import java.util.HashMap;

    public class EmbedUseExample {
        public static void main(String[] args) throws VnanoException {

            ////////// Preparation of Vnano Engine: Begin //////////

            // Initialize the Vnano Engine
            VnanoEngine engine = new VnanoEngine();

            // Configure engine options (for details, see Settings.txt)
            Map<String, Object> optionMap = new HashMap<String, Object>();
            optionMap.put("EVAL_INT_LITERAL_AS_FLOAT", true);
            optionMap.put("EVAL_ONLY_FLOAT", true);
            optionMap.put("EVAL_ONLY_EXPRESSION", true);
            optionMap.put("UI_MODE", "CUI");      // For CUI applications
            // optionMap.put("UI_MODE", "GUI"); // For GUI applications
            optionMap.put("ACCELERATOR_ENABLED", true);
            optionMap.put("ACCELERATOR_OPTIMIZATION_LEVEL", 0);
            engine.setOptionMap(optionMap);

            // Set permissions for plugins that support permission features
            Map<String, String> permissionMap = new HashMap<String, String>();
            permissionMap.put("DEFAULT", "ASK");   // For asking to users by default
            // permissionMap.put("DEFAULT", "DENY"); // For denying by default
            // (Here add permission items you want to allow, as follows)
            // permissionMap.put("FILE_READ", "ALLOW");
            engine.setPermissionMap(permissionMap);

            // Load plugins
            PluginLoader pluginLoader = new PluginLoader("UTF-8");
            pluginLoader.setPluginListPath("./plugin/VnanoPluginList.txt");
            pluginLoader.load();
            for (Object plugin: pluginLoader.getPluginInstances()) {
                engine.connectPlugin("___VNANO_AUTO_KEY", plugin);
            }

            // Load library scripts
            ScriptLoader scriptLoader = new ScriptLoader("UTF-8");
            scriptLoader.setLibraryScriptListPath("./lib/VnanoLibraryList.txt");
            scriptLoader.load();
            String[] libPaths = scriptLoader.getLibraryScriptPaths(true);
            String[] libScripts = scriptLoader.getLibraryScriptContents();
            int libCount = libScripts.length;
            for (int ilib=0; ilib<libCount; ilib++) {
                engine.registerLibraryScript(libPaths[ilib], libScripts[ilib]);
            }

            ////////// Preparation of Vnano Engine: End //////////

            // Calculate and output the result
            String input = "1.2 + 3.4";
            double x = (double) engine.executeScript(input + ";");
            System.out.println("Output of the Vnano Engine: " + x);

            // Unload resources
            engine.unregisterAllLibraryScripts();
            engine.disconnectAllPlugins();
        }
    }

## How to Compile and Execute

To compile the example code, add the Vnano Engine's JAR file, "**Vnano.jar**" (included in the RINPn package), to the classpath:

    # For Microsoft Windows
    javac -cp ".;Vnano.jar" -encoding UTF-8 EmbedUseExample.java

    # For Linux and other systems
    javac -cp ".:Vnano.jar" -encoding UTF-8 EmbedUseExample.java

Once compiled successfully, the "EmbedUseExample.class" file will be generated. Execute it with the following command:

    # For Microsoft Windows
    java -cp ".;Vnano.jar" EmbedUseExample    

    # For Linux and other systems
    java -cp ".:Vnano.jar" EmbedUseExample    

The result will display:

    Output of the Vnano Engine: 4.6

This confirms the correct calculation of the expression "1.2 + 3.4", which results in "4.6". Within the expression, you can utilize functions from libraries or plugins. Additionally, instead of just an expression, you can pass Vnano script content to the "executeScript(...)" method of the script engine.

## License and Detailed Information about the Vnano Engine

The Vnano Engine is open source software released under the MIT License, allowing free use for both commercial and non-commercial purposes, similar to RINPn.

For more detailed information, including guides and specification documents, visit the official Vnano website:

* [Vnano Official Website](https://www.vcssl.org/en-us/vnano/)

---

## Credits and Trademarks

* Oracle and Java are registered trademarks of Oracle and/or its affiliates.

* Microsoft Windows is either a registered trademarks or trademarks of Microsoft Corporation in the United States and/or other countries.

* Linux is a trademark of linus torvalds in the United States and/or other countries.

* ChatGPT is a trademark or a registered trademark of OpenAI OpCo, LLC in the United States and other countries.

* Other names may be either a registered trademarks or trademarks of their respective owners.

