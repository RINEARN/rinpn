/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.DefaultEditorKit;
import javax.swing.border.LineBorder;

import com.rinearn.rinpn.util.SettingContainer;

import javax.swing.SwingUtilities;


/**
 * The class providing UI components of this application.
 */
public final class View {

	/** The flag storing whether the UI components have already been initialized. */
	private volatile boolean initialized = false;

	public static final int WINDOW_EDGE_WIDTH = 5;
	public static final int WINDOW_MIN_WIDTH = 200;
	public static final int WINDOW_MIN_HEIGHT = 100;

	public JFrame frame = null;
	public JPanel basePanel = null;
	public JPanel mainPanel = null;
	public JPanel keyPanel = null;

	public JTextField inputField = null;
	public JTextField outputField = null;
	public JLabel inputLabel = null;
	public JLabel outputLabel = null;
	public JLabel keyRetractorLabel = null;

	public JButton runButton = null;
	public JButton exitButton = null;
	public JPopupMenu textFieldPopupMenu = null;

	public List<JButton> keyPanelButtonList = null;


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

			// The base panel, on which the main panel is added.
			// There are narrow margins at the top/bottom/right/left edges of the base panel, 
			// and users can resize the window by mouse-dragging one of the margins.
			basePanel = new JPanel();
			basePanel.setBorder(new LineBorder(
				new Color(
					this.settingContainer.windowBackgroundColorR,
					this.settingContainer.windowBackgroundColorG,
					this.settingContainer.windowBackgroundColorB
				),
				WINDOW_EDGE_WIDTH)
			);
			basePanel.setLayout(null);
			frame.getContentPane().add(basePanel);

			// Create the main panel, on which "INPUT" / "OUTPUT" text fields and so on will be put.
			int mainPanelX = WINDOW_EDGE_WIDTH;
			int mainPanelY = WINDOW_EDGE_WIDTH;
			int mainPanelWidth = this.settingContainer.windowWidth - 2 * WINDOW_EDGE_WIDTH;
			int mainPanelHeight = this.settingContainer.retractedWindowHeight - WINDOW_EDGE_WIDTH;
			mainPanel = new JPanel();
			mainPanel.setBounds(mainPanelX, mainPanelY, mainPanelWidth, mainPanelHeight);
			basePanel.add(mainPanel);

			// Create the key panel.
			int keyPanelX = WINDOW_EDGE_WIDTH;
			int keyPanelY = this.settingContainer.retractedWindowHeight;
			int keyPanelWidth = this.settingContainer.windowWidth - 2 * WINDOW_EDGE_WIDTH;
			int keyPanelHeight = this.settingContainer.windowHeight - this.settingContainer.retractedWindowHeight - WINDOW_EDGE_WIDTH;
			keyPanel = new JPanel();
			keyPanel.setBounds(keyPanelX, keyPanelY, keyPanelWidth, keyPanelHeight);
			basePanel.add(keyPanel);

			// Create and mount components on the main panel and the key panel.
			this.mountMainPanelComponents();
			this.mountKeyPanelComponents();

			// Make the window frame semi-transparent, if possible.
			GraphicsDevice graphicsDevide = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			if (graphicsDevide.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
				frame.setUndecorated(true);
				frame.setAlwaysOnTop(this.settingContainer.stayOnTopOfAllWindows);
				frame.setOpacity((float)this.settingContainer.windowOpacity);

				Color windowBackgroundColor = new Color(
					this.settingContainer.windowBackgroundColorR,
					this.settingContainer.windowBackgroundColorG,
					this.settingContainer.windowBackgroundColorB
				);
				mainPanel.setBackground(windowBackgroundColor);
				keyPanel.setBackground(windowBackgroundColor);

				runButton.setBackground(new Color(255, 255, 255));
				exitButton.setBackground(new Color(255, 255, 255));

				inputLabel.setForeground(new Color(0, 0, 0, 120));
				outputLabel.setForeground(new Color(0, 0, 0, 120));
				keyRetractorLabel.setForeground(new Color(0, 0, 0, 120));

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
			mainPanel.setVisible(true);
			basePanel.setVisible(true);
			frame.setVisible(true);
		}

		// Create and mount components on the main panel.
		private void mountMainPanelComponents() {
			GridBagLayout layout = new GridBagLayout();
			GridBagConstraints gridConstraints = new GridBagConstraints(
				0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0
			);
			gridConstraints.insets = new Insets(0, 0, 0, 0);
			mainPanel.setLayout(layout);

			// The horizontal bar on which the "▼INPUT" label and "X" button are.
			{
				gridConstraints.gridy = 0;
				gridConstraints.gridwidth = 1;
				gridConstraints.weighty = 1.0;
				gridConstraints.fill = GridBagConstraints.NONE;

				// Create the "INPUT" label.
				gridConstraints.gridx = 0;
				gridConstraints.anchor = GridBagConstraints.WEST;
				inputLabel = new JLabel("▼INPUT", JLabel.LEFT);
				layout.setConstraints(inputLabel, gridConstraints);
				inputLabel.setFont(SettingContainer.LABEL_FONT);
				mainPanel.add(inputLabel);

				// Create the "X" button.
				gridConstraints.gridx = 1;
				gridConstraints.anchor = GridBagConstraints.EAST;
				exitButton = new JButton("×");
				layout.setConstraints(exitButton, gridConstraints);
				exitButton.setFont(SettingContainer.EXIT_BUTTON_FONT);
				exitButton.setForeground(Color.RED);
				mainPanel.add(exitButton);
			}

			// Create the "INPUT" text field.
			gridConstraints.gridx = 0;
			gridConstraints.gridy = 1;
			gridConstraints.gridwidth = 2;
			gridConstraints.weighty = 1.0;
			gridConstraints.fill = GridBagConstraints.BOTH;
			gridConstraints.insets = new Insets(0, 0, 0, 0);
 			inputField = new JTextField();
			layout.setConstraints(inputField, gridConstraints);
			inputField.setFont(new Font(
				SettingContainer.TEXT_FIELD_FONT_NAME, SettingContainer.TEXT_FIELD_FONT_TYPE, this.settingContainer.textFieldFontSize
			));
			mainPanel.add(inputField);

			// The horizontal bar on which the "▼OUTPUT" label and the "=" buttons are.
			{
				gridConstraints.gridy = 2;
				gridConstraints.gridwidth = 1;
				gridConstraints.weighty = 1.0;
				gridConstraints.fill = GridBagConstraints.NONE;

				// Create the "OUTPUT" label.
				gridConstraints.gridx = 0;
				gridConstraints.anchor = GridBagConstraints.WEST;
				outputLabel = new JLabel("▼OUTPUT", JLabel.LEFT);
				layout.setConstraints(outputLabel, gridConstraints);
				outputLabel.setFont(SettingContainer.LABEL_FONT);
				mainPanel.add(outputLabel);

				// Create the "run" button.
				gridConstraints.gridx = 1;
				gridConstraints.anchor = GridBagConstraints.EAST;
				runButton = new JButton("=");
				layout.setConstraints(runButton, gridConstraints);
				runButton.setFont(SettingContainer.BUTTON_FONT);
				mainPanel.add(runButton);
			}

			// Create the "OUTPUT" text field.
			gridConstraints.gridx = 0;
			gridConstraints.gridy = 3;
			gridConstraints.gridwidth = 2;
			gridConstraints.weighty = 1.0;
			gridConstraints.fill = GridBagConstraints.BOTH;
			gridConstraints.insets = new Insets(0, 0, 0, 0);
			outputField = new JTextField();
			layout.setConstraints(outputField, gridConstraints);
			outputField.setFont(new Font(
				SettingContainer.TEXT_FIELD_FONT_NAME, SettingContainer.TEXT_FIELD_FONT_TYPE, this.settingContainer.textFieldFontSize
			));
			mainPanel.add(outputField);

			// The horizontal bar on which the "▼KEY-PANEL" (key-retractor) label is.
			gridConstraints.gridx = 0;
			gridConstraints.gridy = 4;
			gridConstraints.gridwidth = 1;
			gridConstraints.weighty = 1.0;
			gridConstraints.fill = GridBagConstraints.NONE;
			gridConstraints.anchor = GridBagConstraints.WEST;
			gridConstraints.insets = new Insets(5, 0, 0, 0);
			keyRetractorLabel = new JLabel("▼KEY-PANEL", JLabel.LEFT);
			layout.setConstraints(keyRetractorLabel, gridConstraints);
			keyRetractorLabel.setFont(SettingContainer.LABEL_FONT);
			mainPanel.add(keyRetractorLabel);


			// Create right-clicking menu for the "INPUT" / "OUTPUT" text fields.
			textFieldPopupMenu = new JPopupMenu();
			textFieldPopupMenu.add(new DefaultEditorKit.CutAction()).setText("Cut");
			textFieldPopupMenu.add(new DefaultEditorKit.CopyAction()).setText("Copy");
			textFieldPopupMenu.add(new DefaultEditorKit.PasteAction()).setText("Paste");
		}

		// Create and mount components on the key panel.
		private void mountKeyPanelComponents() {
			GridBagLayout layout = new GridBagLayout();
			GridBagConstraints gridConstraints = new GridBagConstraints(
				0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0
			);
			gridConstraints.insets = new Insets(0, 0, 0, 0);
			keyPanel.setLayout(layout);

			// Initialize the list for storing the references of all JButtons mounted on the key panel.
			keyPanelButtonList = new ArrayList<JButton>();

			// Create the panel on which mount function keys.
			gridConstraints.gridx = 0;
			gridConstraints.weightx = 5.0;
			JPanel functionPanel = new JPanel();
			functionPanel.setBackground(Color.RED); // temporary
			layout.setConstraints(functionPanel, gridConstraints);
			keyPanel.add(functionPanel);

			// Create the panel on which mount number keys.
			gridConstraints.gridx = 1;
			gridConstraints.weightx = 3.0;
			JPanel numberPanel = new JPanel();
			numberPanel.setBackground(Color.GREEN); // temporary
			layout.setConstraints(numberPanel, gridConstraints);
			keyPanel.add(numberPanel);

			// Create the panel on which mount action keys.
			gridConstraints.gridx = 2;
			gridConstraints.weightx = 1.0;
			JPanel actionPanel = new JPanel();
			actionPanel.setBackground(Color.BLUE); // temporary
			layout.setConstraints(actionPanel, gridConstraints);
			keyPanel.add(actionPanel);
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
			mainPanel = null;
			inputField = null;
			outputField = null;
			inputLabel = null;
			outputLabel = null;
			keyRetractorLabel = null;
			runButton = null;
			exitButton = null;
		}
	}
}
