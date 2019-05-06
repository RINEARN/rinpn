/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.presenter;

import com.rinearn.processornano.model.Calculator;
import com.rinearn.processornano.view.ViewContainer;

public final class DownwardSynchronizer implements Runnable {

	private Calculator calculator = null;
	private ViewContainer view = null;

	public DownwardSynchronizer(Calculator calculator, ViewContainer view) {
		this.calculator = calculator;
		this.view = view;
	}

	@Override
	public final void run() {
		calculator.setInputText(this.view.inputField.getText());
		calculator.setOutputText(this.view.outputField.getText());
	}
}
