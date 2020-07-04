/*
 * Copyright(C) 2019-2020 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.presenter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.SwingUtilities;

import com.rinearn.processornano.model.AsynchronousCalculationListener;
import com.rinearn.processornano.model.CalculatorModel;
import com.rinearn.processornano.util.SettingContainer;
import com.rinearn.processornano.view.ViewInterface;

public final class RunKeyListener implements KeyListener {

	private CalculatorModel calculator;
	private ViewInterface view;
	private SettingContainer setting;

	protected RunKeyListener(ViewInterface view, CalculatorModel calculator, SettingContainer setting) {
		this.calculator = calculator;
		this.view = view;
		this.setting = setting;
	}

	@Override
	public final void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {

			// 「OUTPUT」欄に計算実行中を表すメッセージを表示
			this.view.setOutputText("RUNNING...");

			// 計算完了時に、結果を OUTPUT 欄に表示するためのイベントリスナーを用意
			AsynchronousCalculationListener asyncCalcListener = new AsynchronousCalculationListener() {
				public void calculationFinished(String outputText) {
					// 計算およびリスナー呼び出しは別スレッド上で行われるため、
					// OUTPUT 欄の更新処理は OutputFieldUpdater 経由でUIスレッドに投げる
					SwingUtilities.invokeLater(new OutputFieldUpdater(view, outputText));
				}
			};

			// 別スレッドで計算を実行
			calculator.calculateAsynchronously(this.view.getInputText(), this.setting, asyncCalcListener);
		}
	}

	@Override
	public final void keyTyped(KeyEvent e) {
	}

	@Override
	public final void keyReleased(KeyEvent e) {
	}
}
