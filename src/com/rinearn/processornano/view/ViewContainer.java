/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

public final class ViewContainer {
	public volatile boolean initialized = false;
	public JFrame frame = null;
	public JPanel basePanel = null;
	public JPanel topPanel = null;
	public JPanel midPanel = null;
	public JTextField inputField = null;
	public JTextField outputField = null;
	public JLabel inputLabel = null;
	public JLabel outputLabel = null;
	public JButton runButton = null;
	public JButton exitButton = null;
	public JPopupMenu textFieldPopupMenu = null;
}
