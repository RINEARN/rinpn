/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.util;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.rinearn.processornano.spec.LocaleCode;

public final class MessageManager {

	public enum DISPLAY_MODE {
		GUI,
		CUI,
	}

	private static DISPLAY_MODE displayMode = DISPLAY_MODE.GUI;

	public static final void setDisplayType(DISPLAY_MODE mode) {
		displayMode = mode;
	}

	public static final void showErrorMessage(String message, String title) {
		switch (displayMode) {
			case GUI : {
				JDialog messageWindow = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE).createDialog(null, title);
				messageWindow.setAlwaysOnTop(true);
				messageWindow.setVisible(true);
				messageWindow.dispose();
				break;
			}
			case CUI : {
				if (LocaleCode.getDefaultLocaleCode().equals(LocaleCode.JA_JP)) {
					System.err.println();
					System.err.println("エラー : " + message);
					System.err.println();
					System.err.println("--------------------------------------------------------------------------------");
					System.err.println("スタックトレース : ");
					System.err.println();
				}
				if (LocaleCode.getDefaultLocaleCode().equals(LocaleCode.EN_US)) {
					System.err.println();
					System.err.println("Error: " + message);
					System.err.println();
					System.err.println("--------------------------------------------------------------------------------");
					System.err.println("Stack Trace: ");
					System.err.println();
				}
			}
		}
	}

	public static final String customizeExceptionMessage(String message) {

		// ScriptEngine の eval でスクリプトを渡した際のエラー箇所情報。
		// このソフトでは1行の内容しか渡せないため、行番号が表示されてもうれしくないので省略
		// (毎回同じ内容になる)
		String defaultErrorSourceDescription = ": in EVAL_SCRIPT at line number 1";

		String result = message;
		if (result.endsWith(defaultErrorSourceDescription)) {
			result = result.substring(0, result.length() - defaultErrorSourceDescription.length());
		}
		return result;
	}

}
