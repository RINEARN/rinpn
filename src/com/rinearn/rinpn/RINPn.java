/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn;

import org.vcssl.nano.VnanoException;
import org.vcssl.nano.VnanoFatalException;

import com.rinearn.rinpn.util.LocaleCode;
import com.rinearn.rinpn.util.MessageManager;
import com.rinearn.rinpn.util.SettingContainer;

import java.io.File;
import java.lang.reflect.InvocationTargetException;


/**
 * The main class of RINPn.
 */
public final class RINPn {

	private static final String VERSION = "0.9.6";
	private static final String OPTION_NAME_VERSION = "--version";
	private static final String OPTION_NAME_DEBUG = "--debug";
	private static final String OPTION_NAME_DIR = "--dir";
	private static final String LIBRARY_LIST_FILE = "lib/VnanoLibraryList.txt";
	private static final String PLUGIN_LIST_FILE = "plugin/VnanoPluginList.txt";

	/**
	 * Creates an new instance of RINPn.
	 */
	public RINPn() {
		// The window will not be launched automatically by this constructor
		// (because the window is not necessary for CUI mode).
		// For launching the window, call launchCalculatorWindow() explicitly.
	}


	/**
	 * The main function.
	 * 
	 * @param args The command-line arguments.
	 */
	public static void main(String[] args) {

		// Variables for storing inputted the contents.
		String inputtedContent = null;  // Stores the inputted expression or the scriot file path. 
		boolean debugEnabled = false;  // Stores whether --debug option is specified.
		String dirPath = ".";  // Stores the base directory of the relative path resolution.

		// Parse arguments.
		int argLength = args.length;
		int argIndex = 0;
		while (argIndex < argLength) {

			// If the arg is "--version", print the version.
			if (args[argIndex].equals(OPTION_NAME_VERSION)) {
				printVersion();
				return;

			// If the arg is "--debug", enable the debugging option.
			} else if (args[argIndex].equals(OPTION_NAME_DEBUG)) {
				debugEnabled = true;
				argIndex++;

			// If the arg is "--dir", regard the next value as the base directory of the relative path resolution.
			} else if (args[argIndex].equals(OPTION_NAME_DIR)) {
				if (argLength <= argIndex + 1) {
					// We want to determine the locale from the setting file but...
					if (LocaleCode.getDefaultLocaleCode().equals(LocaleCode.JA_JP)) {
						System.err.println("オプション「 " + OPTION_NAME_DIR + " 」の後に値が必要です。");
					}
					if (LocaleCode.getDefaultLocaleCode().equals(LocaleCode.EN_US)) {
						System.err.println("An option value is required after \"" + OPTION_NAME_DIR + "\".");
					}
				}
				dirPath = args[argIndex + 1];
				argIndex += 2;

			// Otherwise, regard the arg as an calculation expression or the path/name of an script file.
			} else {
				if (inputtedContent == null) {
					inputtedContent = args[argIndex];

				// Too many ars:
				} else {
					if (LocaleCode.getDefaultLocaleCode().equals(LocaleCode.JA_JP)) {
						System.err.println("コマンドライン引数の数が多すぎます。");
					}
					if (LocaleCode.getDefaultLocaleCode().equals(LocaleCode.EN_US)) {
						System.err.println("Too many command-line arguments.");
					}
				}
				argIndex++;
			}
		}

		// If no calculation expression or script file is specified, launch the GUI window.
		if (inputtedContent == null) {
			new RINPn().launchCalculatorWindow(dirPath, debugEnabled);

		// If any calculation expression or script file is specified, process it in CUI mode.
		// (The result will be printed to the standard output)
		} else {
			new RINPn().calculate(inputtedContent, dirPath, debugEnabled);
		}
	}


	/**
	 * Prints the versions of RINPN and Vnano to the standard output.
	 */
	private static void printVersion() {
		
		// Print the version of RINPn.
		System.out.print("RINPn Ver." + VERSION + " ");

		// Print the version of Vnano.
		System.out.print(" / with " + org.vcssl.nano.spec.EngineInformation.LANGUAGE_NAME);
		System.out.print(" Ver." + org.vcssl.nano.spec.EngineInformation.LANGUAGE_VERSION);
		System.out.println("");
	}


	/**
	 * Calculate an expression or execute a script, and print the result to the standard output.
	 *
	 * @param inputtedContent The calculation expression, or the name/path of the script file.
	 * @param dirPath The path of the base directory for relative path resolution.
	 * @param debug Specify true for printing information for debugging.
	 */
	public final void calculate(String inputtedContent, String dirPath, boolean debug) {

		// Set the mode of the message manager to "CUI", which prints messages to the standard output.
		MessageManager.setDisplayType(MessageManager.DisplayMode.CUI);

		// Create and initialize the container for storing settings,
		// and the model (which performs calculations) of this app.
		SettingContainer setting = null;
		Model model = null;
		try {
			setting = this.createInitializedSettingContainer(false, debug);
			model = this.createInitializedCalculatorModel(dirPath, false, setting);

		// An exception is thrown if any error occurred for loading settings/libraries.
		} catch (RINPnException e) {
			if (setting==null || setting.exceptionStackTracerEnabled) {
				String localeCode = (setting==null) ? LocaleCode.getDefaultLocaleCode() : setting.localeCode;
				MessageManager.showExceptionStackTrace(e, localeCode);
			}
			return;
		}

		// Calculate the expression or execute the script.
		String outputText = null;
		try {
			outputText = model.calculate(inputtedContent, false, setting);
			if (outputText != null) {
				System.out.println(outputText);
			}

		} catch (VnanoException | VnanoFatalException | RINPnFatalException e) {
			String message = MessageManager.customizeExceptionMessage(e.getMessage());
			MessageManager.showErrorMessage(message, "!", setting.localeCode);
			if (setting.exceptionStackTracerEnabled) {
				MessageManager.showExceptionStackTrace(e, setting.localeCode);
			}
		}

		// Shutdown the model of this app.
		model.shutdown(setting);
	}


	/**
	 * Launch the calculator window.
	 *
	 * @param debug Specify true for printing information for debugging.
	 */
	public final void launchCalculatorWindow(String dirPath, boolean debug) {

		// Create and initialize the container for storing settings,
		// and the model (which performs calculations) of this app.
		SettingContainer setting = null;
		Model calculator = null;
		try {
			setting = this.createInitializedSettingContainer(true, debug);
			calculator = this.createInitializedCalculatorModel(dirPath, true, setting);

		// An exception is thrown if any error occurred for loading settings/libraries.
		} catch (RINPnException e) {
			if (setting==null || setting.exceptionStackTracerEnabled) {
				String localeCode = (setting==null) ? LocaleCode.getDefaultLocaleCode() : setting.localeCode;
				MessageManager.showExceptionStackTrace(e, localeCode);
			}
			return;
		}


		// Create the view (window, GUI components, and so on) of this app;
		View view = new View();
		try {
			view.initialize(setting);

		// If the initialization process is interrupted, an exception occurs.
		// It is an irregular case, so exit this app if it has been occurred.
		} catch (InvocationTargetException | InterruptedException e) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(
					"Unexpected exception occurred: " + e.getClass().getCanonicalName(), "Error", setting.localeCode
				);
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(
					"予期しない例外が発生しました: " + e.getClass().getCanonicalName(), "エラー", setting.localeCode
				);
			}
			if (setting.exceptionStackTracerEnabled) {
				MessageManager.showExceptionStackTrace(e, setting.localeCode);
			}
			return;
		}

		// Create the presenter (which handles events) of this app, and link the model and the view by it.
		Presenter presenter = new Presenter();
		presenter.link(view, calculator, setting);
	}


	/**
	 * Creates the setting container storing values written in the setting file.
	 * 
	 * @param isGuiMode Specify true if this app is executed in GUI mode.
	 * @param debug Specify true if print information for debugging.
	 * @return The created setting container.
	 * @throws RINPnException
	 *      Thrown if an error occurs for loading / parsing the setting file.
	 */
	private final SettingContainer createInitializedSettingContainer(boolean isGuiMode, boolean debug)
			throws RINPnException {

		SettingContainer setting = new SettingContainer();

		// The setting file name may have either ".vnano" or ".txt" as the extension.
		String settingScriptPath = SettingContainer.SETTING_SCRIPT_PATH_VNANO;
		if (!new File(settingScriptPath).exists()) {
			settingScriptPath = SettingContainer.SETTING_SCRIPT_PATH_TXT;

			// If both files don't not exist: error.
			if (!new File(settingScriptPath).exists()) {
				String errorMessage = null;

				if (setting.localeCode.equals(LocaleCode.JA_JP)) {
					errorMessage =
						"設定ファイル「 "
						+ SettingContainer.SETTING_SCRIPT_PATH_TXT
						+ " 」または「 "
						+ SettingContainer.SETTING_SCRIPT_PATH_VNANO
						+ " 」が見つかりません。";
					MessageManager.showErrorMessage(errorMessage, "設定ファイル読み込みエラー", LocaleCode.JA_JP);
				} else {
					errorMessage =
						"The setting file \""
						+ SettingContainer.SETTING_SCRIPT_PATH_TXT
						+ "\" or \""
						+ SettingContainer.SETTING_SCRIPT_PATH_VNANO
						+ "\" is not found.";
					MessageManager.showErrorMessage(errorMessage, "Setting File Loading Error", LocaleCode.EN_US);
				}

				throw new RINPnException(errorMessage);
			}
		}

		// Execute the content of the setting file as a Vnano script,
		// for inputting setting values to fields of the setting container.
		setting.evaluateSettingScript(
			settingScriptPath, LIBRARY_LIST_FILE, PLUGIN_LIST_FILE, isGuiMode, debug
		);

		return setting;
	}


	/**
	 * Create the model of this app, and initialize it.
	 *
	 * @return The created model of this app.
	 * @throws RINPnException
	 *      Thrown if an error occurs for initializing the script engine, loading library scripts, and so on.
	 */
	private final Model createInitializedCalculatorModel(String dirPath, boolean isGuiMode, SettingContainer setting)
			throws RINPnException {

		Model model = new Model();
		model.initialize(setting, isGuiMode, dirPath, LIBRARY_LIST_FILE, PLUGIN_LIST_FILE);
		return model;
	}

}
