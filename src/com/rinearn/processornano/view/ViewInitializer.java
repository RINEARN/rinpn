/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.view;

import com.rinearn.processornano.spec.SettingContainer;

public final class ViewInitializer implements Runnable {

	private ViewContainer view = null;
	private SettingContainer setting = null;

	public ViewInitializer(ViewContainer view, SettingContainer setting) {
		this.view = view;
		this.setting = setting;
	}

	@Override
	public final void run() {
		this.view.initialize(this.setting);
	}
}
