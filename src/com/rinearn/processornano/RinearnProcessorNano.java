/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

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


	public static void main(String[] args) {

		// 引数が何も無い場合は電卓画面を起動
		if (args.length == 0) {
			new RinearnProcessorNano().launchCalculatorWindow();

		// 引数がバージョン出力オプションだった場合は、バージョンを表示して終了
		} else if (args.length == 1 && args[0].equals(OPTION_NAME_VERSION)) {
			printVersion();

		// それ以外の引数は式と見なして、電卓画面を起動せずに計算を実行（コマンドライン用）
		} else if (args.length == 1) {

			new RinearnProcessorNano().calculate(args[0]);

		// 引数が多すぎる場合はエラー
		} else {
			if (LocaleCode.getDefaultLocaleCode().equals(LocaleCode.JA_JP)) {
				System.err.println("コマンドライン引数の数が多すぎます。");
			}
			if (LocaleCode.getDefaultLocaleCode().equals(LocaleCode.EN_US)) {
				System.err.println("Too many command-line arguments.");
			}
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
	 */
	public final void calculate(String inputExpression) {

		// メッセージの出力をコマンドラインモードに変更
		MessageManager.setDisplayType(MessageManager.DISPLAY_MODE.CUI);

		// 設定値コンテナと計算機モデルを生成して初期化
		SettingContainer setting = null;
		CalculatorModel calculator = null;
		try {
			setting = this.createInitializedSettingContainer();
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
	}


	/**
	 * 電卓画面を起動します。
	 */
	public final void launchCalculatorWindow() {

		// 設定値コンテナと計算機モデルを生成して初期化
		SettingContainer setting = null;
		CalculatorModel calculator = null;
		try {
			setting = this.createInitializedSettingContainer();
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
	 * @return 初期化済みの設定値コンテナ
	 * @throws RinearnProcessorNanoException
	 * 		設定スクリプトの読み込みエラーなどで失敗した場合にスローされます。
	 */
	private final SettingContainer createInitializedSettingContainer()
			throws RinearnProcessorNanoException {

		SettingContainer setting = new SettingContainer();

		// 設定スクリプトを読み込む
		String settingScriptCode = null;
		settingScriptCode = CodeLoader.loadCode(
			SettingContainer.SETTING_SCRIPT_PATH, SettingContainer.SETTING_SCRIPT_ENCODING,
			LocaleCode.getDefaultLocaleCode()
		);

		// 設定スクリプトを実行して設定値を書き込む（スクリプトエンジンはメソッド内で生成）
		setting.evaluateSettingScript(settingScriptCode, SettingContainer.SETTING_SCRIPT_PATH);

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

		// ライブラリの配置フォルダからファイル一覧を取得（フォルダの存在は SettingContainer 内で検査済み）
		File libraryDir = new File(setting.libraryFolder);
		File[] files = libraryDir.listFiles();

		// ライブラリのスクリプトコード内容やファイル名を一時的に格納するリストを用意
		List<String> libraryScriptList = new LinkedList<String>();
		List<String> libraryNameList = new LinkedList<String>();

		// 拡張子が「 .vnano 」のファイルを全て読み込んでリストに格納し、最後に配列として取り出す
		for (File file: files) {
			if (file.getPath().endsWith(setting.libraryExtension)) {
				String script = CodeLoader.loadCode(
					file.getPath(), setting.libraryEncoding, setting.localeCode
				);
				libraryScriptList.add(script);
				libraryNameList.add(file.getName());
			}
		}
		String[] libraryScripts = libraryScriptList.toArray(new String[0]);
		String[] libraryNames = libraryNameList.toArray(new String[0]);

		// 計算機のインスタンスを生成、初期化して返す
		CalculatorModel calculator = new CalculatorModel();
		calculator.initialize(setting, libraryScripts, libraryNames);
		return calculator;
	}

}
