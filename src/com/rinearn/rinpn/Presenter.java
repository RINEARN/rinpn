/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import java.lang.reflect.InvocationTargetException;

import com.rinearn.rinpn.util.LocaleCode;
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
		view.inputField.addKeyListener(new RunKeyListener(view, model, settingContainer));
		view.inputField.addMouseListener(new InputFieldMouseListener(view));
		view.outputField.addMouseListener(new OutputFieldMouseListener(view));
		view.runButton.addActionListener(new RunButtonListener(view, model, settingContainer));
		view.exitButton.addActionListener(new ExitButtonListener(view, model, settingContainer));
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
}
