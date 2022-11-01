/*
 * Copyright(C) 2019-2020 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import com.rinearn.rinpn.RinearnProcessorNanoFatalException;
import com.rinearn.rinpn.util.RoundingTarget;
import com.rinearn.rinpn.util.SettingContainer;

// メモ：暗黙丸めと明示的丸めは独立に効くようにしてもいいかもしれない。
//       現状の仕様では、明示的丸めが OFF の場合に暗黙丸めも OFF にできない。
//       できても、内部データが double である時点でその精度以上を表示する必要は無いけれど、
//       オプションの効き方としては単純化できるので、検討の余地はあるかも。

public final class OutputValueFormatter {

	protected static final BigDecimal round(double inputValue, SettingContainer setting) {

		// オプションで丸め（設定内容に基づく明示的丸め）が有効化されている場合
		if (setting.outputRounderEnabled) {

			// 丸めモード名を取得し、"HALF_TO_EVEN" の場合は "HALF_EVEN" に変換
			//（ Vnano の組み込み関数への指定では HALF_TO_EVEN 表記（にする予定）なので、
			//   Vnano 実行をサポートしている RINPn でも統一性のために同名にしたいが、
			//   以下で使う RoundingMode の enum 要素名は HALF_EVEN であるため。）
			String roundingModeName = setting.roundingMode;
			if (roundingModeName.equals("HALF_TO_EVEN")) {
				roundingModeName = "HALF_EVEN";
			}

			// 丸めモード名を RoundingMode enum の要素に変換
			RoundingMode mode = RoundingMode.valueOf(roundingModeName);

			// その他の設定値を取得/変換
			RoundingTarget target = RoundingTarget.valueOf(setting.roundingTarget);
			int digits = setting.roundingLength;
			boolean performsImplicitRounding = setting.performImplicitRoundingBeforeRounding;

			// 丸め処理を実行して返す
			return round(inputValue, mode, target, digits, performsImplicitRounding);

		// オプションで丸め（設定内容に基づく明示的丸め）が無効化されている場合
		} else {

			// double の有効精度内の桁数に収めるため、一旦 String を介してから BigDecimal に変換する
			// （暗黙の丸め： 下に続くメソッド内のコメント参照）
			return new BigDecimal( Double.toString(inputValue) );
		}
	}


	protected static final BigDecimal round(
			double inputValue, RoundingMode mode, RoundingTarget target, int digits,
			boolean performsImplicitRounding) {

		// 注意:
		// 「 INPUT 」欄に入力された値をそのまま丸めて表示するような場合で、
		// 入力値を格納する double 値が10進数の有限桁で表現できない場合に、
		// その2進-10進変換誤差が丸め結果にクリティカルに効く場合がある。

		// 例えば 1.005 を代入した double 値（2進表現）は、理論上は10進表現でちょうど 1.005 にはならないため、
		// 素直に BigDecimal に（コンストラクタ引数に渡して）変換すると 1.00499... になる。
		// これを、有効桁数3桁（小数点以下2桁）に HALF_UP すると、小数部は .005 に足らないため切り捨てられて 1.00 になる。
		// （これはこれである意味正しい挙動）

		// 対して、事前に String に変換すると、double の有効精度を考慮してほどほどに丸められた10進表現が得られるので、
		// 少なくとも代入直後の状態では、上述の2進-10進変換誤差を（限定的ではあるが）そこそこ吸収する作用が見込める。
		// 例えば 1.005 を代入した double 値では "1.005" が得られる。これを BigDecimal に変換して、
		// 先と同様に有効桁数3桁（小数点以下2桁）に HALF_UP すると、今度は .005 が切り上げられて 1.01 が得られる。

		// ただし完全ではないし、代入後の状態から演算を行うと誤差が（文字列変換時に丸める冗長範囲を超えて）表面化もするし、
		// その程度の効果のために丸め処理の内容を複雑にしたいかどうかも恐らく場面によるので、
		// オプションで有効/無効を切り替え可能にしている（このメソッドの引数 performsImplicitRounding に渡される）。


		// 丸めを行うために、double から BigDecimal に変換する
		BigDecimal bdValue = null;

		// 丸め前の暗黙丸めオプションが有効な場合
		if (performsImplicitRounding) {

			// まず普通に10進数の文字列表現に変換する（精度範囲付近または超過するような桁に、ある程度の暗黙的な丸めが作用する）
			String strValue = Double.toString(inputValue);

			// その後に BigDecimal に変換する
			bdValue = new BigDecimal(strValue);

		// 丸め前の暗黙丸めオプションが無効な場合
		} else {

			// 直接的に BigDecimal に変換する（10進表現で割り切れない値などは、double の精度範囲以降に変換誤差が乗った値になる）
			bdValue = new BigDecimal(inputValue);
		}

		// 丸め処理を実行
		switch (target) {

			// 「小数点以下何桁」などの基準で丸める場合
			case AFTER_FIXED_POINT : {
				bdValue = bdValue.setScale(digits, mode);
				break;
			}

			// 有効桁数基準で丸める場合（いわゆる数値の精度）
			case SIGNIFICAND : {
				MathContext mathContext = new java.math.MathContext(digits, mode);
				bdValue = bdValue.round(mathContext);
				break;
			}
		}
		return bdValue;
	}

	protected static final String simplify(BigDecimal inputValue) {
		String fullStr = inputValue.toString().toUpperCase();

		// 指数部と仮数部に分割
		String significandStr = null;  // 仮数部
		String exponentStr = null;     // 指数部
		boolean hasExponent = 0 <= fullStr.indexOf("E");
		if (hasExponent) {
			String[] split = fullStr.split("E");
			if (split.length != 2) {
				throw new RinearnProcessorNanoFatalException("Unexpected value format detected.");
			}
			significandStr = split[0];
			exponentStr = split[1];
		} else {
			significandStr = fullStr;
			exponentStr = "";
		}

		// 仮数部を整数部と小数部に分割
		String integerStr = null;   // 整数部
		String fractionStr = null;  // 小数部
		boolean hasPoint = 0 <= significandStr.indexOf(".");
		if (hasPoint) {
			String split[] = significandStr.split("\\.");
			if (split.length != 2) {
				throw new RinearnProcessorNanoFatalException("Unexpected value format detected.");
			}
			integerStr = split[0];
			fractionStr = split[1];
		} else {
			integerStr = fullStr;
			fractionStr = "";
		}

		// 小数部の末尾に連続する0を削除
		while (fractionStr.endsWith("0")) {
			fractionStr = fractionStr.substring(0, fractionStr.length()-1);
		}

		// 指数部の数字先頭に「+」に付いている場合は削除（「-」はそのまま）
		if (exponentStr.startsWith("+")) {
			exponentStr = exponentStr.substring(1, exponentStr.length());
		}

		// 各部を結合し、値全体の文字列を生成して返す
		String outputValueStr = integerStr;
		if (hasPoint && 0 < fractionStr.length()) {
			outputValueStr += "." + fractionStr;
		}
		if (hasExponent) {
			outputValueStr += "E" + exponentStr;
		}
		return outputValueStr;
	}

}
