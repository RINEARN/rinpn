/*
 * A simple example to embed the script engine of the RINPn (Vnano Engine) into
 * an application, and calculate "1.2 + 3.4" under the same settings with the RINPn.
 * RINPn のスクリプトエンジン（Vnanoエンジン）をアプリケーション内に組み込み、
 * RINPn 同様のエンジン設定で式「 1.2 + 3.4 」の値を計算する単純なサンプルコード
 * --------------------------------------------------------------------------------
 * This file is released under CC0.
 * Written in 2020 by RINEARN (Fumihiro Matsui)
 * --------------------------------------------------------------------------------
 * 
 * Preparing - 事前準備
 * 
 * How to compile this code - このコードのコンパイル方法 :
 * 
 *     javac EmbedUseExample.java -encoding UTF-8
 * 
 * 
 * How to execute this code - このコードの実行方法 :
 * 
 *     java -cp ".;Vnano.jar" EmbedUseExample
 * 
 *         or, (depending on your environment)
 * 
 *     java -cp ".:Vnano.jar" EmbedUseExample
 * 
 * 
 * Expected result - 正常な実行結果 :
 * 
 *     "output: 4.6"
 * 
 */

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;
import java.util.HashMap;

public class EmbedUseExample {

	public static void main(String[] args) {

		// Get the script engine (Vnano Engine) used in the RINPn for calculations.
		// RINPn で計算に用いているスクリプトエンジン（Vnano Engine）を取得
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("vnano");
		if (engine == null) {
			System.err.println("Script engine loading error");
			return;
		}

		// Set options of the script engine
		// スクリプトエンジンのオプション設定
		Map<String, Object> optionMap = new HashMap<String, Object>();
		optionMap.put("EVAL_NUMBER_AS_FLOAT", true);  // See: Setting.vnano: evalNumberAsFloat
		optionMap.put("EVAL_ONLY_FLOAT",      true);  // See: Setting.vnano: evalOnlyFloat
		optionMap.put("EVAL_ONLY_EXPRESSION", true);  // See: Setting.vnano: evalOnlyExpression
		optionMap.put("TERMINAL_IO_UI", "CUI");       // For CUI applications
		// optionMap.put("TERMINAL_IO_UI", "GUI");    // For GUI applications
		engine.put("___VNANO_OPTION_MAP", optionMap);

		// Settings for loading libraries/plugins
		// ライブラリ/プラグインの読み込み設定
		engine.put("___VNANO_LIBRARY_LIST_FILE", "lib/VnanoLibraryList.txt");
		engine.put("___VNANO_PLUGIN_LIST_FILE", "plugin/VnanoPluginList.txt");


		// Calculate the value of "1.2 + 3.4" by the script engine, and output
		// スクリプトエンジンで「 1.2 + 3.4 」の値を計算して表示
		try{
			String input = "1.2 + 3.4";
			double x = (double) engine.eval(input + ";");
			System.out.println("output: " + x);

		} catch (ScriptException e) {
			e.printStackTrace();
		}


		// Unload libraries/plugins
		// ライブラリ/プラグインの登録/接続解除
		engine.put("___VNANO_COMMAND", "REMOVE_LIBRARY");
		engine.put("___VNANO_COMMAND", "REMOVE_PLUGIN");
	}
}
