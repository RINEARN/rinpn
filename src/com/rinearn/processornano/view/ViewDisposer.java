/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.view;

public final class ViewDisposer implements Runnable {

	private ViewInterface view = null;

	public ViewDisposer(ViewInterface view) {
		this.view = view;
	}

	@Override
	public final void run() {
		if (this.view.isInitialized()) {
			this.view.dispose();
		}
		this.view = null;
	}
}
