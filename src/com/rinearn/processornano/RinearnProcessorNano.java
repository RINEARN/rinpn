/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.script.ScriptException;
import javax.swing.SwingUtilities;

import com.rinearn.processornano.calculator.Calculator;
import com.rinearn.processornano.event.EventListenerManager;
import com.rinearn.processornano.spec.LocaleCode;
import com.rinearn.processornano.spec.SettingContainer;
import com.rinearn.processornano.ui.UIContainer;
import com.rinearn.processornano.ui.UIInitializer;
import com.rinearn.processornano.util.MessageManager;


public final class RinearnProcessorNano {

	public RinearnProcessorNano() {
		// 起動は、インスタンス生成後に明示的に launchCalculatorWindow() を呼ぶ
	}


	/**
	 * 電卓画面を起動せずに、計算を実行し、結果を標準出力に表示します。
	 *
	 * @param scriptCode 計算内容（式またはスクリプトコード）
	 */
	public final void calculate(String scriptCode) {

		// メッセージの出力をコマンドラインモードに変更
		MessageManager.setDisplayType(MessageManager.DISPLAY_MODE.CUI);

		// 設定値コンテナと計算機を生成して初期化
		SettingContainer setting = null;
		Calculator calculator = null;
		try {
			setting = this.createInitializedSettingContainer();
			calculator = this.createInitializedCalculator(setting);

		// スクリプトエンジンの接続や、設定スクリプト/ライブラリの読み込みエラーなどで失敗した場合
		} catch (RinearnProcessorNanoException e) {
			e.printStackTrace();
			return;
		}

		// 計算を実行
		Object value = null;
		try {
			value = calculator.calculate(scriptCode, setting);
		} catch (ScriptException e) {
			String message = MessageManager.customizeExceptionMessage(e.getMessage());
			MessageManager.showErrorMessage(message, "!");
			e.printStackTrace();
		}

		// 結果を表示
		if (value != null) {
			System.out.println(value.toString());
		}
	}


	/**
	 * 電卓画面を起動します。
	 */
	public final void launchCalculatorWindow() {

		// 設定値コンテナと計算機を生成して初期化
		SettingContainer setting = null;
		Calculator calculator = null;
		try {
			setting = this.createInitializedSettingContainer();
			calculator = this.createInitializedCalculator(setting);

		// スクリプトエンジンの接続や、設定スクリプト/ライブラリの読み込みエラーなどで失敗した場合
		} catch (RinearnProcessorNanoException e) {
			e.printStackTrace();
			return;
		}


		// 電卓画面のUIを生成して初期化
		UIContainer ui = new UIContainer();
		try {
			UIInitializer uiInitialiser = new UIInitializer(ui, setting); // 別スレッドで初期化するためのRunnable
			SwingUtilities.invokeAndWait(uiInitialiser);                  // それをSwingのイベントスレッドで実行

		// 初期化実行スレッドの処理待ち時の割り込みで失敗した場合など（結構異常な場合なので、リトライせず終了する）
		} catch (InvocationTargetException | InterruptedException e) {

			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage("Unexpected exception occurred: " + e.getClass().getCanonicalName(), "Error");
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage("予期しない例外が発生しました: " + e.getClass().getCanonicalName(), "エラー");
			}
			e.printStackTrace();
			return; // この例外が発生する場合はまだUI構築が走っていないので、破棄するUIリソースはない
		}

		// UIの各部品にイベントリスナを登録
		EventListenerManager.addAllEventListenersToUI(ui, calculator, setting);
	}


	/**
	 * 設定スクリプトを実行して値を初期化済みの、設定値コンテナを生成して返します。
	 *
	 * @return 初期化済みの設定値コンテナ
	 * @throws RinearnProcessorNanoException
	 * 		設定スクリプトの読み込みエラーなどで失敗した場合にスローされます。
	 */
	private final SettingContainer createInitializedSettingContainer()
			throws RinearnProcessorNanoException {

		SettingContainer setting = new SettingContainer();

		// 設定スクリプトを読み込む
		String settingScriptCode = null;
		settingScriptCode = this.loadCode(
			SettingContainer.SETTING_SCRIPT_PATH, SettingContainer.SETTING_SCRIPT_ENCODING,
			LocaleCode.getDefaultLocaleCode()
		);

		// 設定スクリプトを実行して設定値を書き込む（スクリプトエンジンはメソッド内で生成）
		setting.evaluateSettingScript(settingScriptCode, SettingContainer.SETTING_SCRIPT_PATH);

		return setting;
	}


	/**
	 * ライブラリスクリプトや設定値を読み込んで初期化済みの、計算機を生成して返します。
	 *
	 * @return 初期化済みの計算機
	 * @throws RinearnProcessorNanoException
	 * 		スクリプトエンジンの接続や、ライブラリの読み込みエラーなどで失敗した場合にスローされます。
	 */
	private final Calculator createInitializedCalculator(SettingContainer setting)
			throws RinearnProcessorNanoException {

		Calculator calculator = new Calculator();

		// ライブラリスクリプトを読み込む
		String libraryScriptCode = this.loadCode(
			setting.libraryScriptPath, setting.libraryScriptEncoding, setting.localeCode
		);

		// 計算機を初期化
		calculator.initialize(setting, libraryScriptCode);

		return calculator;
	}


	/**
	 * ファイルからコードを読み込みます。
	 *
	 * @param filePath ファイルのパス
	 * @param encoding 文字コード
	 * @param localeCode エラーメッセージの言語を指定するロケールコード
	 * @return 読み込まれたコード
	 * @throws RinearnProcessorNanoException
	 * 		読み込みに失敗した場合にスローされます。
	 */
	private final String loadCode(String filePath, String encoding, String localeCode)
			throws RinearnProcessorNanoException {

		// 指定されたファイルが存在するか検査
		if (!new File(filePath).exists()) {
			if (localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage("The file \"" + filePath + "\" does not exist.", "Code Loading Error");
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage("ファイル \"" + filePath + "\" が見つかりません。", "コード読み込みエラー");
			}
			throw new RinearnProcessorNanoException("The file \"" + filePath + "\" does not exist.");
		}

		// 読み込み処理
		try {

			// 全行を読み込み、行単位で格納したListを取得
			List<String> lines = Files.readAllLines(Paths.get(filePath), Charset.forName(encoding));

			// 改行コードを挟みつつ全行を結合
			StringBuilder codeBuilder = new StringBuilder();
			String eol = System.getProperty("line.separator");
			for (String line: lines) {
				codeBuilder.append(line);
				codeBuilder.append(eol);
			}
			String code = codeBuilder.toString();

			// UTF-8ではBOMの有無を検査し、付いている場合は削除
			if(encoding.toLowerCase().equals("utf-8")) {
				// UTF-8のBOMは 0xEF 0xBB 0xBF だが、文字列内部表現がUTF-16な都合で、読み込み後は 0xFEFF が付いている
				final char bom = (char)0xFEFF;
				if(0 < code.length() && code.charAt(0) == bom){
					code = code.substring(1);
				}
			}

			return code;

		// 非対応の文字コードが指定された場合
		} catch (UnsupportedCharsetException uce) {

			if (localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage("The encoding \"" + encoding + "\" is not supported.", "Code Loading Error");
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage("非対応の文字コード \"" + encoding + "\" が指定されています。", "コード読み込みエラー");
			}
			throw new RinearnProcessorNanoException(uce);

		// 何らかの理由で読み込みに失敗した場合
		} catch (IOException ioe) {

			if (localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(
					"An error (IOException) occurred for the loading of \"" + filePath + "\".",
					"Code Loading Error"
				);
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(
					"\"" + filePath + "\" の読み込みにおいて、エラー (IOException) が発生しました。",
					"コード読み込みエラー"
				);
			}
			throw new RinearnProcessorNanoException(ioe);
		}
	}

}
