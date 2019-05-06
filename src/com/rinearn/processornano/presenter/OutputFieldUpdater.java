/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.presenter;

import com.rinearn.processornano.view.ViewContainer;

public final class OutputFieldUpdater implements Runnable {

	private ViewContainer view = null;
	private String outputText = null;

	public OutputFieldUpdater(ViewContainer view, String outputText) {
		this.view = view;
		this.outputText = outputText;
	}

	@Override
	public final void run() {
		this.view.outputField.setText(this.outputText);
	}
}
