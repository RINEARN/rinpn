/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import com.rinearn.processornano.model.CalculatorModel;
import com.rinearn.processornano.presenter.Presenter;
import com.rinearn.processornano.spec.LocaleCode;
import com.rinearn.processornano.spec.SettingContainer;
import com.rinearn.processornano.util.CodeLoader;
import com.rinearn.processornano.util.MessageManager;
import com.rinearn.processornano.view.ViewImpl;
import com.rinearn.processornano.view.ViewInitializer;


public final class RinearnProcessorNano {

	private static final String VERSION = "0.2.3";
	private static final String OPTION_NAME_VERSION = "--version";
	private static final String OPTION_NAME_DEBUG = "--debug";
	private static final String LIBRARY_LIST_FILE = "lib/VnanoLibraryList.txt";
	private static final String PLUGIN_LIST_FILE = "plugin/VnanoPluginList.txt";


	public static void main(String[] args) {

		// コマンドラインから渡された内容を控える変数
		String expression = null;     // 計算式
		boolean debugEnabled = false; // デバッグモードが有効かどうか

		// 引数の解釈
		for (String arg: args) {

			// 引数がバージョン出力オプションだった場合は、バージョンを表示
			if (arg.equals(OPTION_NAME_VERSION)) {
				printVersion();

			// 引数がデバッグオプションだった場合は、デバッグモードを有効化
			} else if (arg.equals(OPTION_NAME_DEBUG)) {
				debugEnabled = true;

			// それ以外の引数は計算式と見なすので、後で計算するために控える
			} else {
				if (expression == null) {
					expression = arg;

				// 既に計算式が控えられている場合は、引数が多すぎるのでエラー
				} else {
					if (LocaleCode.getDefaultLocaleCode().equals(LocaleCode.JA_JP)) {
						System.err.println("コマンドライン引数の数が多すぎます。");
					}
					if (LocaleCode.getDefaultLocaleCode().equals(LocaleCode.EN_US)) {
						System.err.println("Too many command-line arguments.");
					}
				}
			}
		}

		// 計算式が渡されなかった場合は電卓画面を起動
		if (expression == null) {
			new RinearnProcessorNano().launchCalculatorWindow(debugEnabled);

		// 計算式が渡された場合はCUIモードで計算（結果はコマンドラインに表示）
		} else {
			new RinearnProcessorNano().calculate(expression, debugEnabled);
		}
	}

	private static void printVersion() {
		System.out.print("RINPn Ver." + VERSION + " ");

		// Vnanoのバージョンも表示するため、スクリプトエンジンを読み込む
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("vnano");
		if (engine == null) {
			return;
		}
		System.out.print(" / with " + engine.get(ScriptEngine.LANGUAGE));
		System.out.print(" Ver." + engine.get(ScriptEngine.LANGUAGE_VERSION));
		System.out.println("");
	}


	public RinearnProcessorNano() {
		// 電卓画面の起動は、インスタンス生成後に明示的に launchCalculatorWindow() を呼ぶ
	}


	/**
	 * 電卓画面を起動せずに、計算を実行し、結果を標準出力に表示します。
	 *
	 * @param inputExpression 計算式（式またはスクリプトコード）
	 * @param debug デバッグ情報を出力するかどうか
	 */
	public final void calculate(String inputExpression, boolean debug) {

		// メッセージの出力をコマンドラインモードに変更
		MessageManager.setDisplayType(MessageManager.DISPLAY_MODE.CUI);

		// 設定値コンテナと計算機モデルを生成して初期化
		SettingContainer setting = null;
		CalculatorModel calculator = null;
		try {
			setting = this.createInitializedSettingContainer(debug);
			calculator = this.createInitializedCalculatorModel(setting);

		// スクリプトエンジンの接続や、設定スクリプト/ライブラリの読み込みエラーなどで失敗した場合
		} catch (RinearnProcessorNanoException e) {
			if (setting==null || setting.exceptionStackTracerEnabled) {
				MessageManager.showExceptionStackTrace(e);
			}
			return;
		}

		// 計算を実行して結果を表示
		String outputText = null;
		try {
			outputText = calculator.calculate(inputExpression, setting);
			System.out.println(outputText);

		} catch (ScriptException e) {
			String message = MessageManager.customizeExceptionMessage(e.getMessage());
			MessageManager.showErrorMessage(message, "!");
			if (setting==null || setting.exceptionStackTracerEnabled) {
				MessageManager.showExceptionStackTrace(e);
			}
		}

		// 最後に計算機モデルの終了時処理を実行
		calculator.shutdown(setting);
	}


	/**
	 * 電卓画面を起動します。
	 *
	 * @param debug デバッグモードで起動するかどうか
	 */
	public final void launchCalculatorWindow(boolean debug) {

		// 設定値コンテナと計算機モデルを生成して初期化
		SettingContainer setting = null;
		CalculatorModel calculator = null;
		try {
			setting = this.createInitializedSettingContainer(debug);
			calculator = this.createInitializedCalculatorModel(setting);

		// スクリプトエンジンの接続や、設定スクリプト/ライブラリの読み込みエラーなどで失敗した場合
		} catch (RinearnProcessorNanoException e) {
			if (setting==null || setting.exceptionStackTracerEnabled) {
				MessageManager.showExceptionStackTrace(e);
			}
			return;
		}


		// 電卓画面を生成して初期化
		ViewImpl view = new ViewImpl();
		try {
			ViewInitializer initialiser = new ViewInitializer(view, setting); // 別スレッドで初期化するためのRunnable
			SwingUtilities.invokeAndWait(initialiser);                        // それをSwingのイベントスレッドで実行

		// 初期化実行スレッドの処理待ち時の割り込みで失敗した場合など（結構異常な場合なので、リトライせず終了する）
		} catch (InvocationTargetException | InterruptedException e) {

			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(
					"Unexpected exception occurred: " + e.getClass().getCanonicalName(), "Error"
				);
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(
					"予期しない例外が発生しました: " + e.getClass().getCanonicalName(), "エラー"
				);
			}
			if (setting.exceptionStackTracerEnabled) {
				MessageManager.showExceptionStackTrace(e);
			}
			return; // この例外が発生する場合はまだUI構築が走っていないので、破棄するUIリソースはない
		}

		// UIと計算機モデルとの間でイベント処理や更新処理などを担うプレゼンターを生成し、両者を繋ぐ
		Presenter presenter = new Presenter();
		presenter.link(view, calculator, setting);
	}


	/**
	 * 設定スクリプトを実行して値を初期化済みの、設定値コンテナを生成して返します。
	 *
	 * @param debug デバッグ情報を出力するかどうか
	 * @return 初期化済みの設定値コンテナ
	 * @throws RinearnProcessorNanoException
	 * 		設定スクリプトの読み込みエラーなどで失敗した場合にスローされます。
	 */
	private final SettingContainer createInitializedSettingContainer(boolean debug)
			throws RinearnProcessorNanoException {

		SettingContainer setting = new SettingContainer();

		// 設定スクリプトを読み込む
		String settingScriptCode = null;
		settingScriptCode = CodeLoader.loadCode(
			SettingContainer.SETTING_SCRIPT_PATH, SettingContainer.SETTING_SCRIPT_ENCODING,
			LocaleCode.getDefaultLocaleCode()
		);

		// 設定スクリプトを実行して設定値を書き込む（スクリプトエンジンはメソッド内で生成）
		setting.evaluateSettingScript(
			settingScriptCode, SettingContainer.SETTING_SCRIPT_PATH,
			LIBRARY_LIST_FILE, PLUGIN_LIST_FILE, debug
		);

		return setting;
	}


	/**
	 * ライブラリスクリプトや設定値を読み込んで初期化済みの、計算機モデルを生成して返します。
	 *
	 * @return 初期化済みの計算機モデル
	 * @throws RinearnProcessorNanoException
	 * 		スクリプトエンジンの接続や、ライブラリの読み込みエラーなどで失敗した場合にスローされます。
	 */
	private final CalculatorModel createInitializedCalculatorModel(SettingContainer setting)
			throws RinearnProcessorNanoException {

		// 計算機のインスタンスを生成、初期化して返す
		CalculatorModel calculator = new CalculatorModel();
		calculator.initialize(setting, LIBRARY_LIST_FILE, PLUGIN_LIST_FILE);
		return calculator;
	}

}
