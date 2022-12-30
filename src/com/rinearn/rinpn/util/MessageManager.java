/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn.util;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public final class MessageManager {

	/**
	 * The enum for specifying how messages should be displayed (display mode).
	 */
	public enum DisplayMode {

		/** The mode for displaying messages on an window. */
		GUI,

		/** The mode for printing messages to the standard output. */
		CUI,
	}

	private static DisplayMode displayMode = DisplayMode.GUI;

	/** If the length of an Japanese message exceeds this value, it the message be displayed in multiple lines.*/
	private static final int GUI_MESSAGE_LINE_FEEDING_THRESHOLD_JAJP = 60;

	/** If the length of an English message exceeds this value, it the message be displayed in multiple lines.*/
	private static final int GUI_MESSAGE_LINE_FEEDING_THRESHOLD_ENUS = 120;

	/** If an Japanese error message contains the following keywords, the detailed information will be displayed. */
	private static final String[] KEYWORDS_FOR_MOR_DETAILS_JA_JP = {
		"予期しない", "不明な", "外部関数", "外部変数", "プラグイン"
	};

	/** If an Error error message contains the following keywords, the detailed information will be displayed. */
	private static final String[] KEYWORDS_FOR_MOR_DETAILS_EN_US = {
		"unexpected", "unknown", "external function", "external variable", "plug-in"
	};

	/** The Japanese message notifying the user that detailed information has been printed to the standard output. */
	private static final String MESSAGE_FOR_MORE_DETAILS_JA_JP
		= "\n\n詳細はコマンドライン端末上のスタックトレースを参照してください。「 " + SettingContainer.SETTING_SCRIPT_PATH_TXT + " 」内の exceptionStackTracerEnabled を true にすると表示されます。" ;

	/** The English message notifying the user that detailed information has been printed to the standard output. */
	private static final String MESSAGE_FOR_MORE_DETAILS_EN_US
		= "For details, see the stack-trace displayed on the command-line terminal. For displaying the stack-trace, enable \" exceptionStackTracerEnabled \" in \"" + SettingContainer.SETTING_SCRIPT_PATH_TXT + "\".";


	/**
	 * Specify how messages should be displayed (display mode).
	 * 
	 * @param mode The display mode.
	 */
	public static final void setDisplayType(DisplayMode mode) {
		displayMode = mode;
	}


	/**
	 * Display the specified error message.
	 * 
	 * @param message The error message to be displayed.
	 * @param title The window title of the error message (for GUI mode).
	 * @param localeCode The locale for switching the language of the error message.
	 */
	public static final void showErrorMessage(String message, String title, String localeCode) {

		// There are "Throwable"s having no message.
		// For such case, display the content of "title" argument as a message.
		if (message == null) {
			message = title;
		}

		// Extract the part of the cause file/line: (file: ..., line: ...)
		String causeLinePart = "";
		if (0 <= message.indexOf("(")) {
			causeLinePart = message.substring(message.lastIndexOf("("));
			message = message.substring(0, message.lastIndexOf("("));
		}

		// If necessary, append the detailed information (e.g.: stack trace) to the message.
		if (localeCode.equals(LocaleCode.JA_JP)) {
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

		// Display the message.
		switch (displayMode) {
			case GUI : {

				// In GUI mode, display the message as multiple lines, if the message is long.
				if (localeCode.equals(LocaleCode.JA_JP)) {
					message = getLineFeededMessageJaJP(message);
				}
				if (localeCode.equals(LocaleCode.EN_US)) {
					message = getLineFeededMessageEnUS(message);
				}
				if (!causeLinePart.contains("main script")) {
					message += causeLinePart;
				}
				JDialog messageWindow = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE).createDialog(null, title);
				messageWindow.setAlwaysOnTop(true);
				messageWindow.setVisible(true);
				messageWindow.dispose();
				return;
			}
			case CUI : {

				// In GUI mode, display the message as single line.
				if (localeCode.equals(LocaleCode.JA_JP)) {
					System.err.println("エラー : " + message + causeLinePart);
				}
				if (localeCode.equals(LocaleCode.EN_US)) {
					System.err.println("Error: " + message + causeLinePart);
				}
				return;
			}
		}
	}


	/**
	 * Splits the specified Japanese message into multiple lines, for readability in GUI mode.
	 * 
	 * @param message The error message as single line.
	 * @return The error message as multiple lines.
	 */
	private static final String getLineFeededMessageJaJP(String message) {
		String eol = System.getProperty("line.separator");
		int messageLength = message.length();
		if (messageLength <= GUI_MESSAGE_LINE_FEEDING_THRESHOLD_JAJP) {
			return message + eol + eol;
		}

		StringBuilder lineFeededMessageBuilder = new StringBuilder();
		int lineBegin = 0;
		while (lineBegin < messageLength) {

			// Find the next char at which we can split lines, and store its index to "lineEnd".
			int lineEnd = messageLength - 1;
			int[] lineEndCandidates = new int[] {
				message.indexOf("、", lineBegin + 1),
				message.indexOf("。", lineBegin + 1),
				message.indexOf("：", lineBegin + 1),
				message.indexOf(": ", lineBegin + 1),
				message.indexOf("？", lineBegin + 1),
				message.indexOf("? ", lineBegin + 1)
			};
			for (int candidate: lineEndCandidates) {
				if (candidate != -1 && candidate < lineEnd) {
					lineEnd = candidate;
				}
			}

			// Split lines at the above char.
			String line = message.substring(lineBegin, lineEnd + 1);
			lineBegin = lineEnd + 1;
			lineFeededMessageBuilder.append(line);
			lineFeededMessageBuilder.append(eol);
		}
		return lineFeededMessageBuilder.toString();
	}


	/**
	 * Splits the specified English message into multiple lines, for readability in GUI mode.
	 * 
	 * @param message The error message as single line.
	 * @return The error message as multiple lines.
	 */
	private static final String getLineFeededMessageEnUS(String message) {
		String eol = System.getProperty("line.separator");
		int messageLength = message.length();
		if (messageLength <= GUI_MESSAGE_LINE_FEEDING_THRESHOLD_ENUS) {
			return message + eol + eol;
		}

		StringBuilder lineFeededMessageBuilder = new StringBuilder();
		int lineBegin = 0;
		while (lineBegin < messageLength) {

			// Find the next char at which we can split lines, and store its index to "lineEnd".
			int lineEnd = messageLength - 1;
			int[] lineEndCandidates = new int[] {
				message.indexOf(", ", lineBegin + 1),
				message.indexOf(". ", lineBegin + 1),
				message.indexOf(": ", lineBegin + 1),
				message.indexOf("? ", lineBegin + 1)
			};
			for (int candidate: lineEndCandidates) {
				if (candidate != -1 && candidate < lineEnd) {
					lineEnd = candidate;
				}
			}

			// Split lines at the above char.
			String line = message.substring(lineBegin, lineEnd + 1);
			lineBegin = lineEnd + 1;
			lineFeededMessageBuilder.append(line);
			lineFeededMessageBuilder.append(eol);
		}
		return lineFeededMessageBuilder.toString();
	}


	/**
	 * Prints the stack trace to the standard error.
	 * 
	 * @param e The Exception.
	 * @param localeCode The locale for switching the language of the header of the stack trace.
	 */
	public static final void showExceptionStackTrace(Exception e, String localeCode) {
		if (localeCode.equals(LocaleCode.JA_JP)) {
			System.err.println();
			System.err.println("詳細エラー情報（スクリプトエンジンのスタックトレース） : ");
			System.err.print("   ");
			e.printStackTrace();
			System.err.println("----------------------------------------------------------------------------------------------------");
			System.err.println();
		}
		if (localeCode.equals(LocaleCode.EN_US)) {
			System.err.println();
			System.err.println("Detailed Error Information (Stack Trace of the Scripting Engine): ");
			System.err.print("   ");
			e.printStackTrace();
			System.err.println("----------------------------------------------------------------------------------------------------");
			System.err.println();
		}
	}


	/**
	 * Customize the messages of the Exceptions (thrown by Vnano Engine and so on), for this application.
	 * 
	 * @param message The message of the Exception.
	 * @return The customized message.
	 */
	public static final String customizeExceptionMessage(String message) {

		// Omit the cause information in the message of a VnanoException,
		// thrown when an incorrect single line expression is executed by Vnano Engine,
		// because it is redundant for this application.
		String defaultErrorSourceDescription = ": in main script at line number 1";
		if (message.endsWith(defaultErrorSourceDescription)) {
			message = message.substring(0, message.length() - defaultErrorSourceDescription.length());
		}

		// Customize the error message related with EVAL_ONLY_FLOAT option.
		// Originally, on Vnano Engine, the above option affects to a calculation expression and a main script, 
		// and does not affect to library scripts.
		// On the other hand, on this application, the above option affects to only a calculation expression, 
		// and does not affect to both main and library scripts.
		// Hence, we should modify the message a little.
		message = message.replaceAll("ライブラリスクリプト内を除き、", "ファイルに書かれたスクリプトコード内を除き、");
		message = message.replaceAll("except in library scripts", "except in scripts written in files");

		return message;
	}

}
