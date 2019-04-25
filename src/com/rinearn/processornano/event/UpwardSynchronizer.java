/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.event;

import com.rinearn.processornano.calculator.Calculator;
import com.rinearn.processornano.ui.UIContainer;

public final class UpwardSynchronizer implements Runnable {

	private Calculator calculator = null;
	private UIContainer ui = null;

	public UpwardSynchronizer(Calculator calculator, UIContainer ui) {
		this.calculator = calculator;
		this.ui = ui;
	}

	@Override
	public final void run() {
		this.ui.inputField.setText(calculator.getInputText());
		this.ui.outputField.setText(calculator.getOutputText());
	}
}
