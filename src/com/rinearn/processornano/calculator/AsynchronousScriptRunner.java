/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.calculator;

import java.math.RoundingMode;

import javax.script.ScriptException;

import com.rinearn.processornano.spec.LocaleCode;
import com.rinearn.processornano.spec.RoundingTarget;
import com.rinearn.processornano.spec.SettingContainer;
import com.rinearn.processornano.ui.MessageManager;


public final class AsynchronousScriptRunner implements Runnable {

	private String scriptCode = null;
	private AsynchronousScriptListener scriptListener = null;
	private Calculator calculator = null;
	private SettingContainer setting = null;

	public AsynchronousScriptRunner(
			String scriptCode, AsynchronousScriptListener scriptListener,
			Calculator calculator, SettingContainer setting) {

		this.scriptCode = scriptCode;
		this.scriptListener = scriptListener;
		this.calculator = calculator;
		this.setting = setting;
	}

	@Override
	public final void run() {

		// 実行処理を synchronized にすると、スクリプト内容が重い場合に実行ボタンが連打された際、
		// 実行待ちスレッドがどんどん積もっていって、全部消化されるまで待たなければいけなくなるので、
		// 実行中に実行リクエストがあった場合はその場で弾くようにする。

		if (this.calculator.isRunning()) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showMessage("The previous calculation have not finished yet!", "!");
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showMessage("まだ前の計算を実行中です !", "!");
			}
			return;
		}
		this.calculator.setRunning(true);

		// 入力フィールドの式を評価して値を所得
		Object value = null;
		try {
			value = this.calculator.getScriptEngine().eval(this.scriptCode);

		} catch (ScriptException e) {
			String errorMessage = MessageManager.customizeExceptionMessage(e.getMessage());
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				MessageManager.showMessage(errorMessage, "Input/Library Error");
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				MessageManager.showMessage(errorMessage, "計算式やライブラリのエラー");
			}
			e.printStackTrace();
			this.calculator.setRunning(false);
			return;
		}

		// 値が浮動小数点数なら、設定内容に応じて丸める
		if (value instanceof Double && this.setting.outputRounderEnabled) {

			// ※ setting 内の設定値の正当性は checkSettingValues で検査済み
			RoundingMode mode = RoundingMode.valueOf(this.setting.roundingMode);
			RoundingTarget target = RoundingTarget.valueOf(this.setting.roundingTarget);
			int digits = this.setting.roundingLength;

			double doubleValue = ((Double)value).doubleValue();
			value = Rounder.round(doubleValue, mode, target, digits); // 型は BigDecimal になる
		}

		// 値を出力フィールドの表示用文字列にセットし、UIスレッドでフィールドに反映
		if (value == null) {
			this.calculator.setOutputText("");
		} else {
			this.calculator.setOutputText(value.toString());
		}
		this.scriptListener.scriptingFinished();

		this.calculator.setRunning(false);
	}
}
