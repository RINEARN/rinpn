/*
 * Copyright(C) 2019-2021 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.model;

import java.io.File;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.rinearn.processornano.RinearnProcessorNanoException;
import com.rinearn.processornano.RinearnProcessorNanoFatalException;
import com.rinearn.processornano.util.LocaleCode;
import com.rinearn.processornano.util.MessageManager;
import com.rinearn.processornano.util.ScriptFileLoader;
import com.rinearn.processornano.util.SettingContainer;

public final class CalculatorModel {

	private static final String SCRIPT_EXTENSION = ".vnano";
	private static final String DEFAULT_SCRIPT_ENCODING = "UTF-8";
	private static final String DEFAULT_FILE_IO_ENCODING = "UTF-8";

	private ScriptEngine engine = null; // 計算式やライブラリの処理を実行するためのVnanoのスクリプトエンジン
	private String dirPath = ".";

	private volatile boolean calculating = false;

	// スクリプト内から下記の組み込み関数「 output 」を呼んで渡した値を控えておくフィールド（GUIモードでの表示用）
	private String lastOutputContent = null;

	// スクリプトエンジンに組み込み関数「 output 」を提供するプラグインクラス
	public class OutputPlugin {

		private boolean isGuiMode;
		private SettingContainer setting;
		public OutputPlugin(SettingContainer setting, boolean isGuiMode) {
			this.setting = setting;
			this.isGuiMode = isGuiMode;
		}

		public void output(String value) {

			// GUIモード用に値を控える
			CalculatorModel.this.lastOutputContent = value;

			// CUIモード用に値を標準出力に出力する
			if (!this.isGuiMode) {
				System.out.println(value);
			}
		}
		public void output(long value) {
			this.output( Long.toString(value) );
		}
		public void output(double value) {

			// 設定内容に応じて丸め、書式を調整
			if ( !((Double)value).isNaN() && !((Double)value).isInfinite() ) {
				BigDecimal roundedValue = OutputValueFormatter.round( ((Double)value).doubleValue(), this.setting);
				String simplifiedValue = OutputValueFormatter.simplify( roundedValue );
				this.output(simplifiedValue);

			} else {
				this.output( Double.toString(value) );
			}
		}
		public void output(boolean value) {
			this.output( Boolean.toString(value) );
		}
	}


	// AsynchronousCalculationRunner や ExitButtonListener から参照する
	public final boolean isCalculating() {
		return this.calculating;
	}


	// 初期化処理
	public final void initialize(
			SettingContainer setting, boolean isGuiMode, String dirPath, String libraryListFilePath, String pluginListFilePath)
					throws RinearnProcessorNanoException {

		this.dirPath = dirPath;

		// 式やライブラリの解釈/実行用に、Vnanoのスクリプトエンジンを読み込んで生成
		ScriptEngineManager manager = new ScriptEngineManager();
		this.engine = manager.getEngineByName("vnano");
		if (engine == null) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(
					"Please put Vnano.jar in the same directory as RinearnProcessorNano.jar.",
					"Engine Loading Error", setting.localeCode
				);
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(
					"Vnano.jar を RinearnProcessorNano.jar と同じフォルダ内に配置してください。",
					"エンジン読み込みエラー", setting.localeCode
				);
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
			String message = e.getMessage();
			if (e.getCause() != null && e.getCause().getMessage() != null) {
					message = e.getCause().getMessage();
			}
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(message, "Plug-in/Library Loading Error", setting.localeCode);
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(message, "プラグイン/ライブラリ 読み込みエラー", setting.localeCode);
			}
			// プラグインエラーはスクリプトの構文エラーよりも深いエラーなため、常にスタックトレースを出力する
			System.err.println("\n" + message);
			MessageManager.showExceptionStackTrace(e, setting.localeCode);
		}

		// 組み込み関数「 output 」を提供するプラグイン（このクラス内に内部クラスとして実装）を登録
		this.engine.put("OutputPlugin", new CalculatorModel.OutputPlugin(setting, isGuiMode));

		// プラグインからのパーミッション要求の扱いを設定するため、パーミッションの項目名と値を格納するマップを用意
		Map<String, String> permissionMap = new HashMap<String, String>();
		permissionMap.put("DEFAULT", "ASK");           // 全パーミッション項目のデフォルト挙動を、ユーザーに尋ねて決める挙動に設定（ASK）
		//permissionMap.put("DEFAULT", "DENY");        // デフォルト挙動を、何も訪ねずに拒否する挙動（DENY）にしたい場合はこちら
		permissionMap.put("FILE_READ", "ALLOW");       // ファイルの読み込みは、電卓ソフト的には安全なので許可（ALLOW）
		permissionMap.put("DIRECTORY_LIST", "ALLOW");  // フォルダ内のファイル一覧取得も、電卓ソフト的には安全なので許可（ALLOW）
		permissionMap.put("PROGRAM_EXIT", "ALLOW");    // exit 関数によるスクリプトの終了も、電卓ソフト的には安全なので許可（ALLOW）
		////////// 他に、個別に別挙動に設定したいパーミッション項目があれば、項目名をキーとしてここで続けて put する
		////////// (とりあえず現状では設定ファイルから読み込んだりはしない方針)

		// パーミッション設定を反映させる
		this.engine.put("___VNANO_PERMISSION_MAP", permissionMap);
	}


	// 終了時処理
	public void shutdown(SettingContainer setting) {
		try {
			// プラグインを接続解除し、ライブラリ登録も削除
			this.engine.put("___VNANO_COMMAND", "REMOVE_PLUGIN");
			this.engine.put("___VNANO_COMMAND", "REMOVE_LIBRARY");

		// shutdown に失敗しても上層ではどうしようも無いため、ここで通知し、さらに上には投げない。
		} catch (Exception e) {
			String message = e.getMessage();
			if (e.getCause() != null && e.getCause().getMessage() != null) {
					message = e.getCause().getMessage();
			}
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(message, "Plug-in Finalization Error", setting.localeCode);
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(message, "プラグイン終了時処理エラー", setting.localeCode);
			}
			// プラグインエラーはスクリプトの構文エラーよりも深いエラーなため、常にスタックトレースを出力する
			System.err.println("\n" + message);
			MessageManager.showExceptionStackTrace(e, setting.localeCode);
		}
	}


	// CUIモードでは RinearnProcessorNano.calculate、GUIモードでは AsynchronousCalculationRunner.run から呼ばれて実行される
	public final String calculate(String inputtedContent, boolean isGuiMode, SettingContainer setting)
			throws ScriptException, RinearnProcessorNanoException {

		// 注意:
		// このメソッドを synchronized にすると、スクリプト内容が重い場合に Enter キーや実行ボタンが連打された場合、
		// 処理がどんどん積もっていって全部消化されるまで待たなければいけなくなってしまう。従って synchronized は付けない。
		// 代わりに、GUIモードにおけるこのメソッドの呼び出し元である AsynchronousCalculationRunner 側で、
		// isCalculating() を呼んで現在実行中かどうか検査し、実行中なら追加の計算リクエストを弾くようにする。

		if (this.calculating) {
			// それでも追加の計算リクエストが来た場合は、呼び出し元での検査不備や処理フローの不備なので Fatal エラー扱いにする
			throw new RinearnProcessorNanoFatalException("The previous calculation has not finished yet");
		}

		// 計算中の状態にする（AsynchronousCalculationRunner から参照する）
		this.calculating = true;

		// 入力が空の場合は、何もせず空の出力を返す
		//（そうしないと、入力が式文ではないため評価結果の値が無いし、オプションによっては入力が式文ではない時点でエラーになる）
		if (inputtedContent.trim().length() == 0) {
			calculating = false;
			return "";
		}

		// スクリプト内から output 関数に渡した内容を控える変数をクリア
		this.lastOutputContent = null;

		// 入力内容がスクリプトかどうか、およびスクリプト名を控える
		boolean scriptFileInputted = false;  // スクリプトの場合は true, 計算式の場合は false
		File scriptFile = null;

		// 前後の空白やダブルクォーテーションを詰めた内容を用意（内容の判定で使用）
		String trimmedContent = inputtedContent.trim();
		if (trimmedContent.startsWith("\"") && trimmedContent.endsWith("\"")) {
			trimmedContent = trimmedContent.substring(1, trimmedContent.length()-1);
		}

		// 詰めた入力内容がスクリプトの拡張子で終わっている場合は、実行対象スクリプトファイルのパスと見なす
		// ( ファイルパスはダブルクォーテーションで囲われている場合もある )
		if (trimmedContent.endsWith(SCRIPT_EXTENSION)) {
			scriptFileInputted = true;
			scriptFile = new File(trimmedContent);

			// 指定内容がフルパスでなかった場合は、dirPath のディレクトリ基準の相対パスと見なす
			if (!scriptFile.isAbsolute()) {
				scriptFile = new File(dirPath, scriptFile.getPath());
			}

			// 入力内容をスクリプトファイルの内容で置き換え
			try {
				inputtedContent = ScriptFileLoader.load(scriptFile.getAbsolutePath(), DEFAULT_SCRIPT_ENCODING, setting);
			} catch (RinearnProcessorNanoException e) {
				this.calculating = false;
				throw e;
			}

			// 設定の一部をスクリプト用に書き換え（整数をfloatと見なすオプションなどは、式の計算には良くても、スクリプトの場合は不便なので）
			try {
				setting = setting.clone();
				setting.evalIntLiteralAsFloat = false;
				setting.evalOnlyFloat = false;
				setting.evalOnlyExpression = false;
			} catch (CloneNotSupportedException e) {
				this.calculating = false;
				throw new RinearnProcessorNanoException(e);
			}

		// それ以外は計算式と見なす
		} else {

			// 式の記述内容を設定に応じて正規化（全角を半角にしたりなど）
			if (setting.inputNormalizerEnabled) {
				inputtedContent = Normalizer.normalize(inputtedContent, Normalizer.Form.NFKC);
			}

			// 末尾にセミコロンを追加（無い場合のみ）
			if (!inputtedContent.trim().endsWith(";")) {
				inputtedContent += ";";
			}
		}


		// ライブラリ/プラグインの再読み込み
		try {
			if (setting.reloadLibrary) {
				this.engine.put("___VNANO_COMMAND", "RELOAD_LIBRARY");
			}
			if (setting.reloadPlugin) {
				this.engine.put("___VNANO_COMMAND", "RELOAD_PLUGIN");
			}

		// 読み込みに失敗しても、そのプラグイン/ライブラリ以外の機能には支障が無いため、本体側は落とさない。
		// そのため、例外をさらに上には投げない。（ただし失敗メッセージは表示する。）
		} catch (Exception e) {
			String message = e.getMessage();
			if (e.getCause() != null && e.getCause().getMessage() != null) {
					message = e.getCause().getMessage();
			}
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(message, "Plug-in/Library Loading Error", setting.localeCode);
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(message, "プラグイン/ライブラリ 読み込みエラー", setting.localeCode);
			}
			// プラグインエラーはスクリプトの構文エラーよりも深いエラーなため、常にスタックトレースを出力する
			System.err.println("\n" + message);
			MessageManager.showExceptionStackTrace(e, setting.localeCode);
		}


		// スクリプトエンジン関連の設定値を Map（オプションマップ）に格納し、エンジンに渡して設定
		Map<String, Object> optionMap = new HashMap<String, Object>();
		optionMap.put("ACCELERATOR_ENABLED", setting.acceleratorEnabled);
		optionMap.put("ACCELERATOR_OPTIMIZATION_LEVEL", setting.acceleratorOptimizationLevel);
		optionMap.put("EVAL_INT_LITERAL_AS_FLOAT", setting.evalIntLiteralAsFloat);
		optionMap.put("EVAL_ONLY_FLOAT", setting.evalOnlyFloat);
		optionMap.put("EVAL_ONLY_EXPRESSION", setting.evalOnlyExpression);
		optionMap.put("LOCALE", LocaleCode.toLocale(setting.localeCode));
		optionMap.put("DUMPER_ENABLED", setting.dumperEnabled);
		optionMap.put("DUMPER_TARGET", setting.dumperTarget);
		optionMap.put("UI_MODE", isGuiMode ? "GUI" : "CUI");
		optionMap.put("FILE_IO_ENCODING", DEFAULT_FILE_IO_ENCODING);
		if (scriptFileInputted) {
			optionMap.put("MAIN_SCRIPT_NAME", scriptFile.getName());
			optionMap.put("MAIN_SCRIPT_DIRECTORY", scriptFile.getParentFile().getAbsolutePath());
		}
		engine.put("___VNANO_OPTION_MAP", optionMap);


		// スクリプトエンジンで計算処理を実行
		Object value = null;
		try {
			value = this.engine.eval(inputtedContent);

		// 入力した式やライブラリに誤りがあった場合は、計算終了状態に戻してから例外を上層に投げる
		} catch (ScriptException e) {
			this.calculating = false;
			throw e;
		}

		// このメソッドの戻り値（出力フィールドに表示される文字列）を格納する
		String outputText = null;

		// スクリプトファイルを実行した場合は、スクリプト内から組み込み関数「 output 」を呼んで渡した内容が
		// このクラスの lastOutputContent フィールドに保持されている（内部クラス OutputPlugin 参照）ので、
		// GUIモードではそれを出力フィールドに表示するために返す。
		// CUIモードでは逐次的に標準出力に出力済みなのでもう何も追加出力する必要は無く、従って何も返さない。
		if (scriptFileInputted) {
			if (isGuiMode) {
				outputText = this.lastOutputContent;
			}

		// 計算式を実行した場合は、その式の値を丸めた上で文字列化して出力する
		} else {
			if (value instanceof Double) {
				if ( !((Double)value).isNaN() && !((Double)value).isInfinite() ) {
					value = OutputValueFormatter.round( ((Double)value).doubleValue(), setting); // 丸め処理： 結果は BigDecimal
					value = OutputValueFormatter.simplify( (BigDecimal)value );                  // 書式調整： 結果は String
				}
			}
			if (value != null) {
				value = value.toString();
			}
			outputText = (String)value;
		}

		// 計算終了状態に戻す（AsynchronousCalculationRunner から参照する）
		this.calculating = false;

		return outputText;
	}
}
