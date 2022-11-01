/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.presenter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import com.rinearn.processornano.model.AsynchronousCalculationListener;
import com.rinearn.processornano.model.AsynchronousCalculationRunner;
import com.rinearn.processornano.model.CalculatorModel;
import com.rinearn.processornano.util.SettingContainer;
import com.rinearn.processornano.view.View;

public final class RunButtonListener implements ActionListener {

	private CalculatorModel calculator = null;
	private View view = null;
	private SettingContainer setting = null;

	protected RunButtonListener(View view, CalculatorModel calculator, SettingContainer setting) {
		this.calculator = calculator;
		this.view = view;
		this.setting = setting;
	}

	@Override
	public final void actionPerformed(ActionEvent e) {
		handleEvent(this.view, this.calculator, this.setting);
	}

	protected static void handleEvent(final View view, CalculatorModel calculator, SettingContainer setting) {
		view.outputField.setText("RUNNING...");

		// 計算完了時に、結果を OUTPUT 欄に表示するためのイベントリスナーを用意
		AsynchronousCalculationListener asyncCalcListener = new AsynchronousCalculationListener() {
			public void calculationFinished(String outputText) {
				// 計算およびリスナー呼び出しは別スレッド上で行われるため、
				// OUTPUT 欄の更新処理は OutputFieldUpdater 経由でUIスレッドに投げる
				SwingUtilities.invokeLater(new OutputFieldUpdater(view, outputText));
			}
		};

		// 計算実行スレッドを生成して実行（中でこのクラスの calculate が呼ばれて実行される）
		AsynchronousCalculationRunner asyncCalcRunner
				= new AsynchronousCalculationRunner(view.inputField.getText(), asyncCalcListener, calculator, setting);
		Thread calculatingThread = new Thread(asyncCalcRunner);
		calculatingThread.start();
	}
}
