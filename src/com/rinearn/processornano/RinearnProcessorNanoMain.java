package com.rinearn.processornano;

public final class RinearnProcessorNanoMain {

	public static void main(String[] args) {

		// 試作実装を起動する（本実装はまだ何もない）
		Prototype prototype = new Prototype();
		prototype.launch();

		// 本実装ができてきた時点で Prototype ではなく
		// RinearnProcessorNano を起動するように切り替える
	}
}
