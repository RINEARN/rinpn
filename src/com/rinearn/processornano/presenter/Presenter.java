/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.presenter;

import com.rinearn.processornano.calculator.Calculator;
import com.rinearn.processornano.spec.SettingContainer;
import com.rinearn.processornano.view.ViewContainer;

public final class Presenter {

	public final void link(
			ViewContainer view, Calculator calculator, SettingContainer setting) {

		// 各リスナを生成して投げて終わりじゃなくフィールドに保持して管理する方針の方が良い？ 後で要検討
		WindowMouseListener windowMouseListener = new WindowMouseListener(view);
		view.frame.addMouseListener(windowMouseListener);
		view.frame.addMouseMotionListener(windowMouseListener);
		view.inputField.addKeyListener(new RunKeyListener(view, calculator, setting));
		view.inputField.addMouseListener(new IOFieldMouseListener(view));
		view.outputField.addMouseListener(new IOFieldMouseListener(view));
		view.runButton.addActionListener(new RunButtonListener(view, calculator, setting));
		view.exitButton.addActionListener(new ExitButtonListener(view, calculator, setting));
	}
}
