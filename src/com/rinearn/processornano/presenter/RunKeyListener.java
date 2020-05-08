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
			AsynchronousCalculationListener asyncCalcListener = new AsynchronousCalculationListener() {
				public void calculationFinished(String outputText) {
					SwingUtilities.invokeLater(new OutputFieldUpdater(view, outputText));
				}
			};
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
