/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.presenter;

import com.rinearn.processornano.model.CalculatorModel;
import com.rinearn.processornano.spec.SettingContainer;
import com.rinearn.processornano.view.ViewInterface;

public final class Presenter {

	public final void link(
			ViewInterface view, CalculatorModel calculator, SettingContainer setting) {

		// 各リスナを生成して投げて終わりじゃなくフィールドに保持して管理する方針の方が良い？ 後で要検討
		WindowMouseListener windowMouseListener = new WindowMouseListener(view);
		view.addFrameMouseListener(windowMouseListener);
		view.addFrameMouseMotionListener(windowMouseListener);
		view.addInputFieldKeyListener(new RunKeyListener(view, calculator, setting));
		view.addInputFieldMouseListener(new InputFieldMouseListener(view));
		view.addOutputFieldMouseListener(new OutputFieldMouseListener(view));
		view.addRunButtonActionListener(new RunButtonListener(view, calculator, setting));
		view.addExitButtonActionListener(new ExitButtonListener(view, calculator, setting));
	}
}
