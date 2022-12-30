/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn;

import java.io.File;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

import org.vcssl.nano.VnanoEngine;
import org.vcssl.nano.VnanoException;
import org.vcssl.nano.VnanoFatalException;
import org.vcssl.nano.interconnect.PluginLoader;
import org.vcssl.nano.interconnect.ScriptLoader;

import com.rinearn.rinpn.util.LocaleCode;
import com.rinearn.rinpn.util.MessageManager;
import com.rinearn.rinpn.util.OutputValueFormatter;
import com.rinearn.rinpn.util.SettingContainer;


/**
 * The class providing internal core features of this application
 * (performing calculations, executing scripts, and so on).
 */
public final class Model {

	private static final String SCRIPT_EXTENSION = ".vnano";
	private static final String DEFAULT_SCRIPT_ENCODING = "UTF-8";
	private static final String DEFAULT_FILE_IO_ENCODING = "UTF-8";

	/** The scripting engine of Vnano. */
	private VnanoEngine engine = null;

	/** The base directory path of relative path resolution. */
	private String dirPath = ".";

	/** The path of the library list file. */
	private String libraryListFilePath = null;

	/** The path of the plugin list file. */
	private String pluginListFilePath = null;

	/** The flag representing whether any calculation or script is currently running. */
	private volatile boolean calculating = false;

	// The variable for storing a value passed to "output" function from a script,
	// for displaying it in GUI mode.
	private String lastOutputContent = null;


	/**
	 * The interface for receiving the notification 
	 * when the asynchronous calculation or script processing completes.
	 */
	public interface AsyncCalculationListener {
		public abstract void calculationFinished(String outputText);
	}


	/**
	 * The plug-in class providing "output" function for Vnano engine.
	 */
	public class OutputPlugin {

		private boolean isGuiMode;
		private SettingContainer setting;
		public OutputPlugin(SettingContainer setting, boolean isGuiMode) {
			this.setting = setting;
			this.isGuiMode = isGuiMode;
		}

		public void output(String value) {

			// In GUI mode, it will be displayed later.
			Model.this.lastOutputContent = value;

			// In CUI mode, it is printed immediately to the standard output.
			if (!this.isGuiMode) {
				System.out.println(value);
			}
		}

		public void output(long value) {
			this.output( Long.toString(value) );
		}

		public void output(double value) {

			// Round and format.
			if ( !((Double)value).isNaN() && !((Double)value).isInfinite() ) {
				BigDecimal roundedValue = OutputValueFormatter.round( ((Double)value).doubleValue(), this.setting);
				String simplifiedValue = OutputValueFormatter.simplify( roundedValue );
				this.output(simplifiedValue);

			} else {
				this.output( Double.toString(value) );
			}
		}
		public void output(boolean value) {
			this.output( Boolean.toString(value) );
		}
	}


	/**
	 * Returns whether any calculation or script is currently running.
	 * This method is called from AsyncCalculationRunner, ExitButtonListener, and so on.
	 * 
	 * @return Returns true if any calculation or script is currently running.
	 */
	public final boolean isCalculating() {
		return this.calculating;
	}


	/**
	 * Initializes this model instance.
	 * 
	 * @param setting The container storing setting values.
	 * @param isGuiMode Specify true if this app is running in GUI mode.
	 * @param dirPath The base directory path of relative path resolution.
	 * @param libraryListFilePath The path of the library list file.
	 * @param pluginListFilePath The path of the plug-in list file.
	 */
	public final void initialize(
			SettingContainer setting, boolean isGuiMode, String dirPath, String libraryListFilePath, String pluginListFilePath) {

		this.dirPath = dirPath;
		this.libraryListFilePath = libraryListFilePath;
		this.pluginListFilePath = pluginListFilePath;

		// Create a scripting engine of Vnano.
		this.engine = new VnanoEngine();

		// Load library scripts and plug-ins.
		try {
			this.loadLibraryScripts();
			this.loadPlugins();

			// Connect the plug-in providing "output" function, implemented in this class.
			this.engine.connectPlugin("OutputPlugin", new Model.OutputPlugin(setting, isGuiMode));

		// Don't shutdown this app when it fails to load any library/plug-in, but notify users.
		} catch (Exception e) {
			String message = e.getMessage();
			if (e.getCause() != null && e.getCause().getMessage() != null) {
					message = e.getCause().getMessage();
			}
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(message, "Plug-in/Library Loading Error", setting.localeCode, setting.alwaysPrintError);
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(message, "プラグイン/ライブラリ 読み込みエラー", setting.localeCode, setting.alwaysPrintError);
			}

			// For errors occurred in plug-ins, always print the stack trace no matter whether it is specified by settings.
			System.err.println("\n" + message);
			MessageManager.showExceptionStackTrace(e, setting.localeCode);
		}

		// Create a Map for storing permission item names and values (permission map).
		Map<String, String> permissionMap = new HashMap<String, String>();

		// Set the default value of all permissions to "ASK".
		permissionMap.put("DEFAULT", "ASK");

		// Following permission items are safe for purposes of this app, so set to "ALLOW".
		permissionMap.put("FILE_READ", "ALLOW");         // File reading.
		permissionMap.put("FILE_WRITE", "ALLOW");        // File writing, excluding overwriting.
		permissionMap.put("FILE_CREATE", "ALLOW");       // File creation, excluding overwriting.
		permissionMap.put("DIRECTORY_LIST", "ALLOW");    // Listing files in a directory.
		permissionMap.put("DIRECTORY_CREATE", "ALLOW");  // Directory creation.
		permissionMap.put("PROGRAM_EXIT", "ALLOW");      // Termination from script-side, by calling "exit" function.

		// Set permission settings to the scripting engine.
		try {
			this.engine.setPermissionMap(permissionMap);
		} catch (VnanoException e) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(
					e.getMessage(), "Permission Setting Error", setting.localeCode, setting.alwaysPrintError
				);
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(
					e.getMessage(), "パーミッション設定エラー", setting.localeCode, setting.alwaysPrintError
				);
			}
		}
	}


	/**
	 * Loads library scripts listed in the library list file.
	 * 
	 * @throws VnanoException Thrown if it fails to load any library.
	 */
	private final void loadLibraryScripts() throws VnanoException {
	    ScriptLoader scriptLoader = new ScriptLoader("UTF-8");
	    scriptLoader.setLibraryScriptListPath(this.libraryListFilePath);
	    scriptLoader.load();
	    String[] libPaths = scriptLoader.getLibraryScriptPaths(true);
	    String[] libScripts = scriptLoader.getLibraryScriptContents();
	    int libCount = libScripts.length;
	    for (int ilib=0; ilib<libCount; ilib++) {
	        this.engine.registerLibraryScript(libPaths[ilib], libScripts[ilib]);
	    }
	}


	/**
	 * Loads plug-ins listed in the plug-in list file.
	 * 
	 * @throws VnanoException Thrown if it fails to load any plug-in.
	 */
	private final void loadPlugins() throws VnanoException {
        PluginLoader pluginLoader = new PluginLoader("UTF-8");
        pluginLoader.setPluginListPath(this.pluginListFilePath);
        pluginLoader.load();
        for (Object plugin: pluginLoader.getPluginInstances()) {
            this.engine.connectPlugin("___VNANO_AUTO_KEY", plugin);
        }
	}


	/**
	 * Shutdown this model instance.
	 * 
	 * @param setting The container storing setting values.
	 */
	public void shutdown(SettingContainer setting) {
		try {
			// Unload all library scripts and plug-ins.
			this.engine.disconnectAllPlugins();
			this.engine.unregisterAllLibraryScripts();

		// Don't re-throw to the upper layer, because we can not do nothing at there.
		} catch (Exception e) {
			String message = e.getMessage();
			if (e.getCause() != null && e.getCause().getMessage() != null) {
					message = e.getCause().getMessage();
			}
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(
					message, "Plug-in Finalization Error", setting.localeCode, setting.alwaysPrintError
				);
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(
					message, "プラグイン終了時処理エラー", setting.localeCode, setting.alwaysPrintError
				);
			}

			// For errors occurred in plug-ins, always print the stack trace no matter whether it is specified by settings.
			System.err.println("\n" + message);
			MessageManager.showExceptionStackTrace(e, setting.localeCode);
		}
	}


	/**
	 * Calculates the specified expression, or executes the specified script, on the caller thread.
	 * 
	 * @param inputtedContent The calculation expression or the name/path of the script.
	 * @param isGuiMode Specify true if this app is running in GUI mode.
	 * @param setting The container storing setting values.
	 * @return The calculation result value, or the value passed to "output" function from the script.
	 * 
	 * @throws VnanoException Thrown if the content of the expression or the script is incorrect.
	 */
	public final String calculate(String inputtedContent, boolean isGuiMode, SettingContainer setting)
			throws VnanoException {

		// Caution!
		// Don't make this method "synchronized", otherwise calculation requests are piled up 
		// when an user mashes "=" button or Enter key.
		// Hence the caller side must check whether any calculation or script is running before calling this method.
		// If any calculation/script is running, it must decline the additional calculation request.

		if (this.calculating) {
			throw new RINPnFatalException("The previous calculation has not finished yet");
		}
		this.calculating = true;

		// If the inputted content is empty, return an empty string.
		if (inputtedContent.trim().length() == 0) {
			calculating = false;
			return "";
		}

		// Clear the variable storing the value passed to "output" function from the script.
		this.lastOutputContent = null;

		// Stores whether the inputted content is the name/path of a script file.
		// If true, also stores the script file.
		boolean scriptFileInputted = false;
		File scriptFile = null;

		// Trim spaces and double quotations at the head/tail of the inputted content.
		String trimmedContent = inputtedContent.trim();
		if (trimmedContent.startsWith("\"") && trimmedContent.endsWith("\"")) {
			trimmedContent = trimmedContent.substring(1, trimmedContent.length()-1);
		}

		// If the above ends with ".vnano", it is the name/path of a script file.
		if (trimmedContent.endsWith(SCRIPT_EXTENSION)) {
			scriptFileInputted = true;
			scriptFile = new File(trimmedContent);

			// If the file path is not absolute, regard it as the relative file path from the "dirPath".
			if (!scriptFile.isAbsolute()) {
				scriptFile = new File(dirPath, scriptFile.getPath());
			}

			// Load the script code from the script file, 
			// and replace the value of inputtedContent (= file path) to the script code.
			ScriptLoader loader = new ScriptLoader(DEFAULT_SCRIPT_ENCODING);
			loader.setMainScriptPath(scriptFile.getAbsolutePath());
			loader.load();
			inputtedContent = loader.getMainScriptContent();

			// Temporary disable some options, because they are not suitable for running scripts.
			// Note that, must clone the setting container before modifying its values, otherwise 
			// the following options will be kept to be disabled even after when the execution of the script completes.
			// (An expression, not a script, may be inputted next time, so they should not be kept to be disabled.)
			try {
				setting = setting.clone();
				setting.evalIntLiteralAsFloat = false;
				setting.evalOnlyFloat = false;
				setting.evalOnlyExpression = false;
			} catch (CloneNotSupportedException e) {
				this.calculating = false;
				throw new RINPnFatalException(e);
			}

		// Otherwise the inputted content is a calculation expression.
		} else {

			// Normalize the content of the expression (e.g.: replace full-width characters to half-width chars).
			if (setting.inputNormalizerEnabled) {
				inputtedContent = Normalizer.normalize(inputtedContent, Normalizer.Form.NFKC);
			}

			// Append a semicolon ";" at the end of the expression, if it does not exist.
			if (!inputtedContent.trim().endsWith(";")) {
				inputtedContent += ";";
			}
		}


		// Load library scripts and plug-ins.
		try {
			if (setting.reloadLibrary) {
				this.engine.unregisterAllLibraryScripts();
				this.loadLibraryScripts();
			}
			if (setting.reloadPlugin) {
				this.engine.disconnectAllPlugins();
				this.loadPlugins();
			}

		// Don't shutdown this app when it fails to load any library/plug-in, but notify users.
		} catch (Exception e) {
			String message = e.getMessage();
			if (e.getCause() != null && e.getCause().getMessage() != null) {
					message = e.getCause().getMessage();
			}
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(
					message, "Plug-in/Library Loading Error", setting.localeCode, setting.alwaysPrintError
				);
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(
					message, "プラグイン/ライブラリ 読み込みエラー", setting.localeCode, setting.alwaysPrintError
				);
			}

			// For errors occurred in plug-ins, always print the stack trace no matter whether it is specified by settings.
			System.err.println("\n" + message);
			MessageManager.showExceptionStackTrace(e, setting.localeCode);
		}


		// Set options to the engine.
		Map<String, Object> optionMap = new HashMap<String, Object>();
		optionMap.put("ACCELERATOR_ENABLED", setting.acceleratorEnabled);
		optionMap.put("ACCELERATOR_OPTIMIZATION_LEVEL", setting.acceleratorOptimizationLevel);
		optionMap.put("EVAL_INT_LITERAL_AS_FLOAT", setting.evalIntLiteralAsFloat);
		optionMap.put("EVAL_ONLY_FLOAT", setting.evalOnlyFloat);
		optionMap.put("EVAL_ONLY_EXPRESSION", setting.evalOnlyExpression);
		optionMap.put("LOCALE", LocaleCode.toLocale(setting.localeCode));
		optionMap.put("DUMPER_ENABLED", setting.dumperEnabled);
		optionMap.put("DUMPER_TARGET", setting.dumperTarget);
		optionMap.put("UI_MODE", isGuiMode ? "GUI" : "CUI");
		optionMap.put("FILE_IO_ENCODING", DEFAULT_FILE_IO_ENCODING);
		if (scriptFileInputted) {
			optionMap.put("MAIN_SCRIPT_NAME", scriptFile.getName());
			optionMap.put("MAIN_SCRIPT_DIRECTORY", scriptFile.getParentFile().getAbsolutePath());
		}
		this.engine.setOptionMap(optionMap);


		// Perform the calculation or run the script/
		Object value = null;
		try {
			value = this.engine.executeScript(inputtedContent);
		} catch (VnanoException e) {
			this.calculating = false;
			throw e;
		}

		// Stores the result to the following variable, and return it as the return value of this method.
		String outputText = null;

		// If the inputted content is a script, we regard the value passed to "output" function as the result.
		// It is stored in "lastOutputContent" field, in GUI mode.
		// (In CUI mode, it had already been printed to the standard output, so we should do nothing.)
		if (scriptFileInputted) {
			if (isGuiMode) {
				outputText = this.lastOutputContent;
			}

		// If the inputted content is the calculation expression, round its result.
		} else {
			if (value instanceof Double) {
				if ( !((Double)value).isNaN() && !((Double)value).isInfinite() ) {
					value = OutputValueFormatter.round( ((Double)value).doubleValue(), setting); // Rounding： to BigDecimal
					value = OutputValueFormatter.simplify( (BigDecimal)value );                  // Formatting： to String
				}
			}
			if (value != null) {
				value = value.toString();
			}
			outputText = (String)value;
		}

		this.calculating = false;
		return outputText;
	}


	/**
	 * Calculates the specified expression, or executes the specified script, on an other thread.
	 * 
	 * @param inputtedContent The calculation expression or the name/path of the script.
	 * @param The listener which will be called-back when the calculation/execution completes.
	 * @param setting The container storing setting values.
	 */
	public void calculateAsynchronously(String inputtedContent, 
			AsyncCalculationListener asyncCalcListener, SettingContainer setting) {
		
		AsyncCalculationRunner asyncCalcRunner = new AsyncCalculationRunner(inputtedContent, asyncCalcListener, setting);
		Thread calculatingThread = new Thread(asyncCalcRunner);
		calculatingThread.start();
	}

	// 非同期で計算処理を実行するためのRunnable実装

	/**
	 * The Runnable implementation for performing the internal process of 
	 * "calculateAsynchronously" method on an other thread. 
	 */
	private final class AsyncCalculationRunner implements Runnable {
		private AsyncCalculationListener calculationListener = null;
		private SettingContainer setting = null;
		private String inputtedContent = null;

		public AsyncCalculationRunner(
				String inputtedContent, AsyncCalculationListener scriptListener, SettingContainer setting) {

			this.inputtedContent = inputtedContent;
			this.calculationListener = scriptListener;
			this.setting = setting;
		}

		@Override
		public final void run() {
			if (isCalculating()) {
				if (setting.localeCode.equals(LocaleCode.EN_US)) {
					MessageManager.showErrorMessage(
						"The previous calculation has not finished yet.", "!", setting.localeCode, setting.alwaysPrintError
					);
				}
				if (setting.localeCode.equals(LocaleCode.JA_JP)) {
					MessageManager.showErrorMessage(
						"まだ前の計算を実行中です。", "!", setting.localeCode, setting.alwaysPrintError
					);
				}
				return;
			}

			try {
				String outputText = calculate(this.inputtedContent, true, this.setting);
				this.calculationListener.calculationFinished(outputText);

			} catch (VnanoException | VnanoFatalException | RINPnFatalException e) {

				this.calculationListener.calculationFinished("ERROR");
				String errorMessage = MessageManager.customizeExceptionMessage(e.getMessage());
				if (setting.localeCode.equals(LocaleCode.EN_US)) {
					MessageManager.showErrorMessage(
						errorMessage, "Expression/Script Error", setting.localeCode, setting.alwaysPrintError
					);
				}
				if (setting.localeCode.equals(LocaleCode.JA_JP)) {
					MessageManager.showErrorMessage(
						errorMessage, "計算式やスクリプトのエラー", setting.localeCode, setting.alwaysPrintError
					);
				}
				if (setting.exceptionStackTracerEnabled) {
					MessageManager.showExceptionStackTrace(e, setting.localeCode);
				}
			}
		}
	}

}
