/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import com.rinearn.processornano.spec.RoundingTarget;
import com.rinearn.processornano.spec.SettingContainer;

public final class Rounder {

	public static final BigDecimal round(double value, SettingContainer setting) {
		if (setting.outputRounderEnabled) {
			RoundingMode mode = RoundingMode.valueOf(setting.roundingMode);
			RoundingTarget target = RoundingTarget.valueOf(setting.roundingTarget);
			int digits = setting.roundingLength;
			return round(value, mode, target, digits);
		} else {
			return new BigDecimal(value);
		}
	}

	public static final BigDecimal round(double value, RoundingMode mode, RoundingTarget target, int digits) {

		BigDecimal bdValue = new BigDecimal(value);

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

}
