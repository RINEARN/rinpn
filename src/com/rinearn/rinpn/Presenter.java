/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.lang.reflect.InvocationTargetException;

import com.rinearn.rinpn.util.LocaleCode;
import com.rinearn.rinpn.util.MessageManager;
import com.rinearn.rinpn.util.SettingContainer;


/**
 * The class handling events occurred on the View, and invoking the corresponding processes of the Model.
 */
public final class Presenter {

	/**
	 * Links the specified View and the specified Model by Presenter's event listeners.
	 * 
	 * @param view The view to be linked.
	 * @param model The model to be linked.
	 * @param settingContainer The container storing setting values.
	 */
	public final void link(
			View view, Model model, SettingContainer settingContainer) {

		WindowMouseListener windowMouseListener = new WindowMouseListener(view);
		view.frame.addMouseListener(windowMouseListener);
		view.frame.addMouseMotionListener(windowMouseListener);

		BasePanelMouseListener basePanelMouseListener = new BasePanelMouseListener(view, settingContainer);
		view.basePanel.addMouseListener(basePanelMouseListener);
		view.basePanel.addMouseMotionListener(basePanelMouseListener);

		view.inputField.addKeyListener(new RunKeyListener(view, model, settingContainer));
		view.inputField.addMouseListener(new InputFieldMouseListener(view));
		view.outputField.addMouseListener(new OutputFieldMouseListener(view));

		view.runButton.addActionListener(new RunButtonListener(view, model, settingContainer));
		view.exitButton.addActionListener(new ExitButtonListener(view, model, settingContainer));

		KeyRetractorMouseListener keyRetractorMouseListener = new KeyRetractorMouseListener(view, settingContainer);
		view.keyRetractorLabel.addMouseListener(keyRetractorMouseListener);
		view.keyRetractorLabel.addMouseMotionListener(keyRetractorMouseListener);

		FunctionKeyActionListener functonKeyActionListener = new FunctionKeyActionListener(view);
		for(JButton key: view.functionKeyList) {
			key.addActionListener(functonKeyActionListener);
		}

		NumberKeyActionListener numberKeyActionListener = new NumberKeyActionListener(view);
		for(JButton key: view.numberKeyList) {
			key.addActionListener(numberKeyActionListener);
		}

		BehaviorKeyActionListener behaviorKeyActionListener = new BehaviorKeyActionListener(view, model, settingContainer);
		for(JButton key: view.behaviorKeyList) {
			key.addActionListener(behaviorKeyActionListener);
		}
	}


	/**
	 * The listener handling mouse events on the window.
	 */
	private static final class WindowMouseListener extends MouseAdapter {
		private View view = null;
		private int mousePressedX = -1;
		private int mousePressedY = -1;

		protected WindowMouseListener(View view) {
			this.view = view;
		}

		@Override
		public final void mousePressed(MouseEvent e) {
			mousePressedX = e.getX();
			mousePressedY = e.getY();
		}

		@Override
		public final void mouseDragged(MouseEvent e) {
			int mouseCurrentX = e.getX();
			int mouseCurrentY = e.getY();

			int dx = mouseCurrentX - mousePressedX;
			int dy = mouseCurrentY - mousePressedY;

			int x = this.view.frame.getLocation().x;
			int y = this.view.frame.getLocation().y;

			this.view.frame.setLocation(x + dx, y + dy);
		}
	}

	/**
	 * The listener handling mouse events on the base panel, for resizing the window.
	 */
	private static final class BasePanelMouseListener extends MouseAdapter {
		private View view = null;
		private SettingContainer settingContainer = null;
		private int pressedMouseAbsoluteX = -1;
		private int pressedMouseAbsoluteY = -1;
		int pressedWindowX = -1;
		int pressedWindowY = -1;
		int pressedWindowWidth = -1;
		int pressedWindowHeight = -1;
		WindowEdge pressedWindowEdge = WindowEdge.NONE;

		private enum WindowEdge {
			TOP,
			BOTTOM,
			LEFT,
			RIGHT,
			TOP_RIGHT,
			TOP_LEFT,
			BOTTOM_RIGHT,
			BOTTOM_LEFT,
			NONE
		};

		protected BasePanelMouseListener(View view, SettingContainer settingContainer) {
			this.view = view;
			this.settingContainer = settingContainer;
		}

		private WindowEdge detectWindowEdge(int mouseX, int mouseY, int windowWidth, int windowHeight) {
			WindowEdge windowEdge = WindowEdge.NONE;
			int windowEdgeWidth = View.WINDOW_EDGE_WIDTH;

			if (0 <= mouseX && mouseX < windowEdgeWidth
					&& windowEdgeWidth < mouseY && mouseY < windowHeight - windowEdgeWidth ) {
				windowEdge = WindowEdge.LEFT;
			}

			if (windowWidth - windowEdgeWidth < mouseX && mouseX <= windowWidth
					&& windowEdgeWidth < mouseY && mouseY < windowHeight - windowEdgeWidth ) {
				windowEdge = WindowEdge.RIGHT;
			}

			if (windowEdgeWidth < mouseX && mouseX < windowWidth - windowEdgeWidth
					&& 0 <= mouseY && mouseY < windowEdgeWidth ) {
				windowEdge = WindowEdge.TOP;
			}

			if (windowEdgeWidth < mouseX && mouseX < windowWidth - windowEdgeWidth
					&& windowHeight - windowEdgeWidth < mouseY && mouseY <= windowHeight ) {
				windowEdge = WindowEdge.BOTTOM;
			}
			
			if (mouseX <= windowEdgeWidth && mouseY <= windowEdgeWidth) {
				windowEdge = WindowEdge.TOP_LEFT;
			}

			if (windowWidth - windowEdgeWidth <= mouseX && mouseY <= windowEdgeWidth) {
				windowEdge = WindowEdge.TOP_RIGHT;
			}

			if (mouseX <= windowEdgeWidth && windowHeight - windowEdgeWidth <= mouseY) {
				windowEdge = WindowEdge.BOTTOM_LEFT;
			}

			if (windowWidth - windowEdgeWidth <= mouseX && windowHeight - windowEdgeWidth <= mouseY) {
				windowEdge = WindowEdge.BOTTOM_RIGHT;
			}

			return windowEdge;
		}

		@Override
		public final void mousePressed(MouseEvent e) {
			this.pressedMouseAbsoluteX = e.getX() + this.view.frame.getLocation().x;
			this.pressedMouseAbsoluteY = e.getY() + this.view.frame.getLocation().y;
			this.pressedWindowX = this.view.frame.getLocation().x;
			this.pressedWindowY = this.view.frame.getLocation().y;
			this.pressedWindowWidth = this.view.frame.getSize().width;
			this.pressedWindowHeight = this.view.frame.getSize().height;
			this.pressedWindowEdge = this.detectWindowEdge(e.getX(), e.getY(), this.pressedWindowWidth, this.pressedWindowHeight);
		}

		@Override
		public final void mouseMoved(MouseEvent e) {
			int mouseX = e.getX();
			int mouseY = e.getY();
			int windowWidth = this.view.frame.getSize().width;
			int windowHeight = this.view.frame.getSize().height;
			WindowEdge windowEdge = this.detectWindowEdge(mouseX, mouseY, windowWidth, windowHeight);

			switch (windowEdge) {
				case TOP : {
					this.view.basePanel.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
					break;
				}
				case BOTTOM : {
					this.view.basePanel.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
					break;
				}
				case RIGHT : {
					this.view.basePanel.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
					break;
				}
				case LEFT : {
					this.view.basePanel.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
					break;
				}
				case TOP_RIGHT : {
					this.view.basePanel.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
					break;
				}
				case TOP_LEFT : {
					this.view.basePanel.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
					break;
				}
				case BOTTOM_RIGHT : {
					this.view.basePanel.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
					break;
				}
				case BOTTOM_LEFT : {
					this.view.basePanel.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
					break;
				}
				case NONE : {
					this.view.basePanel.setCursor(Cursor.getDefaultCursor());
					break;
				}
				default : {
					throw new RINPnFatalException("Unexpected window edge: " + windowEdge);
				}
			}
		}

		@Override
		public final void mouseDragged(MouseEvent e) {

			// Note: Don't use the relative coordinates for the followings,
			// because they change during we are resizing the window.
			int mouseAbsoluteX = e.getX() + this.view.frame.getLocation().x;
			int mouseAbsoluteY = e.getY() + this.view.frame.getLocation().y;

			int dxFromPressedPoint = mouseAbsoluteX - this.pressedMouseAbsoluteX;
			int dyFromPressedPoint = mouseAbsoluteY - this.pressedMouseAbsoluteY;

			int resizedWindowX = this.pressedWindowX;
			int resizedWindowY = this.pressedWindowY;
			int resizedWindowWidth = this.pressedWindowWidth;
			int resizedWindowHeight = this.pressedWindowHeight;

			if (this.pressedWindowEdge == WindowEdge.TOP
					|| this.pressedWindowEdge == WindowEdge.TOP_RIGHT
					|| this.pressedWindowEdge == WindowEdge.TOP_LEFT) {

				resizedWindowY += dyFromPressedPoint;
				resizedWindowHeight -= dyFromPressedPoint;
			}

			if (this.pressedWindowEdge == WindowEdge.BOTTOM
					|| this.pressedWindowEdge == WindowEdge.BOTTOM_RIGHT
					|| this.pressedWindowEdge == WindowEdge.BOTTOM_LEFT) {

				resizedWindowHeight += dyFromPressedPoint;
			}

			if (this.pressedWindowEdge == WindowEdge.RIGHT
					|| this.pressedWindowEdge == WindowEdge.TOP_RIGHT
					|| this.pressedWindowEdge == WindowEdge.BOTTOM_RIGHT) {

				resizedWindowWidth += dxFromPressedPoint;
			}

			if (this.pressedWindowEdge == WindowEdge.LEFT
					|| this.pressedWindowEdge == WindowEdge.TOP_LEFT
					|| this.pressedWindowEdge == WindowEdge.BOTTOM_LEFT) {

				resizedWindowX += dxFromPressedPoint;
				resizedWindowWidth -= dxFromPressedPoint;
			}

			if (this.pressedWindowEdge == WindowEdge.NONE) {
				resizedWindowX = this.pressedWindowX + dxFromPressedPoint;
				resizedWindowY = this.pressedWindowY + dyFromPressedPoint;
			}

			if (resizedWindowHeight < this.settingContainer.retractedWindowHeight) {
				resizedWindowHeight = this.settingContainer.retractedWindowHeight;
			}
			if (resizedWindowWidth < View.WINDOW_MIN_WIDTH) {
				resizedWindowWidth = View.WINDOW_MIN_WIDTH;
			}

			// Resize the window.
			this.view.frame.setBounds(resizedWindowX, resizedWindowY, resizedWindowWidth, resizedWindowHeight);
			try {
				this.view.resizePanels(this.settingContainer);
			} catch (InvocationTargetException | InterruptedException ie) {
				ie.printStackTrace();
			}

			// Update the key retractor label.
			boolean retracted = (this.view.frame.getSize().height == this.settingContainer.retractedWindowHeight);
			if (retracted) {
				this.view.keyRetractorLabel.setText("▼KEY-PANEL");
			} else {
				this.view.keyRetractorLabel.setText("▲KEY-PANEL");
			}
		}
	}


	/**
	 * The listener handling mouse events on the mouse retractor label.
	 */
	private static final class KeyRetractorMouseListener extends MouseAdapter {
		private View view = null;
		private SettingContainer settingContainer = null;
		private int windowHeightBeforeRetracted = -1;

		protected KeyRetractorMouseListener(View view, SettingContainer settingContainer) {
			this.view = view;
			this.settingContainer = settingContainer;
			this.windowHeightBeforeRetracted = this.settingContainer.windowHeight;
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			this.view.keyRetractorLabel.setForeground(Color.RED);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			Color keyRetractorLabelColor = new Color(
				this.settingContainer.keyRetractorForegroundColorR,
				this.settingContainer.keyRetractorForegroundColorG,
				this.settingContainer.keyRetractorForegroundColorB
			);
			this.view.keyRetractorLabel.setForeground(keyRetractorLabelColor);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			boolean retracted = (this.view.frame.getSize().height == this.settingContainer.retractedWindowHeight);
			if (retracted) {
				this.view.frame.setBounds(
					this.view.frame.getLocation().x,
					this.view.frame.getLocation().y,
					this.view.frame.getSize().width,
					this.windowHeightBeforeRetracted
				);
				this.view.keyRetractorLabel.setText("▲KEY-PANEL");

			} else {
				this.windowHeightBeforeRetracted = this.view.frame.getSize().height;
				this.view.frame.setBounds(
					this.view.frame.getLocation().x,
					this.view.frame.getLocation().y,
					this.view.frame.getSize().width,
					this.settingContainer.retractedWindowHeight
				);
				this.view.keyRetractorLabel.setText("▼KEY-PANEL");
			}
			try {
				this.view.resizePanels(this.settingContainer);
			} catch (InvocationTargetException | InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}


	/**
	 * The listener of mouse events on the "INPUT" text field.
	 */
	private static final class InputFieldMouseListener extends MouseAdapter {
		private View view = null;

		protected InputFieldMouseListener(View view) {
			this.view = view;
		}

		@Override
		public final void mouseClicked(MouseEvent e) {
			if(javax.swing.SwingUtilities.isRightMouseButton(e)){
				this.view.textFieldPopupMenu.show(this.view.inputField, e.getX(), e.getY());
			}
		}
	}


	/**
	 * The listener of mouse events on the "OUTPUT" text field.
	 */
	private static final class OutputFieldMouseListener extends MouseAdapter {
		private View view = null;

		protected OutputFieldMouseListener(View view) {
			this.view = view;
		}

		@Override
		public final void mouseClicked(MouseEvent e) {
			if(javax.swing.SwingUtilities.isRightMouseButton(e)){
				this.view.textFieldPopupMenu.show(this.view.outputField, e.getX(), e.getY());
			}
		}
	}
	

	/**
	 * The Runnable implementation for updating the content of the "OUTPUT" text field,
	 * on the event-dispatcher thread.
	 */
	public static final class OutputFieldUpdater implements Runnable {
		private View view = null;
		private String outputText = null;

		protected OutputFieldUpdater(View view, String outputText) {
			this.view = view;
			this.outputText = outputText;
		}

		@Override
		public final void run() {
			this.view.outputField.setText(this.outputText);
		}
	}


	/**
	 * The listener handling the event of "=" button, for running a calculation or a script.
	 */
	private static final class RunButtonListener implements ActionListener {
		private Model model = null;
		private View view = null;
		private SettingContainer settingContainer = null;

		protected RunButtonListener(View view, Model model, SettingContainer settingContainer) {
			this.model = model;
			this.view = view;
			this.settingContainer = settingContainer;
		}

		@Override
		public final void actionPerformed(ActionEvent e) {
			handleEvent(this.view, this.model, this.settingContainer);
		}

		protected static void handleEvent(final View view, Model model, SettingContainer setting) {
			view.outputField.setText("RUNNING...");

			// Create a listener for updating the content of the "OUTPUT" text field,
			// which will be called back when the calculation or the scripting will have completed.
			Model.AsyncCalculationListener asyncCalcListener = new Model.AsyncCalculationListener() {
				public void calculationFinished(String outputText) {
					// The calculation/scripting and calling-back will be performed on the other thread.
					// Hence, use "SwingUtilities.invokeLayer" for updating the content of 
					// the "OUTPUT" text field on the event-dispatcher thread.
					SwingUtilities.invokeLater(new OutputFieldUpdater(view, outputText));
				}
			};

			// Perform the calculation on the other thread, asynchronously.
			// When it will have completed, the above "calculation listener" will be called back. 
			model.calculateAsynchronously(view.inputField.getText(), asyncCalcListener, setting);
		}
	}
	
	
	/**
	 * The listener handling the event of "=" button, for running a calculation or a script.
	 */
	private static final class RunKeyListener extends KeyAdapter {
		private Model model;
		private View view;
		private SettingContainer settingContainer;

		protected RunKeyListener(View view, Model model, SettingContainer settingContainer) {
			this.model = model;
			this.view = view;
			this.settingContainer = settingContainer;
		}

		@Override
		public final void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				RunButtonListener.handleEvent(this.view, this.model, this.settingContainer);
			}
		}
	}

	
	/**
	 * The listener handling the event of the exit button.
	 */
	private static final class ExitButtonListener implements ActionListener {
		private Model model = null;
		private View view = null;
		private SettingContainer settingContainer = null;

		protected ExitButtonListener(View view, Model model, SettingContainer settingContainer) {
			this.model = model;
			this.view = view;
			this.settingContainer = settingContainer;
		}

		@Override
		public final void actionPerformed(ActionEvent actionEvent) {

			// If any script is running, ask the user whether terminate it, and terminate it if YES.
			if (this.model.isCalculating()) {
				String message = "";
				if (this.settingContainer.localeCode.equals(LocaleCode.JA_JP)) {
					message = "計算処理を実行中ですが、このソフトを強制終了しますか ?";
				}
				if (this.settingContainer.localeCode.equals(LocaleCode.EN_US)) {
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
			this.model.shutdown(this.settingContainer);
		}
	}


	/**
	 * The listener handling the event of function keys (sin, cos, tan, (, ), _, and so on).
	 */
	private static final class FunctionKeyActionListener implements ActionListener {
		private View view = null;

		protected FunctionKeyActionListener(View view) {
			this.view = view;
		}
		@Override
		public final void actionPerformed(ActionEvent ae) {
			JButton key = JButton.class.cast(ae.getSource());
			String keyText = key.getText();
			String appendedText = null;

			switch (keyText) {
				case "_" : {
					appendedText = " ";
					break;
				}

				case "(" :
				case ")" : 
				case "PI" : {
					appendedText = keyText;
					break;
				}

				default : {
					appendedText = keyText + "(";
					break;
				}
			}

			int caretPosition = view.inputField.getCaretPosition();
			StringBuilder inputBuilder = new StringBuilder(this.view.inputField.getText());
			inputBuilder.insert(caretPosition, appendedText);

			this.view.inputField.setText(inputBuilder.toString());
			this.view.inputField.setCaretPosition(caretPosition + appendedText.length());
			this.view.inputField.requestFocus(true);
		}
	}


	/**
	 * The listener handling the event of number keys (1, 2, 3, +, -, and so on).
	 */
	private static final class NumberKeyActionListener implements ActionListener {
		private View view = null;

		protected NumberKeyActionListener(View view) {
			this.view = view;
		}
		@Override
		public final void actionPerformed(ActionEvent ae) {
			JButton key = JButton.class.cast(ae.getSource());

			int caretPosition = view.inputField.getCaretPosition();
			StringBuilder inputBuilder = new StringBuilder(this.view.inputField.getText());
			inputBuilder.insert(caretPosition, key.getText());

			this.view.inputField.setText(inputBuilder.toString());
			this.view.inputField.setCaretPosition(caretPosition + key.getText().length());
			this.view.inputField.requestFocus(true);
		}
	}


	/**
	 * The listener handling the event of behavior keys (=, C, BS, Script).
	 */
	private static final class BehaviorKeyActionListener implements ActionListener {
		private View view = null;
		private Model model = null;
		private SettingContainer settingContainer = null;

		protected BehaviorKeyActionListener(View view, Model model, SettingContainer settingContainer) {
			this.model = model;
			this.view = view;
			this.settingContainer = settingContainer;
		}
		@Override
		public final void actionPerformed(ActionEvent ae) {

			JButton key = JButton.class.cast(ae.getSource());
			String keyText = key.getText();

			switch (keyText) {
				case "=" : {
					RunButtonListener.handleEvent(this.view, this.model, this.settingContainer);
					break;
				}

				case "C" : {
					this.view.inputField.setText("");
					break;
				}

				case "BS" : {
					String currentText = view.inputField.getText();
					int caretPosition = view.inputField.getCaretPosition();
					if (1 <= caretPosition) {
						this.view.inputField.setText(
							currentText.substring(0, caretPosition - 1)
							+
							currentText.substring(caretPosition, currentText.length())
						);
						this.view.inputField.setCaretPosition(caretPosition - 1);
						this.view.inputField.requestFocus(true);
					}
					break;
				}

				case "Script" : {

					// Select a Vnano script file, which has the extension ".vnano"..
					JFileChooser fileChooser = new JFileChooser(".");
					FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Vnano Script File", "vnano");
					fileChooser.setFileFilter(fileFilter);
					fileChooser.showOpenDialog(this.view.frame);
					File scriptFile = fileChooser.getSelectedFile();

					// Canceled.
					if (scriptFile == null) {
						return;
					}

					// If selected a non-Vnano file.
					if (!scriptFile.getName().endsWith(".vnano") || scriptFile.isDirectory()) {
						String errorMessage = this.settingContainer.localeCode.equals(LocaleCode.JA_JP) ? 
							"選択されたファイルは、Vnano のスクリプトファイルではありません。" : 
							"The selected file is not a Vnano script file." ;
						MessageManager.showErrorMessage(errorMessage, "!", settingContainer.localeCode);
						return;
					}

					// If selected a Vnano script file, execute it.
					this.view.inputField.setText(scriptFile.getPath());
					RunButtonListener.handleEvent(view, model, settingContainer);
					break;
				}

				default : {
					throw new RINPnFatalException("Unexpected behavior key: " + keyText);
				}
			}
			view.inputField.requestFocus(true);
		}
	}
}
