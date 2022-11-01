/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.DefaultEditorKit;

import com.rinearn.rinpn.util.SettingContainer;

import javax.swing.SwingUtilities;


/**
 * The class providing UI components of this application.
 */
public final class View {
	
	/** The flag storing whether the UI components have already been initialized. */
	private volatile boolean initialized = false;

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


	/**
	 * Checks whether the UI components have already been initialized.
	 * 
	 * @return Returns true if they have been initialized.
	 */
	public synchronized boolean isInitialized() {
		return initialized;
	}


	/**
	 * Initializes the UI resources.
	 * 
	 * @param settingContainer The container storing setting values.
	 * @throws InterruptedException Thrown when the processing thread has been interrupted.
	 * @throws InvocationTargetException Thrown when any error occurred on the processing thread..
	 */
	public void initialize(SettingContainer settingContainer) throws InvocationTargetException, InterruptedException {
		if (this.initialized) {
			return;
		}
		if (SwingUtilities.isEventDispatchThread()) {
			new ViewInitializer(settingContainer).run();
		} else {
			SwingUtilities.invokeAndWait(new ViewInitializer(settingContainer));
		}
		this.initialized = true;
	}


	/**
	 * The Runnable implementation for initializing the UI components on the event-dispatcher thread.
	 */
	private class ViewInitializer implements Runnable {
		
		/** The container storing setting values. */
		private SettingContainer settingContainer;
		
		/**
		 * Creates new initializer of the UI.
		 * 
		 * @param setting The container storing setting values.
		 */
		public ViewInitializer(SettingContainer settingContainer) {
			this.settingContainer = settingContainer;
		}
		
		public void run() {
			
			// Create the window frame.
			frame = new JFrame();
			frame.setBounds(0, 0, this.settingContainer.windowWidth, this.settingContainer.windowHeight);

			// Create the base panel, on which all UI components will be put.
			basePanel = new JPanel();
			basePanel.setLayout(new GridLayout(4, 1));
			frame.getContentPane().add(basePanel);

			// Create the top horizontal panel (on which "INPUT" label will be put).
			topPanel = new JPanel();
			topPanel.setLayout(new GridLayout(1, 2));
			basePanel.add(topPanel);

			// Create the "INPUT" label.
			inputLabel = new JLabel("  ▼INPUT", JLabel.LEFT);
			inputLabel.setFont(SettingContainer.LABEL_FONT);
			topPanel.add(inputLabel);

			// Create fonts for the "INPUT" / "OUTPUT" text fields.
			Font textFieldFont = new Font(
				SettingContainer.TEXT_FIELD_FONT_NAME,
				SettingContainer.TEXT_FIELD_FONT_TYPE,
				this.settingContainer.textFieldFontSize
			);

			// Create the "INPUT" text field.
			inputField = new JTextField();
			inputField.setFont(textFieldFont);
			basePanel.add(inputField);

			// Create the mid horizontal panel (on which "OUTPUT" label will be put).
			midPanel = new JPanel();
			midPanel.setLayout(new GridLayout(1, 2));
			basePanel.add(midPanel);

			// Create the "OUTPUT" label.
			outputLabel = new JLabel("  ▼OUTPUT   ", JLabel.LEFT);
			outputLabel.setFont(SettingContainer.LABEL_FONT);
			midPanel.add(outputLabel);

			// Create the "OUTPUT" text field.
			outputField = new JTextField();
			outputField.setFont(textFieldFont);
			basePanel.add(outputField);

			// Create right-clicking menu for the "INPUT" / "OUTPUT" text fields.
			textFieldPopupMenu = new JPopupMenu();
			textFieldPopupMenu.add(new DefaultEditorKit.CutAction()).setText("Cut");
			textFieldPopupMenu.add(new DefaultEditorKit.CopyAction()).setText("Copy");
			textFieldPopupMenu.add(new DefaultEditorKit.PasteAction()).setText("Paste");


			// Create a panel, for aligning the "=" button to the right-side.
			JPanel midButtonPanel = new JPanel();
			midButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			midPanel.add(midButtonPanel);

			// Create "=" button.
			runButton = new JButton("=");
			runButton.setFont(SettingContainer.BUTTON_FONT);
			midButtonPanel.add(runButton);

			// Create a panel, for aligning the exit button to the right-side.
			JPanel topButtonPanel = new JPanel();
			topButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			topPanel.add(topButtonPanel);

			// Create the "exit" button.
			exitButton = new JButton("×");
			exitButton.setFont(SettingContainer.EXIT_BUTTON_FONT);
			exitButton.setForeground(Color.RED);
			topButtonPanel.add(exitButton);


			// Make the window frame semi-transparent, if possible.
			GraphicsDevice graphicsDevide = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			if (graphicsDevide.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
				frame.setUndecorated(true);
				frame.setAlwaysOnTop(this.settingContainer.stayOnTopOfAllWindows);
				frame.setOpacity((float)this.settingContainer.windowOpacity);

				topPanel.setBackground(new Color(0, 0, 0, 0));
				midPanel.setBackground(new Color(0, 0, 0, 0));
				midButtonPanel.setBackground(new Color(0, 0, 0, 0));
				topButtonPanel.setBackground(new Color(0, 0, 0, 0));

				Color windowBackgroundColor = new Color(
					this.settingContainer.windowBackgroundColorR,
					this.settingContainer.windowBackgroundColorG,
					this.settingContainer.windowBackgroundColorB
				);
				basePanel.setBackground(windowBackgroundColor);

				runButton.setBackground(new Color(255, 255, 255));
				exitButton.setBackground(new Color(255, 255, 255));

				inputLabel.setForeground(new Color(0, 0, 0, 120));
				outputLabel.setForeground(new Color(0, 0, 0, 120));

				Color textFieldBackgroundColor = new Color(
					this.settingContainer.textFieldBackgroundColorR,
					this.settingContainer.textFieldBackgroundColorG,
					this.settingContainer.textFieldBackgroundColorB
				);
				inputField.setBackground(textFieldBackgroundColor);
				outputField.setBackground(textFieldBackgroundColor);

				Color textFieldForegroundColor = new Color(
					this.settingContainer.textFieldForegroundColorR,
					this.settingContainer.textFieldForegroundColorG,
					this.settingContainer.textFieldForegroundColorB
				);
				inputField.setForeground(textFieldForegroundColor);
				outputField.setForeground(textFieldForegroundColor);

				inputField.setCaretColor(textFieldForegroundColor);
				outputField.setCaretColor(textFieldForegroundColor);
			}

			// Make the window visible.
			frame.setVisible(true);
		}
	}


	/**
	 * Disposes the UI resources.
	 * 
	 * @throws InterruptedException Thrown when the processing thread has been interrupted.
	 * @throws InvocationTargetException Thrown when any error occurred on the processing thread..
	 */
	public void dispose() throws InvocationTargetException, InterruptedException {
		if (SwingUtilities.isEventDispatchThread()) {
			new ViewDisposer().run();
		} else {
			SwingUtilities.invokeAndWait(new ViewDisposer());
		}
		this.initialized = true;
	}

	/**
	 * The Runnable implementation for disposing the UI components on the event-dispatcher thread.
	 */
	private class ViewDisposer implements Runnable {
		
		/**
		 * Creates new disposer of the UI.
		 */
		public ViewDisposer() {
		}
		
		@Override
		public void run() {
			frame.dispose();
			frame = null;
			basePanel = null;
			midPanel = null;
			inputField = null;
			outputField = null;
			inputLabel = null;
			outputLabel = null;
			runButton = null;
			exitButton = null;
		}
	}
}
