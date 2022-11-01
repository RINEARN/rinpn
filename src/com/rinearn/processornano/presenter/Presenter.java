/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.presenter;

import com.rinearn.processornano.model.CalculatorModel;
import com.rinearn.processornano.util.SettingContainer;
import com.rinearn.processornano.view.ViewImpl;

public final class Presenter {

	public final void link(
			ViewImpl view, CalculatorModel calculator, SettingContainer setting) {

		WindowMouseListener windowMouseListener = new WindowMouseListener(view);
		view.frame.addMouseListener(windowMouseListener);
		view.frame.addMouseMotionListener(windowMouseListener);
		view.inputField.addKeyListener(new RunKeyListener(view, calculator, setting));
		view.inputField.addMouseListener(new InputFieldMouseListener(view));
		view.outputField.addMouseListener(new OutputFieldMouseListener(view));
		view.runButton.addActionListener(new RunButtonListener(view, calculator, setting));
		view.exitButton.addActionListener(new ExitButtonListener(view, calculator, setting));
	}
}
