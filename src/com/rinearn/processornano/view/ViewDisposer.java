/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.view;

public final class ViewDisposer implements Runnable {

	private ViewContainer view = null;

	public ViewDisposer(ViewContainer view) {
		this.view = view;
	}

	@Override
	public final void run() {

		if (view.initialized) {
			view.frame.dispose();
			view.frame = null;
			view.basePanel = null;
			view.midPanel = null;
			view.inputField = null;
			view.outputField = null;
			view.inputLabel = null;
			view.outputLabel = null;
			view.runButton = null;
			view.exitButton = null;
		}

		this.view = null;
	}
}
