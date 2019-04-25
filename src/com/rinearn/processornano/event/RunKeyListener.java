/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.event;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.SwingUtilities;

import com.rinearn.processornano.calculator.AsynchronousScriptListener;
import com.rinearn.processornano.calculator.Calculator;
import com.rinearn.processornano.spec.SettingContainer;
import com.rinearn.processornano.ui.UIContainer;

public final class RunKeyListener implements KeyListener {

	private Calculator calculator;
	private UIContainer ui;
	private SettingContainer setting;

	public RunKeyListener(UIContainer ui, Calculator calculator, SettingContainer setting) {
		this.calculator = calculator;
		this.ui = ui;
		this.setting = setting;
	}

	@Override
	public final void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			AsynchronousScriptListener scriptListener = new AsynchronousScriptListener() {
				public void scriptingFinished() {
					SwingUtilities.invokeLater(new UpwardSynchronizer(calculator, ui));
				}
			};
			calculator.requestCalculation(this.ui, this.setting, scriptListener);
		}
	}

	@Override
	public final void keyTyped(KeyEvent e) {
	}

	@Override
	public final void keyReleased(KeyEvent e) {
	}
}
