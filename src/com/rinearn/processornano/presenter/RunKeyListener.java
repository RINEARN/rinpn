/*
 * Copyright(C) 2019-2020 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.presenter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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

	// 「INPUT」欄でキーが押されると呼ばれる
	@Override
	public final void keyPressed(KeyEvent e) {

		// 押されたのがEnter キーの場合
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {

			// 「 = 」ボタンが押された際と全く同じ処理を行うので、RunButtonListener の内部処理に投げる
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
