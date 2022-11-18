/*
 * Copyright(C) 2019-2022 RINEARN (Fumihiro Matsui)
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


public final class RINPn {

	private static final String VERSION = "0.9.5";
	private static final String OPTION_NAME_VERSION = "--version";
	private static final String OPTION_NAME_DEBUG = "--debug";
	private static final String OPTION_NAME_DIR = "--dir";
	private static final String LIBRARY_LIST_FILE = "lib/VnanoLibraryList.txt";
	private static final String PLUGIN_LIST_FILE = "plugin/VnanoPluginList.txt";


	public static void main(String[] args) {

		// コマンドラインから渡された内容を控える変数
		String inputtedContent = null;     // 計算対象（計算式またはスクリプト名）
		boolean debugEnabled = false; // デバッグモードが有効かどうか
		String dirPath = ".";         // スクリプトの読み込み基準ディレクトリのパス

		// 引数の解釈
		int argLength = args.length;
		int argIndex = 0;
		while (argIndex < argLength) {

			// 引数がバージョン出力オプションだった場合は、バージョンを表示
			if (args[argIndex].equals(OPTION_NAME_VERSION)) {
				printVersion();
				return;

			// 引数がデバッグオプションだった場合は、デバッグモードを有効化
			} else if (args[argIndex].equals(OPTION_NAME_DEBUG)) {
				debugEnabled = true;
				argIndex++;

			} else if (args[argIndex].equals(OPTION_NAME_DIR)) {
				if (argLength <= argIndex + 1) {
					// ここのエラーメッセージのロケールは、理想的には設定ファイルから取得したいが、そうすると設定の読み込みタイミングを変える必要がある。後々で要検討
					if (LocaleCode.getDefaultLocaleCode().equals(LocaleCode.JA_JP)) {
						System.err.println("オプション「 " + OPTION_NAME_DIR + " 」の後に値が必要です。");
					}
					if (LocaleCode.getDefaultLocaleCode().equals(LocaleCode.EN_US)) {
						System.err.println("An option value is required after \"" + OPTION_NAME_DIR + "\".");
					}
				}
				dirPath = args[argIndex + 1];
				argIndex += 2;

			// それ以外の引数は計算式と見なすので、後で計算するために控える
			} else {
				if (inputtedContent == null) {
					inputtedContent = args[argIndex];

				// 既に計算式が控えられている場合は、引数が多すぎるのでエラー
				} else {
					// ここのエラーメッセージのロケールは、理想的には設定ファイルから取得したいが、そうすると設定の読み込みタイミングを変える必要がある。後々で要検討
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

		// 計算式が渡されなかった場合は電卓画面を起動
		if (inputtedContent == null) {
			new RINPn().launchCalculatorWindow(dirPath, debugEnabled);

		// 計算式が渡された場合はCUIモードで計算（結果はコマンドラインに表示）
		} else {
			new RINPn().calculate(inputtedContent, dirPath, debugEnabled);
		}
	}

	private static void printVersion() {
		System.out.print("RINPn Ver." + VERSION + " ");

		// Vnanoのバージョンも表示
		System.out.print(" / with " + org.vcssl.nano.spec.EngineInformation.LANGUAGE_NAME);
		System.out.print(" Ver." + org.vcssl.nano.spec.EngineInformation.LANGUAGE_VERSION);
		System.out.println("");
	}


	public RINPn() {
		// 電卓画面の起動は、インスタンス生成後に明示的に launchCalculatorWindow() を呼ぶ
	}


	/**
	 * 電卓画面を起動せずに、計算を実行し、結果を標準出力に表示します。
	 *
	 * @param inputtedContent 計算対象（計算式またはスクリプト名）
	 * @param dirPath スクリプトの読み込み基準ディレクトリのパス
	 * @param debug デバッグ情報を出力するかどうか
	 */
	public final void calculate(String inputtedContent, String dirPath, boolean debug) {

		// メッセージの出力をコマンドラインモードに変更
		MessageManager.setDisplayType(MessageManager.DISPLAY_MODE.CUI);

		// 設定値コンテナと計算機モデルを生成して初期化
		SettingContainer setting = null;
		Model model = null;
		try {
			setting = this.createInitializedSettingContainer(false, debug);
			model = this.createInitializedCalculatorModel(dirPath, false, setting);

		// スクリプトエンジンの接続や、設定スクリプト/ライブラリの読み込みエラーなどで失敗した場合
		} catch (RINPnException e) {
			if (setting==null || setting.exceptionStackTracerEnabled) {
				String localeCode = (setting==null) ? LocaleCode.getDefaultLocaleCode() : setting.localeCode;
				MessageManager.showExceptionStackTrace(e, localeCode);
			}
			return;
		}

		// 計算を実行して結果を表示
		String outputText = null;
		try {
			outputText = model.calculate(inputtedContent, false, setting);
			if (outputText != null) {
				System.out.println(outputText);
			}

		} catch (VnanoException | VnanoFatalException | RINPnException | RINPnFatalException e) {
			String message = MessageManager.customizeExceptionMessage(e.getMessage());
			MessageManager.showErrorMessage(message, "!", setting.localeCode);
			if (setting.exceptionStackTracerEnabled) {
				MessageManager.showExceptionStackTrace(e, setting.localeCode);
			}
		}

		// 最後に計算機モデルの終了時処理を実行
		model.shutdown(setting);
	}


	/**
	 * 電卓画面を起動します。
	 *
	 * @param debug デバッグモードで起動するかどうか
	 */
	public final void launchCalculatorWindow(String dirPath, boolean debug) {

		// 設定値コンテナと計算機モデルを生成して初期化
		SettingContainer setting = null;
		Model calculator = null;
		try {
			setting = this.createInitializedSettingContainer(true, debug);
			calculator = this.createInitializedCalculatorModel(dirPath, true, setting);

		// スクリプトエンジンの接続や、設定スクリプト/ライブラリの読み込みエラーなどで失敗した場合
		} catch (RINPnException e) {
			if (setting==null || setting.exceptionStackTracerEnabled) {
				String localeCode = (setting==null) ? LocaleCode.getDefaultLocaleCode() : setting.localeCode;
				MessageManager.showExceptionStackTrace(e, localeCode);
			}
			return;
		}


		// 電卓画面を生成して初期化
		View view = new View();
		try {
			view.initialize(setting);

		// 初期化実行スレッドの処理待ち時の割り込みで失敗した場合など（結構異常な場合なので、リトライせず終了する）
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
	 * @throws RINPnException
	 * 		設定スクリプトの読み込みエラーなどで失敗した場合にスローされます。
	 */
	private final SettingContainer createInitializedSettingContainer(boolean isGuiMode, boolean debug)
			throws RINPnException {

		SettingContainer setting = new SettingContainer();

		// 設定ファイルは、拡張子 .txt と .vnano のどちらか存在する方（vnano優先）が読まれる
		//（新規導入環境での開きやすさを確保するため、標準では .txt で、.vnano に変えても参照される、という仕様）
		String settingScriptPath = SettingContainer.SETTING_SCRIPT_PATH_VNANO;
		if (!new File(settingScriptPath).exists()) {
			settingScriptPath = SettingContainer.SETTING_SCRIPT_PATH_TXT;

			// それでもファイルが存在しない場合はエラー
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

		// 設定スクリプトを実行して設定値を書き込む（スクリプトエンジンはメソッド内で生成）
		setting.evaluateSettingScript(
			settingScriptPath, LIBRARY_LIST_FILE, PLUGIN_LIST_FILE, isGuiMode, debug
		);

		return setting;
	}


	/**
	 * ライブラリスクリプトや設定値を読み込んで初期化済みの、計算機モデルを生成して返します。
	 *
	 * @return 初期化済みの計算機モデル
	 * @throws RINPnException
	 * 		スクリプトエンジンの接続や、ライブラリの読み込みエラーなどで失敗した場合にスローされます。
	 */
	private final Model createInitializedCalculatorModel(String dirPath, boolean isGuiMode, SettingContainer setting)
			throws RINPnException {

		// 計算機のインスタンスを生成、初期化して返す
		Model model = new Model();
		model.initialize(setting, isGuiMode, dirPath, LIBRARY_LIST_FILE, PLUGIN_LIST_FILE);
		return model;
	}

}
