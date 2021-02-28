/*
 * Copyright(C) 2019-2021 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.util;

import java.awt.Font;
import java.io.File;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.rinearn.processornano.RinearnProcessorNanoException;

public final class SettingContainer implements Cloneable {

	public static final String TEXT_FIELD_FONT_NAME = "Monospaced";
	public static final int TEXT_FIELD_FONT_TYPE = Font.BOLD;
	public static final Font LABEL_FONT = new Font("Dialog", Font.BOLD, 14);
	public static final Font BUTTON_FONT = new Font("Dialog", Font.BOLD, 14);
	public static final Font EXIT_BUTTON_FONT = new Font("Dialog", Font.BOLD, 14);

	public static final String SETTING_SCRIPT_PATH = "Setting.vnano";
	public static final String DEFAULT_SETTING_SCRIPT_ENCODING = "UTF-8";

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


	@Override
	public SettingContainer clone() throws CloneNotSupportedException {
		SettingContainer cloned = (SettingContainer) ( super.clone() );

		// 参照型のフィールドを追加した際はここでコピー処理を追記

		return cloned;
	}


	public synchronized final void evaluateSettingScript(
			String settingScriptFilePath,
			String libraryListFilePath, String pluginListFilePath, boolean isGuiMode, boolean debug)
					throws RinearnProcessorNanoException {

		String localeCode = LocaleCode.getDefaultLocaleCode();
		File settingScriptFile = new File(settingScriptFilePath);

		// 設定スクリプト解釈用に、Vnanoのスクリプトエンジンを読み込んで生成
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine settingVnanoEngine = manager.getEngineByName("vnano");

		if (settingVnanoEngine == null) {
			if (localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(
					"Please put Vnano.jar in the same directory as RinearnProcessorNano.jar.",
					"Engine Loading Error",
					localeCode
				);
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(
					"Vnano.jar を、RinearnProcessorNano.jar と同じフォルダ内に配置してください。",
					"エンジン読み込みエラー",
					localeCode
				);
			}
			throw new RinearnProcessorNanoException("ScriptEngine of the Vnano could not be loaded.");
		}

		// ライブラリ/プラグインの読み込みリストファイルを登録
		try {
			settingVnanoEngine.put("___VNANO_LIBRARY_LIST_FILE", libraryListFilePath);
			settingVnanoEngine.put("___VNANO_PLUGIN_LIST_FILE", pluginListFilePath);

		// 読み込みに失敗しても、そのプラグイン/ライブラリ以外の機能には支障が無いため、本体側は落とさない。
		// そのため、例外をさらに上には投げない。（ただし失敗メッセージは表示する。）
		} catch (Exception e) {
			String message = e.getMessage();
			if (e.getCause() != null && e.getCause().getMessage() != null) {
					message = e.getCause().getMessage();
			}
			if (localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(message, "Plug-in/Library Loading Error", localeCode);
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(message, "プラグイン/ライブラリ 読み込みエラー", localeCode);
			}
			System.err.println("\n" + message);
			MessageManager.showExceptionStackTrace(e, localeCode);
		}

		// 設定値をスクリプトから読み書きするため、このインスタンスをスクリプトエンジンにバインディング
		settingVnanoEngine.put("SettingContainer", this); // キーは省略可能な名前空間として使用される

		// スクリプトエンジンに渡すオプションを用意
		//（エラーメッセージ用にスクリプト名し、アクセラレータも無効化する）
		Map<String, Object> optionMap = new HashMap<String, Object>();
		optionMap.put("MAIN_SCRIPT_NAME", settingScriptFile.getName());
		optionMap.put("DUMPER_ENABLED", debug);
		optionMap.put("ACCELERATOR_ENABLED", false);
		optionMap.put("UI_MODE", isGuiMode ? "GUI" : "CUI");
		settingVnanoEngine.put("___VNANO_OPTION_MAP", optionMap);

		if (debug) {
			System.out.println("");
			System.out.println("################################################################################");
			System.out.println("### - Debug Info -");
			System.out.println("###   Results of \"--dump\" option of the Vnano Engine for the setting script");
			System.out.println("################################################################################");
			System.out.println("");
		}

		// 設定スクリプトを読み込む
		SettingContainer defaultSetting = new SettingContainer(); // 設定を読み込むために使うデフォルトの設定（言語ロケールなどが影響）
		String settingScriptCode = ScriptFileLoader.load(
			settingScriptFile.getPath(), DEFAULT_SETTING_SCRIPT_ENCODING, defaultSetting
		);

		// 読み込んだ設定スクリプトを実行して、設定ファイルの記述内容を解釈する
		try {
			settingVnanoEngine.eval(settingScriptCode);

		// 設定スクリプトの内容にエラーがあった場合
		} catch (ScriptException se) {
			String errorMessage = MessageManager.customizeExceptionMessage(se.getMessage());
			if (localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(errorMessage, "Setting Error", localeCode);
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(errorMessage, "設定スクリプトのエラー", localeCode);
			}
			throw new RinearnProcessorNanoException(se);
		}

		// ライブラリ/プラグインを接続解除
		try {
			settingVnanoEngine.put("___VNANO_COMMAND", "REMOVE_PLUGIN");
			settingVnanoEngine.put("___VNANO_COMMAND", "REMOVE_LIBRARY");

		// 設定の読み込みは完了しているため、本体側を落とさないため、例外をさらに上には投げない。通知のみ行う。
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

		// 読み込んだ設定値を検査する
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
			throw new RinearnProcessorNanoException(errorMessage);
		}
	}

}
