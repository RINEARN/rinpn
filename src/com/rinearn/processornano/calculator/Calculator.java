/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.calculator;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.rinearn.processornano.RinearnProcessorNanoException;
import com.rinearn.processornano.spec.LocaleCode;
import com.rinearn.processornano.spec.SettingContainer;
import com.rinearn.processornano.ui.UIContainer;
import com.rinearn.processornano.util.MessageManager;

public final class Calculator {

	private ScriptEngine engine = null; // 計算式やライブラリの処理を実行するためのVnanoのスクリプトエンジン
	private volatile boolean running = false;
	private volatile String inputText = "";
	private volatile String outputText = "";

	public final ScriptEngine getScriptEngine() {
		return this.engine;
	}

	public final boolean isRunning() {
		return this.running;
	}

	public final synchronized void setRunning(boolean running) {
		this.running = running;
	}

	public final String getInputText() {
		return this.inputText;
	}

	public final synchronized void setInputText(String inputText) {
		this.inputText = inputText;
	}

	public final String getOutputText() {
		return this.outputText;
	}

	public final synchronized void setOutputText(String outputText) {
		this.outputText = outputText;
	}


	public final void initialize(SettingContainer setting, String libraryScriptCode)
					throws RinearnProcessorNanoException {

		// 式やライブラリの解釈/実行用に、Vnanoのスクリプトエンジンを読み込んで生成
		ScriptEngineManager manager = new ScriptEngineManager();
		this.engine = manager.getEngineByName("vnano");
		if (engine == null) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage("Please put Vnano.jar in the same directory as RinearnProcessorNano.jar.", "Engine Loading Error");
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage("Vnano.jar を RinearnProcessorNano.jar と同じフォルダ内に配置してください。", "エンジン読み込みエラー");
			}
			throw new RinearnProcessorNanoException("ScriptEngine of the Vnano could not be loaded.");
		}

		// スクリプトエンジンに渡すオプション値マップを用意
		Map<String, Object> optionMap = new HashMap<String, Object>();
		optionMap.put("ACCELERATOR", setting.acceleratorEnabled);
		optionMap.put("EVAL_NUMBER_AS_FLOAT", setting.evalNumberAsFloat);
		optionMap.put("LIBRARY_SCRIPT_NAME", setting.libraryScriptPath);
		optionMap.put("LIBRARY_SCRIPT_CODE", libraryScriptCode);
		optionMap.put("LOCALE", LocaleCode.toLocale(setting.localeCode));

		// スクリプトエンジンにオプションマップを設定
		engine.put("VNANO_OPTION", optionMap);
	}


	public final synchronized Object calculate(String scriptCode, SettingContainer setting)
			throws ScriptException {

		// 入力された式を式文にするために末尾にセミコロンを追加（無い場合のみ）
		if (!scriptCode.trim().endsWith(";")) {
			scriptCode += ";";
		}

		// 計算を実行
		Object value = this.engine.eval(scriptCode);

		// 値が浮動小数点数なら、設定内容に応じて丸める
		if (value instanceof Double) {
			value = Rounder.round( ((Double)value).doubleValue(), setting); // 型は BigDecimal になる
		}

		return value;
	}


	public final synchronized void requestCalculation(UIContainer ui, SettingContainer setting,
			AsynchronousScriptListener scriptListener) {

		// 設定に応じて、まず入力フィールドの内容を正規化
		if (setting.inputNormalizerEnabled) {
			ui.inputField.setText(
				Normalizer.normalize(ui.inputField.getText(), Normalizer.Form.NFKC)
			);
		}

		// 入力フィールドの内容を取得してスクリプト実行をリクエストする
		this.inputText = ui.inputField.getText();

		// 入力された式を式文にするために末尾にセミコロンを追加（無い場合のみ）し、
		// 実行するスクリプトコードの内容としてセット
		String scriptCode = this.inputText;
		if (!scriptCode.trim().endsWith(";")) {
			scriptCode += ";";
		}

		// スクリプト実行スレッドを生成して実行
		AsynchronousScriptRunner asyncScriptRunner
				= new AsynchronousScriptRunner(scriptCode, scriptListener, this, setting);
		Thread scriptingThread = new Thread(asyncScriptRunner);
		scriptingThread.start();
	}

}
