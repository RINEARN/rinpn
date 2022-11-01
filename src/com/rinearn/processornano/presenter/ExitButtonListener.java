/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.presenter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JOptionPane;

import com.rinearn.processornano.model.CalculatorModel;
import com.rinearn.processornano.util.LocaleCode;
import com.rinearn.processornano.util.SettingContainer;
import com.rinearn.processornano.view.View;

public final class ExitButtonListener implements ActionListener {

	private CalculatorModel calculator = null;
	private View view = null;
	private SettingContainer setting = null;

	protected ExitButtonListener(View view, CalculatorModel calculator, SettingContainer setting) {
		this.calculator = calculator;
		this.view = view;
		this.setting = setting;
	}

	@Override
	public final void actionPerformed(ActionEvent actionEvent) {

		// If any script is running, ask the user whether terminate it, and terminate it if YES.
		if (calculator.isCalculating()) {
			String message = "";
			if (this.setting.localeCode.equals(LocaleCode.JA_JP)) {
				message = "計算処理を実行中ですが、このソフトを強制終了しますか ?";
			}
			if (this.setting.localeCode.equals(LocaleCode.EN_US)) {
				message = "The calculation is running. Do you want to force-quit this software ?";
			}
			int quitOrNot = JOptionPane.showConfirmDialog(
				null, message, "!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
			);
			if (quitOrNot == JOptionPane.YES_OPTION) {
				System.exit(0);
			} else {
				return;
			}
		}

		// Dispose the UI resources.
		try {
			this.view.dispose();
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}

		// Invoke the shutdown process of the model.
		this.calculator.shutdown(this.setting);
	}
}
