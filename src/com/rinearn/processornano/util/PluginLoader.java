/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.util;

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

	/**
	 * プラグインを読み込み、インスタンス化して返します。
	 *
	 * @param pluginName プラグイン名（基準パスから見た相対パスをドット区切りで指定、拡張子は不要）
	 * @param loadingBasePaths プラグインを検索する基準バス
	 * @param localeCode エラーメッセージの言語を指定するロケールコード
	 * @return プラグインのインスタンス
	 * @throws RinearnProcessorNanoException 読み込みに失敗した場合にスローされます。
	 */
	public static final Object loadPlugin(String pluginName, String[] loadingBasePaths, String localeCode)
			throws RinearnProcessorNanoException {

		// プラグイン検索場所をURL配列に変換
		int basePathsLength = loadingBasePaths.length;
		URL[] baseURLs = new URL[basePathsLength];
		for (int baseIndex=0; baseIndex<basePathsLength; baseIndex++) {
			try {
				baseURLs[baseIndex] = new URL(loadingBasePaths[baseIndex]);
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

		// URLクラスローダでプラグインのクラスを読み込む
		Class<?> pluginClass = null;
		try (URLClassLoader classLoader = new URLClassLoader(baseURLs)) {

			pluginClass = classLoader.loadClass(pluginName);

		} catch (ClassNotFoundException | IOException e) {
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
			e.printStackTrace();
		}

		return pluginInstance;
	}
}
