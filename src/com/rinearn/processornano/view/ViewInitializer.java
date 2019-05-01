/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.view;

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

public final class ViewInitializer implements Runnable {

	private ViewContainer view = null;
	private SettingContainer setting = null;

	public ViewInitializer(ViewContainer view, SettingContainer setting) {
		this.view = view;
		this.setting = setting;
	}

	@Override
	public final void run() {

		// ※ イベントリスナの登録は event.EventListenerManager.addAllEventListenerToUI で行う

		// ウィンドウを生成
		this.view.frame = new JFrame();
		this.view.frame.setBounds(0, 0, setting.windowWidth, setting.windowHeight);

		// 全UIの土台になるパネルを生成してウィンドウに配置
		this.view.basePanel = new JPanel();
		this.view.basePanel.setLayout(new GridLayout(4, 1));
		this.view.frame.getContentPane().add(this.view.basePanel);

		// 最上段の水平パネル（「INPUT」のラベルがあるパネル）を生成してい配置
		this.view.topPanel = new JPanel();
		this.view.topPanel.setLayout(new GridLayout(1, 2));
		this.view.basePanel.add(this.view.topPanel);

		// 「INPUT」と表示するラベルを生成して配置
		this.view.inputLabel = new JLabel("  ▼INPUT", JLabel.LEFT);
		this.view.inputLabel.setFont(SettingContainer.LABEL_FONT);
		this.view.topPanel.add(this.view.inputLabel);

		// 入出力フィールドのフォントを生成
		Font textFieldFont = new Font(
			SettingContainer.TEXT_FIELD_FONT_NAME, SettingContainer.TEXT_FIELD_FONT_TYPE, setting.textFieldFontSize
		);

		// 入力フィールドを生成して配置
		this.view.inputField = new JTextField();
		this.view.inputField.setFont(textFieldFont);
		this.view.basePanel.add(this.view.inputField);

		// 中段の水平パネル（「OUTPUT」のラベルがあるパネル）を生成して配置
		this.view.midPanel = new JPanel();
		this.view.midPanel.setLayout(new GridLayout(1, 2));
		//midPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.view.basePanel.add(this.view.midPanel);

		// 出力フィールドの上に「OUTPUT」と表示するラベルを生成して配置
		this.view.outputLabel = new JLabel("  ▼OUTPUT   ", JLabel.LEFT);
		this.view.outputLabel.setFont(SettingContainer.LABEL_FONT);
		this.view.midPanel.add(this.view.outputLabel);

		// 出力フィールドを生成して配置
		this.view.outputField = new JTextField();
		this.view.outputField.setFont(textFieldFont);
		this.view.basePanel.add(this.view.outputField);


		// 以下、水平パネルの上にボタンを並べる

		JPanel midButtonPanel = new JPanel();
		midButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.view.midPanel.add(midButtonPanel);

		// 「RUN」ボタンを生成して配置
		this.view.runButton = new JButton("=");
		this.view.runButton.setFont(SettingContainer.BUTTON_FONT);
		midButtonPanel.add(this.view.runButton);

		// Exitボタンを右端に配置するためにもう一枚パネルを挟む
		JPanel topButtonPanel = new JPanel();
		topButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.view.topPanel.add(topButtonPanel);

		// 「EXIT」ボタンを生成して配置
		this.view.exitButton = new JButton("×");
		this.view.exitButton.setFont(SettingContainer.EXIT_BUTTON_FONT);
		this.view.exitButton.setForeground(Color.RED);
		topButtonPanel.add(this.view.exitButton);

		// テキストフィールドの右クリックメニューを生成
		this.view.textFieldPopupMenu = new JPopupMenu();
		this.view.textFieldPopupMenu.add(new DefaultEditorKit.CutAction()).setText("Cut");
		this.view.textFieldPopupMenu.add(new DefaultEditorKit.CopyAction()).setText("Copy");
		this.view.textFieldPopupMenu.add(new DefaultEditorKit.PasteAction()).setText("Paste");


		// ウィンドウの透過処理などの設定（可能であれば）
		// ※ setting 内の設定値の正当性は checkSettingValues で検査済み
		GraphicsDevice graphicsDevide = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		if (graphicsDevide.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
			this.view.frame.setUndecorated(true);
			this.view.frame.setAlwaysOnTop(setting.stayOnTopOfAllWindows);
			this.view.frame.setOpacity((float)setting.windowOpacity);

			this.view.topPanel.setBackground(new Color(0, 0, 0, 0));
			this.view.midPanel.setBackground(new Color(0, 0, 0, 0));
			midButtonPanel.setBackground(new Color(0, 0, 0, 0));
			topButtonPanel.setBackground(new Color(0, 0, 0, 0));

			Color windowBackgroundColor = new Color(
				setting.windowBackgroundColorR,
				setting.windowBackgroundColorG,
				setting.windowBackgroundColorB
			);
			this.view.basePanel.setBackground(windowBackgroundColor);

			this.view.runButton.setBackground(new Color(255, 255, 255));
			this.view.exitButton.setBackground(new Color(255, 255, 255));

			this.view.inputLabel.setForeground(new Color(0, 0, 0, 120));
			this.view.outputLabel.setForeground(new Color(0, 0, 0, 120));

			Color textFieldBackgroundColor = new Color(
				setting.textFieldBackgroundColorR,
				setting.textFieldBackgroundColorG,
				setting.textFieldBackgroundColorB
			);
			this.view.inputField.setBackground(textFieldBackgroundColor);
			this.view.outputField.setBackground(textFieldBackgroundColor);

			Color textFieldForegroundColor = new Color(
				setting.textFieldForegroundColorR,
				setting.textFieldForegroundColorG,
				setting.textFieldForegroundColorB
			);
			this.view.inputField.setForeground(textFieldForegroundColor);
			this.view.outputField.setForeground(textFieldForegroundColor);

			this.view.inputField.setCaretColor(textFieldForegroundColor);
			this.view.outputField.setCaretColor(textFieldForegroundColor);
		}

		// ウィンドウを表示
		this.view.frame.setVisible(true);

		this.view.initialized = true;
	}
}
