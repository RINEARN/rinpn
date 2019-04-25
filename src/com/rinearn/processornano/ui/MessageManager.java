/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.ui;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public final class MessageManager {

	public static final void showMessage(String message, String title) {
		JDialog messageWindow = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE).createDialog(null, title);
		messageWindow.setAlwaysOnTop(true);
		messageWindow.setVisible(true);
		messageWindow.dispose();
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
