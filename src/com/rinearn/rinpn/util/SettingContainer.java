/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn.util;

import java.awt.Font;
import java.io.File;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.vcssl.nano.VnanoEngine;
import org.vcssl.nano.VnanoException;
import org.vcssl.nano.VnanoFatalException;
import org.vcssl.nano.interconnect.PluginLoader;
import org.vcssl.nano.interconnect.ScriptLoader;

import com.rinearn.rinpn.RINPnException;
import com.rinearn.rinpn.RINPnFatalException;

/**
 * The class of the container for storing setting values.
 */
public final class SettingContainer implements Cloneable {

	/** The name of the font of INPUT/OUTPUT text fields. */
	public static final String TEXT_FIELD_FONT_NAME = "Monospaced";

	/** The type of the font of INPUT/OUTPUT text fields. */
	public static final int TEXT_FIELD_FONT_TYPE = Font.BOLD;

	/** The font of text labels. */
	public static final Font LABEL_FONT = new Font("Dialog", Font.BOLD, 14);

	/** The font of "=" buttons. */
	public static final Font RUN_BUTTON_FONT = new Font("Dialog", Font.BOLD, 14);

	/** The font of exit buttons. */
	public static final Font EXIT_BUTTON_FONT = new Font("Dialog", Font.BOLD, 14);

	/**
	 * The extension of the setting file: "txt".
	 * Note that Both extensions ".txt" and ".vnano" are valid for setting files.
	 */
	public static final String SETTING_SCRIPT_PATH_TXT = "Settings.txt";

	/**
	 * The extension of the setting file: "vnano".
	 * Note that Both extensions ".txt" and ".vnano" are valid for setting files.
	 */
	public static final String SETTING_SCRIPT_PATH_VNANO = "Settings.vnano";

	/** The default character encoding of the setting file. */
	public static final String DEFAULT_SETTING_SCRIPT_ENCODING = "UTF-8";

	/** The maximum length of the digits to be rounded. */
	public static final int MAX_ROUNDING_LENGTH = 100;

	// ====================================================================================================
	// The following fields correspond the items in the setting file.
	// Their values are updated by Vnano Engine, when it execute the setting file as a script.
	// ( An instance of this class will be connected to the engine as a plug-in,
	//   so fields of the instance can be accessed from the script runs on the engine. )
	// ====================================================================================================

	public int textFieldFontSize = 18;
	public int numberKeyFontSize = 18;
	public int behaviorKeyFontSize = 18;
	public int functionKeyFontSize = 14;

	public boolean stayOnTopOfAllWindows = true;
	public int windowWidth = 540;
	public int windowHeight = 380;
	public double windowOpacity = 0.76;
	public int retractedWindowHeight = 160;

	public int windowBackgroundColorR = 180;
	public int windowBackgroundColorG = 220;
	public int windowBackgroundColorB = 255;

	public int textFieldBackgroundColorR = 100;
	public int textFieldBackgroundColorG = 100;
	public int textFieldBackgroundColorB = 100;

	public int textFieldForegroundColorR = 140;
	public int textFieldForegroundColorG = 255;
	public int textFieldForegroundColorB = 180;

	public int textLabelForgroundColorR = 100;
	public int textLabelForgroundColorG = 120;
	public int textLabelForgroundColorB = 140;

	public int keyRetractorForegroundColorR = 100;
	public int keyRetractorForegroundColorG = 120;
	public int keyRetractorForegroundColorB = 140;

	public boolean inputNormalizerEnabled = true;
	public boolean outputRounderEnabled = true;
	public boolean performImplicitRoundingBeforeRounding = true;
	public String roundingMode = "HALF_UP";
	public String roundingTarget = "SIGNIFICAND";
	public int roundingLength = 10;

	public boolean reloadPlugin = false;
	public boolean reloadLibrary = true;

	public boolean acceleratorEnabled = true;
	public int acceleratorOptimizationLevel = 0;

	public boolean evalIntLiteralAsFloat = true;
	public boolean evalOnlyFloat = true;
	public boolean evalOnlyExpression = true;

	public boolean dumperEnabled = false;
	public String dumperTarget = "ALL";

	public boolean exceptionStackTracerEnabled = false;

	public String localeCode = LocaleCode.getDefaultLocaleCode();


	/**
	 * Clones this instance.
	 * 
	 * @return The cloned instance.
	 */
	@Override
	public SettingContainer clone() throws CloneNotSupportedException {
		SettingContainer cloned = (SettingContainer) ( super.clone() );
		// When we add non-mutable fields to this class, we must deep-copy their values here.
		return cloned;
	}


	/**
	 * Execute the setting file as a script, for updating the values of this instance.
	 * 
	 * @param settingScriptFilePath The path of the setting (script) file.
	 * @param libraryListFilePath The path of the library list file.
	 * @param pluginListFilePath The path of the plug-in list file.
	 * @param isGuiMode Specify true it this application runs in GUI mode.
	 * @param debug Specify true if this application runs in debugging mode.
	 * @throws RINPnException Thrown if invalid settings or script code exists.
	 */
	public synchronized final void evaluateSettingScript(
			String settingScriptFilePath,
			String libraryListFilePath, String pluginListFilePath, boolean isGuiMode, boolean debug)
					throws RINPnException {

		String localeCode = LocaleCode.getDefaultLocaleCode();
		File settingScriptFile = new File(settingScriptFilePath);

		// Create a Vnano Engine for executing the setting file as a script.
		VnanoEngine settingVnanoEngine = new VnanoEngine();

		// Connect plug-ins to the above engine.
		// (Don't load library scripts, because it make the debugging of the setting file difficult.)
		try {

			// Load all plug-ins specified in the plug-in list file.
	        PluginLoader pluginLoader = new PluginLoader("UTF-8");
	        pluginLoader.setPluginListPath(pluginListFilePath);
	        pluginLoader.load();
	        for (Object plugin: pluginLoader.getPluginInstances()) {
	        	settingVnanoEngine.connectPlugin("___VNANO_AUTO_KEY", plugin);
	        }

	        // Load this instance as a plug-in, for updating its fields.
			settingVnanoEngine.connectPlugin("SettingContainer", this);

		// Don't re-throw to the upper layer, because we can not do nothing at there.
		} catch (Exception e) {
			String message = e.getMessage();
			if (e.getCause() != null && e.getCause().getMessage() != null) {
					message = e.getCause().getMessage();
			}
			if (localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(message, "Plug-in Loading Error", localeCode);
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(message, "プラグイン読み込みエラー", localeCode);
			}
			System.err.println("\n" + message);
			MessageManager.showExceptionStackTrace(e, localeCode);
		}

		// Set the engine's options.
		Map<String, Object> optionMap = new HashMap<String, Object>();
		optionMap.put("MAIN_SCRIPT_NAME", settingScriptFile.getName());
		optionMap.put("DUMPER_ENABLED", debug);
		optionMap.put("ACCELERATOR_ENABLED", false);
		optionMap.put("UI_MODE", isGuiMode ? "GUI" : "CUI");
		try {
			settingVnanoEngine.setOptionMap(optionMap);
		} catch (VnanoException vne) {
			throw new RINPnFatalException(vne);
		}

		if (debug) {
			System.out.println("");
			System.out.println("################################################################################");
			System.out.println("### - Debug Info -");
			System.out.println("###   Results of \"--dump\" option of the Vnano Engine for the setting script");
			System.out.println("################################################################################");
			System.out.println("");
		}

		// Load the setting file, and execute its content as a script code.
		// By this step, the values of this instance (= setting values) will be updated.
		try {
			ScriptLoader loader = new ScriptLoader(DEFAULT_SETTING_SCRIPT_ENCODING);
			loader.setMainScriptPath(settingScriptFile.getPath());
			loader.load();
			String settingScriptCode = loader.getMainScriptContent();
			settingVnanoEngine.executeScript(settingScriptCode);

		// Thrown when any error exists in the script (setting file).
		} catch (VnanoException | VnanoFatalException vne) {
			String errorMessage = MessageManager.customizeExceptionMessage(vne.getMessage());
			if (localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(errorMessage, "Setting Error", localeCode);
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(errorMessage, "設定スクリプトのエラー", localeCode);
			}
			throw new RINPnException(vne);
		}

		// Disconnect plug-ins.
		try {
			settingVnanoEngine.disconnectAllPlugins();

		// Don't re-throw to the upper layer, because we can not do nothing at there.
		} catch (Exception e) {
			String message = e.getMessage();
			if (e.getCause() != null && e.getCause().getMessage() != null) {
					message = e.getCause().getMessage();
			}
			if (localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(message, "Plug-in Finalization Error", localeCode);
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(message, "プラグイン終了時処理エラー", localeCode);
			}
			System.err.println("\n" + message);
			MessageManager.showExceptionStackTrace(e, localeCode);
		}

		// Check/normalize the updated values of the fields of this instance (= setting values).
		this.checkAndNormalizeSettingValues();
	}


	/**
	 * Checks and normalizes the updated values of the fields of this instance (= setting values).
	 * 
	 * @throws RINPnException Thrown if invalid setting value exists.
	 */
	private final void checkAndNormalizeSettingValues()
			throws RINPnException {

		boolean errorOccurred = false;
		String errorMessage = null;

		this.localeCode = this.localeCode.toLowerCase();
		if (!LocaleCode.isSupported(this.localeCode)) {

			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The locale-code \"" + this.localeCode + "\" is not supported.";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "非対応のロケールコード \"" + this.localeCode + "\" が指定されています。";
			}
			errorOccurred = true;
		}
		if (this.localeCode.equals(LocaleCode.AUTO)) {
			this.localeCode = LocaleCode.getDefaultLocaleCode();
		}

		if (this.textFieldFontSize <= 0) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"textFieldFontSize\" should be grater than 0.";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"textFieldFontSize\" の値は 0 よりも大きい必要があります。";
			}
			errorOccurred = true;
		}
		if (this.windowWidth <= 0) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"windowWidth\" should be grater than 0.";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"windowWidth\" の値は 0 よりも大きい必要があります。";
			}
			errorOccurred = true;
		}
		if (this.windowHeight <= 0) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"windowHeight\" should be grater than 0.";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"windowHeight\" の値は 0 よりも大きい必要があります。";
			}
			errorOccurred = true;
		}
		if (this.windowOpacity <= 0) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"windowOpacity\" should be grater than 0.";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"windowOpacity\" の値は 0 よりも大きい必要があります。";
			}
			errorOccurred = true;
		}

		if (this.windowBackgroundColorR < 0 || 255 < this.windowBackgroundColorR) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"windowBackgroundColorR\" should be in the range of 0 ~ 255.";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"windowBackgroundColorR\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}
		if (this.windowBackgroundColorG < 0 || 255 < this.windowBackgroundColorG) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"windowBackgroundColorG\" should be in the range of 0 ~ 255.";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"windowBackgroundColorG\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}
		if (this.windowBackgroundColorB < 0 || 255 < this.windowBackgroundColorB) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"windowBackgroundColorB\" should be in the range of 0 ~ 255.";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"windowBackgroundColorB\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}

		if (this.textFieldBackgroundColorR < 0 || 255 < this.textFieldBackgroundColorR) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"textFieldBackgroundColorR\" should be in the range of 0 ~ 255.";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"textFieldBackgroundColorR\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}
		if (this.textFieldBackgroundColorG < 0 || 255 < this.textFieldBackgroundColorG) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"textFieldBackgroundColorG\" should be in the range of 0 ~ 255.";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"textFieldBackgroundColorG\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}
		if (this.textFieldBackgroundColorB < 0 || 255 < this.textFieldBackgroundColorB) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"textFieldBackgroundColorB\" should be in the range of 0 ~ 255.";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"textFieldBackgroundColorB\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}

		if (this.textFieldForegroundColorR < 0 || 255 < this.textFieldForegroundColorR) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"textFieldForegroundColorR\" should be in the range of 0 ~ 255.";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"textFieldForegroundColorR\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}
		if (this.textFieldForegroundColorG < 0 || 255 < this.textFieldForegroundColorG) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"textFieldForegroundColorG\" should be in the range of 0 ~ 255.";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"textFieldForegroundColorG\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}
		if (this.textFieldForegroundColorB < 0 || 255 < this.textFieldForegroundColorB) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"textFieldForegroundColorB\" should be in the range of 0 ~ 255.";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"textFieldForegroundColorB\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}


		try {
			// RoundingMode enum の要素名でなければエラーとするが、
			// "HALF_TO_EVEN" は丸め時に "HALF_EVEN" に変換するため、特例的に許可
			// （OutputValueFormatter内コメント参照）
			if (!this.roundingMode.equals("HALF_TO_EVEN")) {
				RoundingMode.valueOf(this.roundingMode);
			}
		} catch (IllegalArgumentException e) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "Invalid value of \"roundingMode\": " + this.roundingMode;
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"roundingMode\" に、正しい選択肢には無い値が指定されています: " + this.roundingMode;
			}
			errorOccurred = true;
		}

		try {
			RoundingTarget.valueOf(this.roundingTarget);
		} catch (IllegalArgumentException e) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "Invalid value of \"roundingTarget\": " + this.roundingMode;
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"roundingTarget\" に、正しい選択肢には無い値が指定されています: " + this.roundingMode;
			}
			errorOccurred = true;
		}

		if (this.roundingLength < 1 || MAX_ROUNDING_LENGTH < this.roundingLength) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"roundingLength\" should be in the range of 1 ~ "
				             + MAX_ROUNDING_LENGTH + ".";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"roundingLength\" の値は 1 ~ "
				             + MAX_ROUNDING_LENGTH + " の範囲内で指定してください。";
			}
			errorOccurred = true;
		}

		if (errorOccurred) {
			String errorTitle = null;
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorTitle = "Setting Error";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorTitle = "設定エラー";
			}
			MessageManager.showErrorMessage(errorMessage, errorTitle, localeCode);
			throw new RINPnException(errorMessage);
		}
	}
}
