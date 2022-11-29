/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import com.rinearn.rinpn.RINPnFatalException;

public final class OutputValueFormatter {

	/**
	 * Rounds the specified value under the specified settings.
	 * 
	 * @param inputValue The value to be rounded.
	 * @param setting The container storing setting values.
	 * @return The rounded value.
	 */
	public static final BigDecimal round(double inputValue, SettingContainer setting) {

		// If the rounding mode/length is explicitly specified by the option settings:
		if (setting.outputRounderEnabled) {

			// Converts the value of the rounding option into the name of RoundingMode enum's element.
			String roundingModeName = setting.roundingMode;
			if (roundingModeName.equals("HALF_TO_EVEN")) {
				roundingModeName = "HALF_EVEN";
			}
			RoundingMode mode = RoundingMode.valueOf(roundingModeName);

			// Get the related option values.
			RoundingTarget target = RoundingTarget.valueOf(setting.roundingTarget);
			int digits = setting.roundingLength;
			boolean performsImplicitRounding = setting.performImplicitRoundingBeforeRounding;

			// Perform the rounding.
			return round(inputValue, mode, target, digits, performsImplicitRounding);

		// If the rounding option is disabled:
		} else {

			// Generally a "double" value may be indivisible (has the infinite length of) number in decimal.
			// Hence, firstly converts the double value into a String value, and then convert it into a BigDecimal object.
			// (Note: the double value is rounded implicitly when the value is converted into a String value.)
			return new BigDecimal( Double.toString(inputValue) );
		}
	}


	/**
	 * Rounds the specified value under the specified settings.
	 * 
	 * @param inputValue The value to be rounded.
	 * @param mode The rounding mode.
	 * @param target The part to be rounded.
	 * @param digits The length of the rounded part.
	 * @param performsImplicitRounding Specify true if 
	 * @return The rounded value.
	 */
	public static final BigDecimal round(
			double inputValue, RoundingMode mode, RoundingTarget target, int digits,
			boolean performsImplicitRounding) {

		// ----------------------------------------------------------------------------------------------------
		// Important Note
		// ----------------------------------------------------------------------------------------------------
		// Caused by binary-decimal conversion errors, 
		// sometimes the rounded result may look tricky, depends of how it is rounded.
		//
		// For example, assumes that a double variable "d" is initialized as follows:
		//
		//   double doubleValue = 1.005;
		//
		// The above value 1.005 is indivisible in binary floating-point number expression,
		// so the above "doubleValue" has an approximate binary value.
		// Then, convert it into decimal floating-point number "decimalValue":
		//
		//   BigDecimal decimalValue = new BigDecimal(doubleValue);
		//
		// The above "decimalValue" has the decimal floating-point number expression
		// OF THE APPROXIMATE NUMBER OF 1.005 (stored in "doubleValue").
		// Practically, the above "decimalValue" take the value 1.00499...
		//
		// Hence, if we round it into the number having 2-digits after the radix point in HALF_UP mode,
		// we get the value 1.00.
		//
		// On the other hand, if we convert the "doubleValue" into a String value, the result is: 1.005.
		// ( Note: Due to the redundancy of the precision of the internal binary expression,
		//   the value of a binary floating point number just after it is initialized can express the original value well,
		//   if we convert it into a character string most of the time. See IEEE754. )
		// So if we convert the "doubleValue" into BigDecimal object through the String value, like as:
		//
		//   BigDecimal decimalValue2 = new BigDecimal( String.valueOf(doubleValue) );
		//
		// If we round the above into the number having 2-digits after the radix point in HALF_UP mode,
		// we get the value 1.01.
		//
		// Therefore, the rounded result may depends on whether we convert a double value into a BigDecimal directly,
		// or convert it into a BigDecimal through a String value.
		//
		// When the argument "performsImplicitRounding" is false, the value will be rounded directly.
		// If true, it will be rounded through a String value.
		// ----------------------------------------------------------------------------------------------------

		// Convert the double value into a BigDecimal value.
		BigDecimal bdValue = null;
		if (performsImplicitRounding) {

			// Convert the double value through a String value.
			// See the above "Important Note".
			String strValue = Double.toString(inputValue);
			bdValue = new BigDecimal(strValue);

		} else {

			// Convert directly.
			bdValue = new BigDecimal(inputValue);
		}

		// Perform the rounding under the specified settings.
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


	/**
	 * Simplify the notation of the rounded value.
	 * For example, simplify 123.00000 to 123, 123.4E+5 to 123.4E5, and so on.
	 *
	 * @param inputValue The rounded value to be simplified.
	 * @return The simplified value.
	 */
	public static final String simplify(BigDecimal inputValue) {
		String fullStr = inputValue.toString().toUpperCase();

		// Split the value into the significand (1.234 of 1.234E5) part and the exponent part (5).
		String significandStr = null;
		String exponentStr = null;
		boolean hasExponent = 0 <= fullStr.indexOf("E");
		if (hasExponent) {
			String[] split = fullStr.split("E");
			if (split.length != 2) {
				throw new RINPnFatalException("Unexpected value format detected.");
			}
			significandStr = split[0];
			exponentStr = split[1];
		} else {
			significandStr = fullStr;
			exponentStr = "";
		}

		// Split the significand part (1 of 1.234) into the integer part and the fraction part (234).
		// 仮数部を整数部と小数部に分割
		String integerStr = null;
		String fractionStr = null;
		boolean hasPoint = 0 <= significandStr.indexOf(".");
		if (hasPoint) {
			String split[] = significandStr.split("\\.");
			if (split.length != 2) {
				throw new RINPnFatalException("Unexpected value format detected.");
			}
			integerStr = split[0];
			fractionStr = split[1];
		} else {
			integerStr = fullStr;
			fractionStr = "";
		}

		// If the fraction part ends with sequential zeros, remove them.
		while (fractionStr.endsWith("0")) {
			fractionStr = fractionStr.substring(0, fractionStr.length()-1);
		}

		// If the exponent part begins with "+", remove it.
		if (exponentStr.startsWith("+")) {
			exponentStr = exponentStr.substring(1, exponentStr.length());
		}

		// Connect the simplified parts again.
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

// (Temporary note in Japanese)
// メモ：暗黙丸めと明示的丸めは独立に効くようにしてもいいかもしれない。
//    現状の仕様では、明示的丸めが OFF の場合に暗黙丸めも OFF にできない。
//    できても、内部データが double である時点でその精度以上を表示する必要は無いけれど、
//    オプションの効き方としては単純化できるので、検討の余地はあるかも。


