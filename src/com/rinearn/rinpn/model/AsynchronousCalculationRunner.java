/*
 * Copyright(C) 2019-2020 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn.model;

import javax.script.ScriptException;

import com.rinearn.rinpn.RinearnProcessorNanoException;
import com.rinearn.rinpn.util.LocaleCode;
import com.rinearn.rinpn.util.MessageManager;
import com.rinearn.rinpn.util.SettingContainer;


public final class AsynchronousCalculationRunner implements Runnable {

	private AsynchronousCalculationListener calculationListener = null;
	private Model model = null;
	private SettingContainer setting = null;
	private String inputExpression = null;

	public AsynchronousCalculationRunner(
			String inputExpression, AsynchronousCalculationListener scriptListener,
			Model model, SettingContainer setting) {

		this.inputExpression = inputExpression;
		this.calculationListener = scriptListener;
		this.model = model;
		this.setting = setting;
	}

	@Override
	public final void run() {

		// スクリプト内容が重い場合に実行ボタンが連打されると、
		// 処理がどんどん積もっていって全部消化されるまで待たなければいけなくなるので、
		// 実行中に実行リクエストがあった場合はその場で弾くようにする。

		if (this.model.isCalculating()) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage("The previous calculation has not finished yet!", "!", setting.localeCode);
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage("まだ前の計算を実行中です !", "!", setting.localeCode);
			}
			return;
		}

		try {
			// 入力フィールドの計算式を実行し、結果の値を取得
			String outputText = this.model.calculate(this.inputExpression, true, this.setting);

			// 計算リクエスト元に計算完了を通知
			this.calculationListener.calculationFinished(outputText);

		} catch (ScriptException | RinearnProcessorNanoException e) {

			// 計算結果の代わりに、エラーの発生を示すメッセージを通知（ OUTPUT 欄に表示される ）
			this.calculationListener.calculationFinished("ERROR");

			//エラー内容をユーザーに表示
			String errorMessage = MessageManager.customizeExceptionMessage(e.getMessage());
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showErrorMessage(errorMessage, "Expression/Script Error", setting.localeCode);
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showErrorMessage(errorMessage, "計算式やスクリプトのエラー", setting.localeCode);
			}
			if (setting.exceptionStackTracerEnabled) {
				MessageManager.showExceptionStackTrace(e, setting.localeCode);
			}
		}
	}
}
