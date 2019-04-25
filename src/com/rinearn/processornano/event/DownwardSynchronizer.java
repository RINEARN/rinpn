/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.event;

import com.rinearn.processornano.calculator.Calculator;
import com.rinearn.processornano.ui.UIContainer;

public final class DownwardSynchronizer implements Runnable {

	private Calculator calculator = null;
	private UIContainer ui = null;

	public DownwardSynchronizer(Calculator calculator, UIContainer ui) {
		this.calculator = calculator;
		this.ui = ui;
	}

	@Override
	public final void run() {
		calculator.setInputText(this.ui.inputField.getText());
		calculator.setOutputText(this.ui.outputField.getText());
	}
}
