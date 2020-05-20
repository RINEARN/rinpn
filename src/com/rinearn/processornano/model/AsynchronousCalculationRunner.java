/*
 * Copyright(C) 2019-2020 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.model;

import javax.script.ScriptException;

import com.rinearn.processornano.RinearnProcessorNanoException;
import com.rinearn.processornano.util.LocaleCode;
import com.rinearn.processornano.util.MessageManager;
import com.rinearn.processornano.util.SettingContainer;


public final class AsynchronousCalculationRunner implements Runnable {

	private AsynchronousCalculationListener calculationListener = null;
	private CalculatorModel calculator = null;
	private SettingContainer setting = null;
	private String inputExpression = null;

	protected AsynchronousCalculationRunner(
			String inputExpression, AsynchronousCalculationListener scriptListener,
			CalculatorModel calculator, SettingContainer setting) {

		this.inputExpression = inputExpression;
		this.calculationListener = scriptListener;
		this.calculator = calculator;
		this.setting = setting;
	}

	@Override
	public final void run() {

		// スクリプト内容が重い場合に実行ボタンが連打されると、
		// 処理がどんどん積もっていって全部消化されるまで待たなければいけなくなるので、
		// 実行中に実行リクエストがあった場合はその場で弾くようにする。

		if (this.calculator.isCalculating()) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage("The previous calculation have not finished yet!", "!");
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage("まだ前の計算を実行中です !", "!");
			}
			return;
		}

		// 入力フィールドの計算式を実行し、結果の値を取得
		String outputText = "";
		try {
			outputText = this.calculator.calculate(this.inputExpression, this.setting);

		} catch (ScriptException | RinearnProcessorNanoException e) {
			String errorMessage = MessageManager.customizeExceptionMessage(e.getMessage());
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(errorMessage, "Expression/Script Error");
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(errorMessage, "計算式やスクリプトのエラー");
			}
			if (setting.exceptionStackTracerEnabled) {
				MessageManager.showExceptionStackTrace(e);
			}
			return;
		}

		// 計算リクエスト元に計算完了を通知
		this.calculationListener.calculationFinished(outputText);
	}
}
