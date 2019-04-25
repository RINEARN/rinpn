/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.event;

import com.rinearn.processornano.calculator.Calculator;
import com.rinearn.processornano.spec.SettingContainer;
import com.rinearn.processornano.ui.UIContainer;

public final class EventListenerManager {

	public final static void addAllEventListenersToUI(UIContainer ui, Calculator calculator, SettingContainer setting) {

		// 各リスナを生成して投げて終わりじゃなくフィールドに保持して管理する方針の方が良い？ 後で要検討
		WindowMouseListener windowMouseListener = new WindowMouseListener(ui);
		ui.frame.addMouseListener(windowMouseListener);
		ui.frame.addMouseMotionListener(windowMouseListener);
		ui.inputField.addKeyListener(new RunKeyListener(ui, calculator, setting));
		ui.inputField.addMouseListener(new IOFieldMouseListener(ui));
		ui.outputField.addMouseListener(new IOFieldMouseListener(ui));
		ui.runButton.addActionListener(new RunButtonListener(ui, calculator, setting));
		ui.exitButton.addActionListener(new ExitButtonListener(ui, calculator, setting));
	}
}
