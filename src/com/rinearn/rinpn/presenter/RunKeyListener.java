/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn.presenter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.rinearn.rinpn.model.CalculatorModel;
import com.rinearn.rinpn.util.SettingContainer;
import com.rinearn.rinpn.view.View;

public final class RunKeyListener implements KeyListener {

	private CalculatorModel calculator;
	private View view;
	private SettingContainer setting;

	protected RunKeyListener(View view, CalculatorModel calculator, SettingContainer setting) {
		this.calculator = calculator;
		this.view = view;
		this.setting = setting;
	}

	@Override
	public final void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			RunButtonListener.handleEvent(this.view, this.calculator, this.setting);
		}
	}

	@Override
	public final void keyTyped(KeyEvent e) {
	}

	@Override
	public final void keyReleased(KeyEvent e) {
	}
}
