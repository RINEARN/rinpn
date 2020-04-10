/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.spec;

import java.awt.Font;
import java.io.File;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.rinearn.processornano.RinearnProcessorNanoException;
import com.rinearn.processornano.util.MessageManager;

public final class SettingContainer implements Cloneable {

	public static final String TEXT_FIELD_FONT_NAME = "Monospaced";
	public static final int TEXT_FIELD_FONT_TYPE = Font.BOLD;
	public static final Font LABEL_FONT = new Font("Dialog", Font.BOLD, 14);
	public static final Font BUTTON_FONT = new Font("Dialog", Font.BOLD, 14);
	public static final Font EXIT_BUTTON_FONT = new Font("Dialog", Font.BOLD, 14);

	public static final String SETTING_SCRIPT_PATH = "./Setting.vnano";
	public static final String SETTING_SCRIPT_ENCODING = "UTF-8";

	public static final int MAX_ROUNDING_LENGTH = 100;

	// 各設定値のフィールドは、スクリプトエンジンに接続して設定スクリプト内で読み書きするため、
	// getter/setter で包まずに、外からも public なフィールドとして直接アクセスする

	public int textFieldFontSize = 18;
	public boolean stayOnTopOfAllWindows = true;
	public int windowWidth = 400;
	public int windowHeight = 160;
	public double windowOpacity = 0.76;

	public int windowBackgroundColorR = 180;
	public int windowBackgroundColorG = 220;
	public int windowBackgroundColorB = 255;

	public int textFieldBackgroundColorR = 100;
	public int textFieldBackgroundColorG = 100;
	public int textFieldBackgroundColorB = 100;

	public int textFieldForegroundColorR = 140;
	public int textFieldForegroundColorG = 255;
	public int textFieldForegroundColorB = 180;

	public boolean inputNormalizerEnabled = true;
	public boolean outputRounderEnabled = true;
	public String roundingMode = "HALF_UP";
	public String roundingTarget = "SIGNIFICAND";
	public int roundingLength = 10;

	public boolean acceleratorEnabled = true;
	public boolean evalNumberAsFloat = true;
	public String libraryFolder = "./lib/";
	public String libraryEncoding = "UTF-8";
	public String libraryExtension = ".vnano";
	public String[] pluginPaths = new String[0];

	public boolean dumperEnabled = false;
	public String dumperTarget = "ALL";

	public boolean exceptionStackTracerEnabled = false;

	public String localeCode = LocaleCode.getDefaultLocaleCode();


	public synchronized final void evaluateSettingScript(
			String settingScriptCode, String settingScriptName, boolean debug)
					throws RinearnProcessorNanoException {

		String localeCode = LocaleCode.getDefaultLocaleCode();

		// 設定スクリプト解釈用に、Vnanoのスクリプトエンジンを読み込んで生成
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine settingVnanoEngine = manager.getEngineByName("vnano");

		if (settingVnanoEngine == null) {
			if (localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(
					"Please put Vnano.jar in the same directory as RinearnProcessorNano.jar.",
					"Engine Loading Error"
				);
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(
					"Vnano.jar を、RinearnProcessorNano.jar と同じフォルダ内に配置してください。",
					"エンジン読み込みエラー"
				);
			}
			throw new RinearnProcessorNanoException("ScriptEngine of the Vnano could not be loaded.");
		}

		// 設定値をスクリプトから読み書きするため、このインスタンスをスクリプトエンジンにバインディング
		settingVnanoEngine.put("SettingContainer", this); // キーは省略可能な名前空間として使用される

		// スクリプトエンジンに渡すオプションを用意
		//（エラーメッセージ用にスクリプト名し、アクセラレータも無効化する）
		Map<String, Object> optionMap = new HashMap<String, Object>();
		optionMap.put("EVAL_SCRIPT_NAME", settingScriptName);
		optionMap.put("DUMPER_ENABLED", debug);
		optionMap.put("ACCELERATOR_ENABLED", false);
		settingVnanoEngine.put("___VNANO_OPTION_MAP", optionMap);

		if (debug) {
			System.out.println("");
			System.out.println("################################################################################");
			System.out.println("### - Debug Info -");
			System.out.println("###   Results of \"--dump\" option of the Vnano Engine for the setting script");
			System.out.println("################################################################################");
			System.out.println("");
		}

		// 設定スクリプトを読み込み、実行して設定ファイルの記述内容を解釈する
		try {
			settingVnanoEngine.eval(settingScriptCode);
		} catch (ScriptException e) {
			String errorMessage = MessageManager.customizeExceptionMessage(e.getMessage());
			if (localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(errorMessage, "Setting Error");
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(errorMessage, "設定スクリプトのエラー");
			}
			throw new RinearnProcessorNanoException(e);
		}

		// 設定値を検査する
		this.checkAndNormalizeSettingValues();
	}


	private final void checkAndNormalizeSettingValues()
			throws RinearnProcessorNanoException {

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
			RoundingMode.valueOf(this.roundingMode);
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

		if ( !(new File(this.libraryFolder).exists()) ) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The library folder \"" + this.libraryFolder + "\" does not exist.";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "ライブラリの配置フォルダ \"" + this.libraryFolder + "\" が見つかりません。";
			}
			errorOccurred = true;

		} else if (!( new File(this.libraryFolder).isDirectory() )) {
			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The path \"" + this.libraryFolder + "\" specified as the library folder is not a folder.";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "ライブラリの配置場所のパス \"" + this.libraryFolder + "\" が、フォルダではありません。";
			}
			errorOccurred = true;
		}

		if ( !this.libraryEncoding.equals("UTF-8")
		     && !this.libraryEncoding.equals("Shift_JIS") ) {

			if (this.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The encoding \"" + this.libraryEncoding + "\" is not supported.";
			}
			if (this.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "非対応の文字コード \"" + this.libraryEncoding + "\" が指定されています。";
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
			MessageManager.showErrorMessage(errorMessage, errorTitle);
			throw new RinearnProcessorNanoException(errorMessage);
		}
	}

}
