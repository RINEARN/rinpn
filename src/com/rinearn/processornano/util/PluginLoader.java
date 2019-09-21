/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.rinearn.processornano.RinearnProcessorNanoException;
import com.rinearn.processornano.spec.LocaleCode;


/**
 * このソフトウェアのビルド単位には含まれない、別途コンパイルされたプラグインのクラスファイルを、
 * 動的に読み込んでインスタンス化するプラグインローダーです。
 */
public final class PluginLoader {

	private String localeCode = null;
	URLClassLoader classLoader = null;


	/**
	 * エラーメッセージ等に使用するロケールを指定して、プラグインローダを生成します。
	 *
	 * @param localeCode エラーメッセージの言語を指定するロケールコード
	 */
	public PluginLoader(String localeCode) {
		this.localeCode = localeCode;
	}


	/**
	 * プラグインを検索する基準パス（フォルダのパスやJarファイルのパスなど）を指定し、
	 * アクセス可能な状態として開きます。
	 *
	 * この操作は、{@link loadPlugin(String pluginName)} メソッドによってプラグインを読み込む前に、
	 * 必ず行う必要があります。
	 *
	 * @param loadingBasePaths プラグインを検索する基準バス
	 * @throws RinearnProcessorNanoException 基準パスに問題があった場合にスローされます。
	 */
	public final void open(String[] loadingBasePaths)
			throws RinearnProcessorNanoException{

		// プラグイン検索場所をURL配列に変換
		int basePathsLength = loadingBasePaths.length;
		URL[] baseURLs = new URL[basePathsLength];
		for (int baseIndex=0; baseIndex<basePathsLength; baseIndex++) {
			try {

				baseURLs[baseIndex] = new File(loadingBasePaths[baseIndex]).toURI().toURL();

			} catch (MalformedURLException e) {
				if (localeCode.equals(LocaleCode.JA_JP)) {
					MessageManager.showErrorMessage(
						"プラグイン読み込み場所「" + loadingBasePaths[baseIndex] + "」を正常に解釈できません。",
						"プラグイン読み込みエラー");
				}
				if (localeCode.equals(LocaleCode.EN_US)) {
					MessageManager.showErrorMessage(
						"The plug-in loading path \"" + loadingBasePaths[baseIndex] + "\" is invalid.",
						"Plugin Loading Error"
					);
				}
				throw new RinearnProcessorNanoException(e);
			}
		}
		this.classLoader = new URLClassLoader(baseURLs);
	}


	/**
	 * プラグインを検索するリソースを明示的に閉じます。
	 *
	 * この操作により、プラグインを読み込んだクラスローダも、新しいクラスを読み込めないように閉じられます。
	 * ただし、プラグインそのものの読み込みが完了しても、
	 * その依存クラスが暗黙的に読み込まれるタイミングは、一般にずっと後になり得る事に留意してください。
	 * 依存クラスが暗黙的に読み込まれる際に、既にこのメソッドによってクラスローダが閉じていると、
	 * その読み込みは失敗します。
	 *
	 * @throws RinearnProcessorNanoException 閉じるのに失敗した場合にスローされます。
	 */
	public final void close() throws RinearnProcessorNanoException {
		try {
			this.classLoader.close();
		} catch (IOException e) {
			throw new RinearnProcessorNanoException(e);
		}
	}


	/**
	 * プラグインを読み込み、インスタンス化して返します。
	 *
	 * このメソッドを使用する前に、{@link open(String[] loadingBasePaths)} メソッドによって、
	 * プラグインを検索する基準パスが、アクセス可能な状態に開かれている必要があります。
	 *
	 * @param pluginName プラグイン名（基準パスから見た相対パスをドット区切りで指定、拡張子は不要）
	 * @return プラグインのインスタンス
	 * @throws RinearnProcessorNanoException 読み込みに失敗した場合にスローされます。
	 */
	public Object loadPlugin(String pluginName)
			throws RinearnProcessorNanoException {

		if (this.classLoader == null) {
			if (localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(
					"このプラグインローダには、プラグイン検索パスが指定されていません。",
					"プラグイン読み込みエラー");
			}
			if (localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(
					"Plug-in base paths have not been set on this plugin loader.",
					"Plugin Loading Error"
				);
			}
			throw new RinearnProcessorNanoException("Plug-in base paths have not been set.");
		}

		// URLクラスローダでプラグインのクラスを読み込む
		Class<?> pluginClass = null;
		try {

			pluginClass = this.classLoader.loadClass(pluginName);

		} catch (ClassNotFoundException e) {
			if (localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(
					"プラグイン「" + pluginName + "」を読み込めませんでした。",
					"プラグイン読み込みエラー");
			}
			if (localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(
					"The specified plug-in \"" + pluginName + "\" could not be loaded.",
					"Plugin Loading Error"
				);
			}
			throw new RinearnProcessorNanoException(e);
		}

		// 読み込んだクラスからプラグインのインスタンスを生成
		Object pluginInstance = null;
		try {

			pluginInstance = pluginClass.getDeclaredConstructor().newInstance();

		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {

			if (localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(
					"プラグイン「" + pluginName + "」をインスタンス化できませんでした。",
					"プラグイン読み込みエラー");
			}
			if (localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(
					"The specified plug-in \"" + pluginName + "\" could not be instantiated.",
					"Plugin Loading Error"
				);
			}
			throw new RinearnProcessorNanoException(e);
		}

		return pluginInstance;

	}
}
