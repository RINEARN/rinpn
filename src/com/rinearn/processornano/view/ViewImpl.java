/*
 * Copyright(C) 2019-2020 RINEARN (Fumihiro Matsui)
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
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.DefaultEditorKit;

import com.rinearn.processornano.util.SettingContainer;

public final class ViewImpl implements ViewInterface {

	private volatile boolean initialized = false;
	private JFrame frame = null;
	private JPanel basePanel = null;
	private JPanel topPanel = null;
	private JPanel midPanel = null;
	private JTextField inputField = null;
	private JTextField outputField = null;
	private JLabel inputLabel = null;
	private JLabel outputLabel = null;
	private JButton runButton = null;
	private JButton exitButton = null;
	private JPopupMenu textFieldPopupMenu = null;


	@Override
	public void setOutputText(String text) {
		this.outputField.setText(text);
	}

	@Override
	public String getInputText() {
		return this.inputField.getText();
	}

	@Override
	public void setLocation(int x, int y) {
		this.frame.setLocation(x, y);
	}

	@Override
	public int getLocationX() {
		return this.frame.getX();
	}

	@Override
	public int getLocationY() {
		return this.frame.getY();
	}

	@Override
	public void popupInputFieldMenu(int x, int y) {
		this.textFieldPopupMenu.show(this.inputField, x, y);
	}

	@Override
	public void popupOutputFieldMenu(int x, int y) {
		this.textFieldPopupMenu.show(this.outputField, x, y);
	}

	@Override
	public void addFrameMouseListener(MouseListener listener) {
		this.frame.addMouseListener(listener);
	}

	@Override
	public void addFrameMouseMotionListener(MouseMotionListener listener) {
		this.frame.addMouseMotionListener(listener);
	}

	@Override
	public void addInputFieldKeyListener(KeyListener listener) {
		this.inputField.addKeyListener(listener);
	}

	@Override
	public void addInputFieldMouseListener(MouseListener listener) {
		this.inputField.addMouseListener(listener);
	}

	@Override
	public void addOutputFieldMouseListener(MouseListener listener) {
		this.outputField.addMouseListener(listener);
	}

	@Override
	public void addRunButtonActionListener(ActionListener listener) {
		this.runButton.addActionListener(listener);
	}

	@Override
	public void addExitButtonActionListener(ActionListener listener) {
		this.exitButton.addActionListener(listener);
	}

	@Override
	public boolean isInitialized() {
		return this.initialized;
	}


	// ViewInitializer を介して SwingUtilities.invokeAndWait で実行する
	@Override
	public void initialize(SettingContainer setting) {

		// ウィンドウを生成
		this.frame = new JFrame();
		this.frame.setBounds(0, 0, setting.windowWidth, setting.windowHeight);

		// 全UIの土台になるパネルを生成してウィンドウに配置
		this.basePanel = new JPanel();
		this.basePanel.setLayout(new GridLayout(4, 1));
		this.frame.getContentPane().add(this.basePanel);

		// 最上段の水平パネル（「INPUT」のラベルがあるパネル）を生成してい配置
		this.topPanel = new JPanel();
		this.topPanel.setLayout(new GridLayout(1, 2));
		this.basePanel.add(this.topPanel);

		// 「INPUT」と表示するラベルを生成して配置
		this.inputLabel = new JLabel("  ▼INPUT", JLabel.LEFT);
		this.inputLabel.setFont(SettingContainer.LABEL_FONT);
		this.topPanel.add(this.inputLabel);

		// 入出力フィールドのフォントを生成
		Font textFieldFont = new Font(
			SettingContainer.TEXT_FIELD_FONT_NAME, SettingContainer.TEXT_FIELD_FONT_TYPE, setting.textFieldFontSize
		);

		// 入力フィールドを生成して配置
		this.inputField = new JTextField();
		this.inputField.setFont(textFieldFont);
		this.basePanel.add(this.inputField);

		// 中段の水平パネル（「OUTPUT」のラベルがあるパネル）を生成して配置
		this.midPanel = new JPanel();
		this.midPanel.setLayout(new GridLayout(1, 2));
		//midPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.basePanel.add(this.midPanel);

		// 出力フィールドの上に「OUTPUT」と表示するラベルを生成して配置
		this.outputLabel = new JLabel("  ▼OUTPUT   ", JLabel.LEFT);
		this.outputLabel.setFont(SettingContainer.LABEL_FONT);
		this.midPanel.add(this.outputLabel);

		// 出力フィールドを生成して配置
		this.outputField = new JTextField();
		this.outputField.setFont(textFieldFont);
		this.basePanel.add(this.outputField);


		// 以下、水平パネルの上にボタンを並べる

		JPanel midButtonPanel = new JPanel();
		midButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.midPanel.add(midButtonPanel);

		// 「RUN」ボタンを生成して配置
		this.runButton = new JButton("=");
		this.runButton.setFont(SettingContainer.BUTTON_FONT);
		midButtonPanel.add(this.runButton);

		// Exitボタンを右端に配置するためにもう一枚パネルを挟む
		JPanel topButtonPanel = new JPanel();
		topButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.topPanel.add(topButtonPanel);

		// 「EXIT」ボタンを生成して配置
		this.exitButton = new JButton("×");
		this.exitButton.setFont(SettingContainer.EXIT_BUTTON_FONT);
		this.exitButton.setForeground(Color.RED);
		topButtonPanel.add(this.exitButton);

		// テキストフィールドの右クリックメニューを生成
		this.textFieldPopupMenu = new JPopupMenu();
		this.textFieldPopupMenu.add(new DefaultEditorKit.CutAction()).setText("Cut");
		this.textFieldPopupMenu.add(new DefaultEditorKit.CopyAction()).setText("Copy");
		this.textFieldPopupMenu.add(new DefaultEditorKit.PasteAction()).setText("Paste");


		// ウィンドウの透過処理などの設定（可能であれば）
		// ※ setting 内の設定値の正当性は checkSettingValues で検査済み
		GraphicsDevice graphicsDevide = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		if (graphicsDevide.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
			this.frame.setUndecorated(true);
			this.frame.setAlwaysOnTop(setting.stayOnTopOfAllWindows);
			this.frame.setOpacity((float)setting.windowOpacity);

			this.topPanel.setBackground(new Color(0, 0, 0, 0));
			this.midPanel.setBackground(new Color(0, 0, 0, 0));
			midButtonPanel.setBackground(new Color(0, 0, 0, 0));
			topButtonPanel.setBackground(new Color(0, 0, 0, 0));

			Color windowBackgroundColor = new Color(
				setting.windowBackgroundColorR,
				setting.windowBackgroundColorG,
				setting.windowBackgroundColorB
			);
			this.basePanel.setBackground(windowBackgroundColor);

			this.runButton.setBackground(new Color(255, 255, 255));
			this.exitButton.setBackground(new Color(255, 255, 255));

			this.inputLabel.setForeground(new Color(0, 0, 0, 120));
			this.outputLabel.setForeground(new Color(0, 0, 0, 120));

			Color textFieldBackgroundColor = new Color(
				setting.textFieldBackgroundColorR,
				setting.textFieldBackgroundColorG,
				setting.textFieldBackgroundColorB
			);
			this.inputField.setBackground(textFieldBackgroundColor);
			this.outputField.setBackground(textFieldBackgroundColor);

			Color textFieldForegroundColor = new Color(
				setting.textFieldForegroundColorR,
				setting.textFieldForegroundColorG,
				setting.textFieldForegroundColorB
			);
			this.inputField.setForeground(textFieldForegroundColor);
			this.outputField.setForeground(textFieldForegroundColor);

			this.inputField.setCaretColor(textFieldForegroundColor);
			this.outputField.setCaretColor(textFieldForegroundColor);
		}

		// ウィンドウを表示
		this.frame.setVisible(true);

		this.initialized = true;
	}


	// イベントスレッド内からは直接呼び、
	// それ以外からは ViewDisposer を介して SwingUtilities.invokeAndWait で実行する
	@Override
	public void dispose() {
		this.frame.dispose();
		this.frame = null;
		this.basePanel = null;
		this.midPanel = null;
		this.inputField = null;
		this.outputField = null;
		this.inputLabel = null;
		this.outputLabel = null;
		this.runButton = null;
		this.exitButton = null;
	}
}
