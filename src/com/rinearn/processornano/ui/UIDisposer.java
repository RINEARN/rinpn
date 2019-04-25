/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.ui;

public final class UIDisposer implements Runnable {

	private UIContainer ui = null;

	public UIDisposer(UIContainer ui) {
		this.ui = ui;
	}

	@Override
	public final void run() {

		if (ui.initialized) {
			ui.frame.dispose();
			ui.frame = null;
			ui.basePanel = null;
			ui.midPanel = null;
			ui.inputField = null;
			ui.outputField = null;
			ui.inputLabel = null;
			ui.outputLabel = null;
			ui.runButton = null;
			ui.exitButton = null;
		}

		this.ui = null;
	}
}
