/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import com.rinearn.processornano.calculator.AsynchronousScriptListener;
import com.rinearn.processornano.calculator.Calculator;
import com.rinearn.processornano.spec.SettingContainer;
import com.rinearn.processornano.ui.UIContainer;

public final class RunButtonListener implements ActionListener {

	private Calculator calculator = null;
	private UIContainer ui = null;
	private SettingContainer setting = null;

	public RunButtonListener(UIContainer ui, Calculator calculator, SettingContainer setting) {
		this.calculator = calculator;
		this.ui = ui;
		this.setting = setting;
	}

	@Override
	public final void actionPerformed(ActionEvent e) {

		AsynchronousScriptListener scriptListener = new AsynchronousScriptListener() {
			public void scriptingFinished() {
				SwingUtilities.invokeLater(new UpwardSynchronizer(calculator, ui));
			}
		};
		calculator.requestCalculation(this.ui, this.setting, scriptListener);
	}
}