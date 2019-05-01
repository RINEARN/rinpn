/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.GraphicsDevice.WindowTranslucency;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.DefaultEditorKit;

import com.rinearn.processornano.spec.SettingContainer;

public final class UIInitializer implements Runnable {

	private UIContainer ui = null;
	private SettingContainer setting = null;

	public UIInitializer(UIContainer ui, SettingContainer setting) {
		this.ui = ui;
		this.setting = setting;
	}

	@Override
	public final void run() {

		// ※ イベントリスナの登録は event.EventListenerManager.addAllEventListenerToUI で行う

		// ウィンドウを生成
		this.ui.frame = new JFrame();
		this.ui.frame.setBounds(0, 0, setting.windowWidth, setting.windowHeight);

		// 全UIの土台になるパネルを生成してウィンドウに配置
		this.ui.basePanel = new JPanel();
		this.ui.basePanel.setLayout(new GridLayout(4, 1));
		this.ui.frame.getContentPane().add(this.ui.basePanel);

		// 最上段の水平パネル（「INPUT」のラベルがあるパネル）を生成してい配置
		this.ui.topPanel = new JPanel();
		this.ui.topPanel.setLayout(new GridLayout(1, 2));
		this.ui.basePanel.add(this.ui.topPanel);

		// 「INPUT」と表示するラベルを生成して配置
		this.ui.inputLabel = new JLabel("  ▼INPUT", JLabel.LEFT);
		this.ui.inputLabel.setFont(SettingContainer.LABEL_FONT);
		this.ui.topPanel.add(this.ui.inputLabel);

		// 入出力フィールドのフォントを生成
		Font textFieldFont = new Font(
			SettingContainer.TEXT_FIELD_FONT_NAME, SettingContainer.TEXT_FIELD_FONT_TYPE, setting.textFieldFontSize
		);

		// 入力フィールドを生成して配置
		this.ui.inputField = new JTextField();
		this.ui.inputField.setFont(textFieldFont);
		this.ui.basePanel.add(this.ui.inputField);

		// 中段の水平パネル（「OUTPUT」のラベルがあるパネル）を生成して配置
		this.ui.midPanel = new JPanel();
		this.ui.midPanel.setLayout(new GridLayout(1, 2));
		//midPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.ui.basePanel.add(this.ui.midPanel);

		// 出力フィールドの上に「OUTPUT」と表示するラベルを生成して配置
		this.ui.outputLabel = new JLabel("  ▼OUTPUT   ", JLabel.LEFT);
		this.ui.outputLabel.setFont(SettingContainer.LABEL_FONT);
		this.ui.midPanel.add(this.ui.outputLabel);

		// 出力フィールドを生成して配置
		this.ui.outputField = new JTextField();
		this.ui.outputField.setFont(textFieldFont);
		this.ui.basePanel.add(this.ui.outputField);


		// 以下、水平パネルの上にボタンを並べる

		JPanel midButtonPanel = new JPanel();
		midButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.ui.midPanel.add(midButtonPanel);

		// 「RUN」ボタンを生成して配置
		this.ui.runButton = new JButton("=");
		this.ui.runButton.setFont(SettingContainer.BUTTON_FONT);
		midButtonPanel.add(this.ui.runButton);

		// Exitボタンを右端に配置するためにもう一枚パネルを挟む
		JPanel topButtonPanel = new JPanel();
		topButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.ui.topPanel.add(topButtonPanel);

		// 「EXIT」ボタンを生成して配置
		this.ui.exitButton = new JButton("×");
		this.ui.exitButton.setFont(SettingContainer.EXIT_BUTTON_FONT);
		this.ui.exitButton.setForeground(Color.RED);
		topButtonPanel.add(this.ui.exitButton);

		// テキストフィールドの右クリックメニューを生成
		this.ui.textFieldPopupMenu = new JPopupMenu();
		this.ui.textFieldPopupMenu.add(new DefaultEditorKit.CutAction()).setText("Cut");
		this.ui.textFieldPopupMenu.add(new DefaultEditorKit.CopyAction()).setText("Copy");
		this.ui.textFieldPopupMenu.add(new DefaultEditorKit.PasteAction()).setText("Paste");


		// ウィンドウの透過処理などの設定（可能であれば）
		// ※ setting 内の設定値の正当性は checkSettingValues で検査済み
		GraphicsDevice graphicsDevide = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		if (graphicsDevide.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
			this.ui.frame.setUndecorated(true);
			this.ui.frame.setAlwaysOnTop(setting.stayOnTopOfAllWindows);
			this.ui.frame.setOpacity((float)setting.windowOpacity);

			this.ui.topPanel.setBackground(new Color(0, 0, 0, 0));
			this.ui.midPanel.setBackground(new Color(0, 0, 0, 0));
			midButtonPanel.setBackground(new Color(0, 0, 0, 0));
			topButtonPanel.setBackground(new Color(0, 0, 0, 0));

			Color windowBackgroundColor = new Color(
				setting.windowBackgroundColorR,
				setting.windowBackgroundColorG,
				setting.windowBackgroundColorB
			);
			this.ui.basePanel.setBackground(windowBackgroundColor);

			this.ui.runButton.setBackground(new Color(255, 255, 255));
			this.ui.exitButton.setBackground(new Color(255, 255, 255));

			this.ui.inputLabel.setForeground(new Color(0, 0, 0, 120));
			this.ui.outputLabel.setForeground(new Color(0, 0, 0, 120));

			Color textFieldBackgroundColor = new Color(
				setting.textFieldBackgroundColorR,
				setting.textFieldBackgroundColorG,
				setting.textFieldBackgroundColorB
			);
			this.ui.inputField.setBackground(textFieldBackgroundColor);
			this.ui.outputField.setBackground(textFieldBackgroundColor);

			Color textFieldForegroundColor = new Color(
				setting.textFieldForegroundColorR,
				setting.textFieldForegroundColorG,
				setting.textFieldForegroundColorB
			);
			this.ui.inputField.setForeground(textFieldForegroundColor);
			this.ui.outputField.setForeground(textFieldForegroundColor);

			this.ui.inputField.setCaretColor(textFieldForegroundColor);
			this.ui.outputField.setCaretColor(textFieldForegroundColor);
		}

		// ウィンドウを表示
		this.ui.frame.setVisible(true);

		this.ui.initialized = true;
	}
}
