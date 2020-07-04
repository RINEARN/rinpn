/*
 * Copyright(C) 2019-2020 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.presenter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import com.rinearn.processornano.model.AsynchronousCalculationListener;
import com.rinearn.processornano.model.CalculatorModel;
import com.rinearn.processornano.util.SettingContainer;
import com.rinearn.processornano.view.ViewInterface;

public final class RunButtonListener implements ActionListener {

	private CalculatorModel calculator = null;
	private ViewInterface view = null;
	private SettingContainer setting = null;

	protected RunButtonListener(ViewInterface view, CalculatorModel calculator, SettingContainer setting) {
		this.calculator = calculator;
		this.view = view;
		this.setting = setting;
	}

	// 「 = 」ボタンが押された際に呼ばれる
	@Override
	public final void actionPerformed(ActionEvent e) {

		// すぐ下で定義している処理を呼ぶ
		handleEvent(this.view, this.calculator, this.setting);
	}

	// 実質的なイベント処理の中身
	// （Enter キーを押された際の処理も全く同じ内容で、従って RunKeyListener からも呼ぶので、protected メソッドにまとめている）
	protected static void handleEvent(final ViewInterface view, CalculatorModel calculator, SettingContainer setting) {

		// 「OUTPUT」欄に計算実行中を表すメッセージを表示
		view.setOutputText("RUNNING...");

		// 計算完了時に、結果を OUTPUT 欄に表示するためのイベントリスナーを用意
		AsynchronousCalculationListener asyncCalcListener = new AsynchronousCalculationListener() {
			public void calculationFinished(String outputText) {
				// 計算およびリスナー呼び出しは別スレッド上で行われるため、
				// OUTPUT 欄の更新処理は OutputFieldUpdater 経由でUIスレッドに投げる
				SwingUtilities.invokeLater(new OutputFieldUpdater(view, outputText));
			}
		};

		// 別スレッドで計算を実行
		calculator.calculateAsynchronously(view.getInputText(), setting, asyncCalcListener);
	}
}