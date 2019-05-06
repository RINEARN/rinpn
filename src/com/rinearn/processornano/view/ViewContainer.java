/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.view;

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

public final class ViewContainer implements ViewInterface {

	protected volatile boolean initialized = false;
	protected JFrame frame = null;
	protected JPanel basePanel = null;
	protected JPanel topPanel = null;
	protected JPanel midPanel = null;
	protected JTextField inputField = null;
	protected JTextField outputField = null;
	protected JLabel inputLabel = null;
	protected JLabel outputLabel = null;
	protected JButton runButton = null;
	protected JButton exitButton = null;
	protected JPopupMenu textFieldPopupMenu = null;

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
}
