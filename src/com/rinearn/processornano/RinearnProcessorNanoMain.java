/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano;

public final class RinearnProcessorNanoMain {

	public static void main(String[] args) {

		// 試作実装を起動
		//new Prototype().launch();

		// 本番実装を起動
		new RinearnProcessorNano().launchCalculatorWindow();
	}
}
