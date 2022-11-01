/*
 * Copyright(C) 2019-2021 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn.util;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public final class MessageManager {

	public enum DISPLAY_MODE {
		GUI,
		CUI,
	}

	private static DISPLAY_MODE displayMode = DISPLAY_MODE.GUI;

	 // これよりも長いメッセージは、メッセージが長いとディスプレイ幅によっては読みづらいので、自動で改行を挟む
	private static final int GUI_MESSAGE_LINE_FEEDING_THRESHOLD_JAJP = 60; // 日本語は全角で幅をとるので少なめ
	private static final int GUI_MESSAGE_LINE_FEEDING_THRESHOLD_ENUS = 120; // 英語は半角で省スペースなので多め

	// ユーザーにとって詳細情報が必要そうな場合に、エラーメッセージ内に登場する語句
	private static final String[] KEYWORDS_FOR_MOR_DETAILS_JA_JP = {
		"予期しない", "不明な", "外部関数", "外部変数", "プラグイン"
	};
	private static final String[] KEYWORDS_FOR_MOR_DETAILS_EN_US = {
		"unexpected", "unknown", "external function", "external variable", "plug-in"
	};

	private static final String MESSAGE_FOR_MORE_DETAILS_JA_JP
		= "\n( 詳細は「 " + SettingContainer.SETTING_SCRIPT_PATH_TXT + " (or .vnano) 」内のデバッグ項目類を有効にしてください )" ;

	private static final String MESSAGE_FOR_MORE_DETAILS_EN_US
		= "\n( Enable debugging options in \"" + SettingContainer.SETTING_SCRIPT_PATH_TXT + " (or .vnano)\" for more details )";


	public static final void setDisplayType(DISPLAY_MODE mode) {
		displayMode = mode;
	}

	public static final void showErrorMessage(String message, String title, String localeCode) {
		if (message == null) {
			// message を持ってない Throwable 等も存在し、
			// その場合はエラーが発生した事だけでも通知するため、ウィンドウタイトルと同内容で代用する
			// （詳細は必要に応じて、呼び出し元でコマンドラインにスタックトレース等を出力する）
			message = title;
		}

		// ややこしいエラーに対しては、詳細を表示したい場合のための補足説明を追記
		if (localeCode.equals(LocaleCode.JA_JP)) {
			// ややこしそうなメッセージに含まれる語句を検索
			for (String keyword: KEYWORDS_FOR_MOR_DETAILS_JA_JP) {
				if (message.contains(keyword)) {
					message += MESSAGE_FOR_MORE_DETAILS_JA_JP;
					break;
				}
			}
		}
		if (localeCode.equals(LocaleCode.EN_US)) {
			String lowerCaseMessage = message.toLowerCase();
			for (String keyword: KEYWORDS_FOR_MOR_DETAILS_EN_US) {
				String lowerCaseKeyword = keyword.toLowerCase();
				if (lowerCaseMessage.contains(lowerCaseKeyword)) {
					message += MESSAGE_FOR_MORE_DETAILS_EN_US;
					break;
				}
			}
		}

		// 画面の種類（GUI/CUI）に応じて表示する
		switch (displayMode) {
			case GUI : {
				int messageLength = message.length();

				// メッセージが長い場合は改行を挟む
				// (あまり長くないメッセージの場合はそのまま一行で表示する)
				if (localeCode.equals(LocaleCode.JA_JP)) {
					if (GUI_MESSAGE_LINE_FEEDING_THRESHOLD_JAJP < messageLength) {
						message = getLineFeededMessageJaJP(message);
					}
				}
				if (localeCode.equals(LocaleCode.EN_US)) {
					if (GUI_MESSAGE_LINE_FEEDING_THRESHOLD_ENUS < messageLength) {
						message = getLineFeededMessageEnUS(message);
					}
				}

				JDialog messageWindow = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE).createDialog(null, title);
				messageWindow.setAlwaysOnTop(true);
				messageWindow.setVisible(true);
				messageWindow.dispose();
				return;
			}
			case CUI : {
				if (localeCode.equals(LocaleCode.JA_JP)) {
					System.err.println("エラー : " + message);
				}
				if (localeCode.equals(LocaleCode.EN_US)) {
					System.err.println("Error: " + message);
				}
				return;
			}
		}
	}

	// 句読点の位置に改行を挟んで返す
	private static final String getLineFeededMessageJaJP(String message) {

		// 現状は単純に句読点で改行するようにしているけれど、日本語の場合は折り返してもいいかもしれない。
		// ただ、スクリプト内のエラー部分の抜き出し箇所とかで折り返されると読みづらいので、
		// 暫定的に句読点ベースにしているものの、将来的に改良を要検討

		String eol = System.getProperty("line.separator");
		int messageLength = message.length();
		StringBuilder lineFeededMessageBuilder = new StringBuilder();
		int lineBegin = 0;
		while (lineBegin < messageLength) {

			// 次の最も近い句読点（や区切りのいい文字）を探して lienEnd にインデックスを格納
			int lineEnd = messageLength - 1;
			int[] lineEndCandidates = new int[] {
				message.indexOf("、", lineBegin + 1),
				message.indexOf("。", lineBegin + 1),
				message.indexOf("：", lineBegin + 1),
				message.indexOf(": ", lineBegin + 1),
			};
			for (int candidate: lineEndCandidates) {
				if (candidate != -1 && candidate < lineEnd) {
					lineEnd = candidate;
				}
			}

			// そこで行を区切って改行コードを挟む
			String line = message.substring(lineBegin, lineEnd + 1);
			lineFeededMessageBuilder.append(line);
			lineFeededMessageBuilder.append(eol);
			lineBegin = lineEnd + 1;
		}
		return lineFeededMessageBuilder.toString();
	}


	// 句読点の位置に改行を挟んで返す
	private static final String getLineFeededMessageEnUS(String message) {

		// 現状は日本語とほぼ同じように句読点で改行するようにしているけれど、
		// スクリプト内のエラー部分の抜き出し箇所などで区切られて読みづらいケースが出てきた場合は改修を要検討
		// （固定幅で折り返す場合は、単語の最中に切ってはいけないので、単語内かどうか判定してハイフンを付けたりが必要になると思う）

		String eol = System.getProperty("line.separator");
		int messageLength = message.length();
		StringBuilder lineFeededMessageBuilder = new StringBuilder();
		int lineBegin = 0;
		while (lineBegin < messageLength) {

			// 次の最も近い句読点（や区切りのいい文字）を探して lienEnd にインデックスを格納
			int lineEnd = messageLength - 1;
			int[] lineEndCandidates = new int[] {
				message.indexOf(", ", lineBegin + 1),
				message.indexOf(". ", lineBegin + 1),
				message.indexOf(": ", lineBegin + 1)
			};
			for (int candidate: lineEndCandidates) {
				if (candidate != -1 && candidate < lineEnd) {
					lineEnd = candidate;
				}
			}

			// そこで行を区切って改行コードを挟む
			String line = message.substring(lineBegin, lineEnd + 1);
			lineFeededMessageBuilder.append(line);
			lineFeededMessageBuilder.append(eol);
			lineBegin = lineEnd + 1;
		}
		return lineFeededMessageBuilder.toString();
	}

	public static final void showExceptionStackTrace(Exception e, String localeCode) {
		if (localeCode.equals(LocaleCode.JA_JP)) {
			System.err.println();
			System.err.println("--------------------------------------------------------------------------------");
			System.err.println("スタックトレース : ");
			System.err.println();
			e.printStackTrace();
		}
		if (localeCode.equals(LocaleCode.EN_US)) {
			System.err.println();
			System.err.println("--------------------------------------------------------------------------------");
			System.err.println("Stack Trace: ");
			System.err.println();
			e.printStackTrace();
		}
	}

	public static final String customizeExceptionMessage(String message) {

		// ScriptEngine の eval でスクリプトを渡した際のエラー箇所情報。
		// このソフトでは1行の内容しか渡せないため、行番号が表示されてもうれしくないので省略
		// (毎回同じ内容になる)
		String defaultErrorSourceDescription = ": in main script at line number 1";
		if (message.endsWith(defaultErrorSourceDescription)) {
			message = message.substring(0, message.length() - defaultErrorSourceDescription.length());
		}

		// 型をfloatのみに制限するオプションによるエラーメッセージは、
		// エンジン側では式の実行時だけでなくスクリプト実行時にも効き、ライブラリスクリプトに対してのみ効かない。
		// しかしこのソフトでは、スクリプト実行時には効かないようにオプションを OFF にしているため、
		// 説明がソフト仕様とちぐはぐにならないようにメッセージを少し調整する。
		// ※ 元のメッセージは org.vcssl.nano.spec.ErrorMessage の NON_FLOAT_DATA_TYPES_ARE_RESTRICTED 参照
		message = message.replaceAll("ライブラリスクリプト内を除き、", "ファイルに書かれたスクリプトコード内を除き、");
		message = message.replaceAll("except in library scripts", "except in scripts written in files");

		return message;
	}

}
