/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import com.rinearn.processornano.RinearnProcessorNanoFatalException;
import com.rinearn.processornano.spec.RoundingTarget;
import com.rinearn.processornano.spec.SettingContainer;

public final class Rounder {

	protected static final BigDecimal round(double inputValue, SettingContainer setting) {
		if (setting.outputRounderEnabled) {
			RoundingMode mode = RoundingMode.valueOf(setting.roundingMode);
			RoundingTarget target = RoundingTarget.valueOf(setting.roundingTarget);
			int digits = setting.roundingLength;
			return round(inputValue, mode, target, digits);
		} else {
			return new BigDecimal(inputValue);
		}
	}

	protected static final BigDecimal round(
			double inputValue, RoundingMode mode, RoundingTarget target, int digits) {

		BigDecimal bdValue = new BigDecimal(inputValue);
		switch (target) {
			case AFTER_FIXED_POINT : {
				bdValue = bdValue.setScale(digits, mode);
				break;
			}
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
		if (hasPoint) {
			outputValueStr += "." + fractionStr;
		}
		if (hasExponent) {
			outputValueStr += "E" + exponentStr;
		}
		return outputValueStr;
	}

}
