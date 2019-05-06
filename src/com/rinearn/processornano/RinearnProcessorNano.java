/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano;

import javax.script.ScriptException;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.rinearn.processornano.calculator.Calculator;
import com.rinearn.processornano.event.EventListenerManager;
import com.rinearn.processornano.spec.LocaleCode;
import com.rinearn.processornano.spec.SettingContainer;
import com.rinearn.processornano.util.CodeLoader;
import com.rinearn.processornano.util.MessageManager;
import com.rinearn.processornano.view.ViewContainer;
import com.rinearn.processornano.view.ViewInitializer;


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


		// 電卓画面を生成して初期化
		ViewContainer view = new ViewContainer();
		try {
			ViewInitializer initialiser = new ViewInitializer(view, setting); // 別スレッドで初期化するためのRunnable
			SwingUtilities.invokeAndWait(initialiser);                        // それをSwingのイベントスレッドで実行

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
		EventListenerManager.addAllEventListenersToUI(view, calculator, setting);
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
	 * ライブラリスクリプトや設定値を読み込んで初期化済みの、計算機を生成して返します。
	 *
	 * @return 初期化済みの計算機
	 * @throws RinearnProcessorNanoException
	 * 		スクリプトエンジンの接続や、ライブラリの読み込みエラーなどで失敗した場合にスローされます。
	 */
	private final Calculator createInitializedCalculator(SettingContainer setting)
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
		Calculator calculator = new Calculator();
		calculator.initialize(setting, libraryScripts, libraryNames);
		return calculator;
	}

}
