/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano;

import com.rinearn.processornano.spec.LocaleCode;

public final class RinearnProcessorNanoMain {

	public static void main(String[] args) {

		// 試作実装を起動する場合はこちら
		//new Prototype().launch();

		// 以下、本番実装を起動


		// 電卓画面を起動
		if (args.length == 0) {
			new RinearnProcessorNano().launchCalculatorWindow();

		// 電卓画面を起動せずに計算を実行（コマンドライン用）
		} else if (args.length == 1) {
			new RinearnProcessorNano().calculate(args[0]);

		// 引数が多すぎる場合はエラー
		} else {
			if (LocaleCode.getDefaultLocaleCode().equals(LocaleCode.JA_JP)) {
				System.out.println("コマンドライン引数の数が多すぎます。");
			}
			if (LocaleCode.getDefaultLocaleCode().equals(LocaleCode.EN_US)) {
				System.out.println("Too many command-line arguments.");
			}
		}
	}
}
