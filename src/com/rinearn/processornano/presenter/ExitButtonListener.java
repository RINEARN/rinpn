/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.presenter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import com.rinearn.processornano.model.Calculator;
import com.rinearn.processornano.spec.LocaleCode;
import com.rinearn.processornano.spec.SettingContainer;
import com.rinearn.processornano.view.ViewContainer;
import com.rinearn.processornano.view.ViewDisposer;

public final class ExitButtonListener implements ActionListener {

	private Calculator calculator = null;
	private ViewContainer view = null;
	private SettingContainer setting = null;

	public ExitButtonListener(ViewContainer view, Calculator calculator, SettingContainer setting) {
		this.calculator = calculator;
		this.view = view;
		this.setting = setting;
	}

	@Override
	public final void actionPerformed(ActionEvent actionEvent) {

		// スクリプトが実行中の場合は、強制終了するかどうか尋ねて、YESなら強制終了する
		if (calculator.isRunning()) {

			// 尋ねるメッセージを用意
			String message = "";
			if (this.setting.localeCode.equals(LocaleCode.JA_JP)) {
				message = "計算処理を実行中ですが、このソフトを強制終了しますか ?";
			}
			if (this.setting.localeCode.equals(LocaleCode.EN_US)) {
				message = "The calculation is running. Do you want to force-quit this software ?";
			}

			// ユーザーに尋ねる
			int quitOrNot = JOptionPane.showConfirmDialog(
				null, message, "!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
			);

			// YESが選択されていたら強制終了、NOなら何もしない
			if (quitOrNot == JOptionPane.YES_OPTION) {
				System.exit(0);
			} else {
				return;
			}
		}

		// UIを破棄（ここはイベントスレッド内なので、SwingUtilities.invokeAndWait はせずrunを直接呼ぶ）
		new ViewDisposer(this.view).run();
	}
}
