/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.presenter;

import com.rinearn.processornano.view.ViewImpl;

public final class OutputFieldUpdater implements Runnable {

	private ViewImpl view = null;
	private String outputText = null;

	protected OutputFieldUpdater(ViewImpl view, String outputText) {
		this.view = view;
		this.outputText = outputText;
	}

	@Override
	public final void run() {
		this.view.outputField.setText(this.outputText);
	}
}
