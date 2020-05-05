/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.model;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.rinearn.processornano.RinearnProcessorNanoException;
import com.rinearn.processornano.spec.LocaleCode;
import com.rinearn.processornano.spec.SettingContainer;
import com.rinearn.processornano.util.MessageManager;

public final class CalculatorModel {

	private ScriptEngine engine = null; // 計算式やライブラリの処理を実行するためのVnanoのスクリプトエンジン
	private volatile boolean calculating = false;

	// AsynchronousCalculationRunner から参照する
	public final boolean isCalculating() {
		return this.calculating;
	}

	// 初期化処理
	public final void initialize(SettingContainer setting, String libraryListFilePath, String pluginListFilePath)
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

		// ライブラリ/プラグインの読み込みリストファイルを登録
		try {
			this.engine.put("___VNANO_LIBRARY_LIST_FILE", libraryListFilePath);
			this.engine.put("___VNANO_PLUGIN_LIST_FILE", pluginListFilePath);

		// 読み込みに失敗しても、そのプラグイン/ライブラリ以外の機能には支障が無いため、本体側は落とさない。
		// そのため、例外をさらに上には投げない。（ただし失敗メッセージは表示する。）
		} catch (Exception e) {
			String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(message, "Plug-in/Library Loading Error");
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(message, "プラグイン/ライブラリ 読み込みエラー");
			}
			// プラグインエラーはスクリプトの構文エラーよりも深いエラーなため、常にスタックトレースを出力する
			System.err.println("\n" + message);
			MessageManager.showExceptionStackTrace(e);
		}

		// スクリプトエンジンに渡すオプション値マップを用意
		Map<String, Object> optionMap = new HashMap<String, Object>();
		optionMap.put("ACCELERATOR_ENABLED", setting.acceleratorEnabled);
		optionMap.put("EVAL_NUMBER_AS_FLOAT", setting.evalNumberAsFloat);
		optionMap.put("LOCALE", LocaleCode.toLocale(setting.localeCode));
		optionMap.put("DUMPER_ENABLED", setting.dumperEnabled);
		optionMap.put("DUMPER_TARGET", setting.dumperTarget);

		// スクリプトエンジンにオプションマップを設定
		engine.put("___VNANO_OPTION_MAP", optionMap);
	}

	// 終了時処理
	public void shutdown(SettingContainer setting) {
		try {
			// プラグインを接続解除し、ライブラリ登録も削除
			this.engine.put("___VNANO_COMMAND", "REMOVE_PLUGIN");
			this.engine.put("___VNANO_COMMAND", "REMOVE_LIBRARY");

		// shutdown に失敗しても上層ではどうしようも無いため、ここで通知し、さらに上には投げない。
		} catch (Exception e) {
			String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(message, "Plug-in Finalization Error");
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(message, "プラグイン終了時処理エラー");
			}
			// プラグインエラーはスクリプトの構文エラーよりも深いエラーなため、常にスタックトレースを出力する
			System.err.println("\n" + message);
			MessageManager.showExceptionStackTrace(e);
		}
	}


	// (AsynchronousCalculationListener から呼ばれて実行される)
	public final synchronized String calculate(String inputExpression, SettingContainer setting)
			throws ScriptException {

		// 計算中の状態にする（AsynchronousCalculationRunner から参照する）
		this.calculating = true;

		// ライブラリ/プラグインの再読み込み
		try {
			this.engine.put("___VNANO_COMMAND", "RELOAD_LIBRARY");
			this.engine.put("___VNANO_COMMAND", "RELOAD_PLUGIN");

		// 読み込みに失敗しても、そのプラグイン/ライブラリ以外の機能には支障が無いため、本体側は落とさない。
		// そのため、例外をさらに上には投げない。（ただし失敗メッセージは表示する。）
		} catch (Exception e) {
			String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(message, "Plug-in/Library Loading Error");
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(message, "プラグイン/ライブラリ 読み込みエラー");
			}
			// プラグインエラーはスクリプトの構文エラーよりも深いエラーなため、常にスタックトレースを出力する
			System.err.println("\n" + message);
			MessageManager.showExceptionStackTrace(e);
		}

		// 設定に応じて、まず入力フィールドの内容を正規化
		if (setting.inputNormalizerEnabled) {
			inputExpression = Normalizer.normalize(inputExpression, Normalizer.Form.NFKC);
		}

		// 入力された式を、式文のスクリプトにするため、末尾にセミコロンを追加（無い場合のみ）
		String inputScript = inputExpression;
		if (!inputScript.trim().endsWith(";")) {
			inputScript += ";";
		}

		// 計算を実行
		Object value = null;
		try {
			value = this.engine.eval(inputScript);

		// 入力した式やライブラリに誤りがあった場合は、計算終了状態に戻してから例外を上層に投げる
		} catch (ScriptException e) {
			this.calculating = false;
			throw e;
		}

		// 値が浮動小数点数なら、設定内容に応じて丸め、書式を調整
		if (value instanceof Double) {
			if ( !((Double)value).isNaN() && !((Double)value).isInfinite() ) {
				value = OutputValueFormatter.round( ((Double)value).doubleValue(), setting); // 丸め処理： 結果は BigDecimal
				value = OutputValueFormatter.simplify( (BigDecimal)value );                  // 書式調整： 結果は String
			}
		}

		// 値を文字列化
		String outputText = "";
		if (value != null) {
			outputText = value.toString();
		}

		// 計算終了状態に戻す（AsynchronousCalculationRunner から参照する）
		this.calculating = false;

		return outputText;
	}


	public final synchronized void calculateAsynchronously(
			String inputExpression, SettingContainer setting, AsynchronousCalculationListener scriptListener) {

		// 計算実行スレッドを生成して実行（中でこのクラスの calculate が呼ばれて実行される）
		AsynchronousCalculationRunner asyncCalcRunner
				= new AsynchronousCalculationRunner(inputExpression, scriptListener, this, setting);

		Thread calculatingThread = new Thread(asyncCalcRunner);
		calculatingThread.start();
	}

}
