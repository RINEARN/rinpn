/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.rinearn.processornano.RinearnProcessorNanoException;
import com.rinearn.processornano.spec.LocaleCode;

public final class CodeLoader {

	/**
	 * ファイルからコードを読み込みます。
	 *
	 * @param filePath ファイルのパス
	 * @param encoding 文字コード
	 * @param localeCode エラーメッセージの言語を指定するロケールコード
	 * @return 読み込まれたコード
	 * @throws RinearnProcessorNanoException
	 * 		読み込みに失敗した場合にスローされます。
	 */
	public static final String loadCode(String filePath, String encoding, String localeCode)
			throws RinearnProcessorNanoException {

		// 指定されたファイルが存在するか検査
		if (!new File(filePath).exists()) {
			if (localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage("The file \"" + filePath + "\" does not exist.", "Code Loading Error");
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage("ファイル \"" + filePath + "\" が見つかりません。", "コード読み込みエラー");
			}
			throw new RinearnProcessorNanoException("The file \"" + filePath + "\" does not exist.");
		}

		// 読み込み処理
		try {

			// 全行を読み込み、行単位で格納したListを取得
			List<String> lines = Files.readAllLines(Paths.get(filePath), Charset.forName(encoding));

			// 改行コードを挟みつつ全行を結合
			StringBuilder codeBuilder = new StringBuilder();
			String eol = System.getProperty("line.separator");
			for (String line: lines) {
				codeBuilder.append(line);
				codeBuilder.append(eol);
			}
			String code = codeBuilder.toString();

			// UTF-8ではBOMの有無を検査し、付いている場合は削除
			if(encoding.toLowerCase().equals("utf-8")) {
				// UTF-8のBOMは 0xEF 0xBB 0xBF だが、文字列内部表現がUTF-16な都合で、読み込み後は 0xFEFF が付いている
				final char bom = (char)0xFEFF;
				if(0 < code.length() && code.charAt(0) == bom){
					code = code.substring(1);
				}
			}

			return code;

		// 非対応の文字コードが指定された場合
		} catch (UnsupportedCharsetException uce) {

			if (localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage("The encoding \"" + encoding + "\" is not supported.", "Code Loading Error");
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage("非対応の文字コード \"" + encoding + "\" が指定されています。", "コード読み込みエラー");
			}
			throw new RinearnProcessorNanoException(uce);

		// 何らかの理由で読み込みに失敗した場合
		} catch (IOException ioe) {

			if (localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(
					"An error (IOException) occurred for the loading of \"" + filePath + "\".",
					"Code Loading Error"
				);
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(
					"\"" + filePath + "\" の読み込みにおいて、エラー (IOException) が発生しました。",
					"コード読み込みエラー"
				);
			}
			throw new RinearnProcessorNanoException(ioe);
		}
	}
}
