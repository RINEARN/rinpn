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

	public List<JButton> numberKeyList = null;
	public List<JButton> functionKeyList = null;
	public List<JButton> behaviorKeyList = null;


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

				runButton.setBackground(Color.WHITE);
				exitButton.setBackground(Color.WHITE);
				/*
				for (JButton button: numberKeyList) {
					button.setBackground(Color.WHITE);
				}
				for (JButton button: functionKeyList) {
					button.setBackground(Color.WHITE);
				}
				for (JButton button: functionKeyList) {
					button.setBackground(Color.WHITE);
				}
				*/

				/*
				inputLabel.setForeground(new Color(0, 0, 0, 120));
				outputLabel.setForeground(new Color(0, 0, 0, 120));
				keyRetractorLabel.setForeground(new Color(0, 0, 0, 120));
				*/

				Color ioLabelColor = new Color(
					this.settingContainer.textLabelForgroundColorR,
					this.settingContainer.textLabelForgroundColorG,
					this.settingContainer.textLabelForgroundColorB
				);
				inputLabel.setForeground(ioLabelColor);
				outputLabel.setForeground(ioLabelColor);

				Color keyRetractorLabelColor = new Color(
					this.settingContainer.keyRetractorForegroundColorR,
					this.settingContainer.keyRetractorForegroundColorG,
					this.settingContainer.keyRetractorForegroundColorB
				);
				keyRetractorLabel.setForeground(keyRetractorLabelColor);

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
				gridConstraints.insets = new Insets(5, 0, 0, 0);
				inputLabel = new JLabel("INPUT", JLabel.LEFT);
				layout.setConstraints(inputLabel, gridConstraints);
				inputLabel.setFont(SettingContainer.LABEL_FONT);
				mainPanel.add(inputLabel);

				// Create the "X" button.
				gridConstraints.gridx = 1;
				gridConstraints.anchor = GridBagConstraints.EAST;
				gridConstraints.insets = new Insets(0, 0, 0, 0);
				exitButton = new JButton("X");
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
				gridConstraints.insets = new Insets(5, 0, 0, 0);
				outputLabel = new JLabel("OUTPUT", JLabel.LEFT);
				layout.setConstraints(outputLabel, gridConstraints);
				outputLabel.setFont(SettingContainer.LABEL_FONT);
				mainPanel.add(outputLabel);

				// Create the "run" button.
				gridConstraints.gridx = 1;
				gridConstraints.anchor = GridBagConstraints.EAST;
				gridConstraints.insets = new Insets(0, 0, 0, 0);
				runButton = new JButton("=");
				layout.setConstraints(runButton, gridConstraints);
				runButton.setFont(SettingContainer.RUN_BUTTON_FONT);
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

			// The horizontal bar on which the "KEY-PANEL" (key-retractor) label is.
			gridConstraints.gridx = 0;
			gridConstraints.gridy = 4;
			gridConstraints.gridwidth = 1;
			gridConstraints.weighty = 1.0;
			gridConstraints.fill = GridBagConstraints.NONE;
			gridConstraints.anchor = GridBagConstraints.WEST;
			gridConstraints.insets = new Insets(5, 0, 0, 0);
			keyRetractorLabel = new JLabel("▲KEY-PANEL", JLabel.LEFT);
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

			// Create the panel on which mount function keys.
			gridConstraints.gridx = 0;
			gridConstraints.weightx = 5.0;
			gridConstraints.insets = new Insets(0, 0, 0, 0);
			JPanel functionPanel = new JPanel();
			functionPanel.setLayout(new GridLayout(8, 3));
			functionPanel.setBackground(new Color(0, 0, 0, 0));
			layout.setConstraints(functionPanel, gridConstraints);
			keyPanel.add(functionPanel);

			// Create function keys.
			{
				// Initialize the list for storing the references of all JButtons mounted on the function key panel.
				functionKeyList = new ArrayList<JButton>();

				JButton sinButton = new JButton("sin");
				functionPanel.add(sinButton);
				functionKeyList.add(sinButton);

				JButton cosButton = new JButton("cos");
				functionPanel.add(cosButton);
				functionKeyList.add(cosButton);

				JButton tanButton = new JButton("tan");
				functionPanel.add(tanButton);
				functionKeyList.add(tanButton);

				JButton asinButton = new JButton("asin");
				functionPanel.add(asinButton);
				functionKeyList.add(asinButton);

				JButton acosButton = new JButton("cos");
				functionPanel.add(acosButton);
				functionKeyList.add(acosButton);

				JButton atanButton = new JButton("tan");
				functionPanel.add(atanButton);
				functionKeyList.add(atanButton);

				JButton absButton = new JButton("abs");
				functionPanel.add(absButton);
				functionKeyList.add(absButton);

				JButton sqrtButton = new JButton("sqrt");
				functionPanel.add(sqrtButton);
				functionKeyList.add(sqrtButton);

				JButton powButton = new JButton("pow");
				functionPanel.add(powButton);
				functionKeyList.add(powButton);

				JButton expButton = new JButton("exp");
				functionPanel.add(expButton);
				functionKeyList.add(expButton);

				JButton lnButton = new JButton("ln");
				functionPanel.add(lnButton);
				functionKeyList.add(lnButton);

				JButton log10Button = new JButton("log10");
				functionPanel.add(log10Button);
				functionKeyList.add(log10Button);

				JButton sumButton = new JButton("sum");
				functionPanel.add(sumButton);
				functionKeyList.add(sumButton);

				JButton vanButton = new JButton("van");
				functionPanel.add(vanButton);
				functionKeyList.add(vanButton);

				JButton van1Button = new JButton("van1");
				functionPanel.add(van1Button);
				functionKeyList.add(van1Button);

				JButton meanButton = new JButton("mean");
				functionPanel.add(meanButton);
				functionKeyList.add(meanButton);

				JButton sdnButton = new JButton("sdn");
				functionPanel.add(sdnButton);
				functionKeyList.add(sdnButton);

				JButton sdn1Button = new JButton("sdn1");
				functionPanel.add(sdn1Button);
				functionKeyList.add(sdn1Button);

				JButton radButton = new JButton("rad");
				functionPanel.add(radButton);
				functionKeyList.add(radButton);

				JButton degButton = new JButton("deg");
				functionPanel.add(degButton);
				functionKeyList.add(degButton);

				JButton piButton = new JButton("PI");
				functionPanel.add(piButton);
				functionKeyList.add(piButton);

				JButton openParenthesisButton = new JButton("(");
				functionPanel.add(openParenthesisButton);
				functionKeyList.add(openParenthesisButton);

				JButton closeParenthesisButton = new JButton(")");
				functionPanel.add(closeParenthesisButton);
				functionKeyList.add(closeParenthesisButton);

				JButton spaceButton = new JButton("_");
				functionPanel.add(spaceButton);
				functionKeyList.add(spaceButton);

				Font functionKeyFont = new Font("Dialog", Font.BOLD, this.settingContainer.functionKeyFontSize);
				for (JButton button: functionKeyList) {
					button.setFont(functionKeyFont);
				}
			}

			// Create the panel on which mount number keys.
			gridConstraints.gridx = 1;
			gridConstraints.weightx = 3.0;
			gridConstraints.insets = new Insets(0, 5, 0, 5);
			JPanel numberPanel = new JPanel();
			numberPanel.setLayout(new GridLayout(4, 4));
			numberPanel.setBackground(new Color(0, 0, 0, 0));
			layout.setConstraints(numberPanel, gridConstraints);
			keyPanel.add(numberPanel);

			// Create number keys.
			{
				// Initialize the list for storing the references of all JButtons mounted on the number key panel.
				numberKeyList = new ArrayList<JButton>();

				JButton num7Button = new JButton("7");
				numberPanel.add(num7Button);
				numberKeyList.add(num7Button);

				JButton num8Button = new JButton("8");
				numberPanel.add(num8Button);
				numberKeyList.add(num8Button);

				JButton num9Button = new JButton("9");
				numberPanel.add(num9Button);
				numberKeyList.add(num9Button);

				JButton divButton = new JButton("/");
				numberPanel.add(divButton);
				numberKeyList.add(divButton);

				JButton num4Button = new JButton("4");
				numberPanel.add(num4Button);
				numberKeyList.add(num4Button);

				JButton num5Button = new JButton("5");
				numberPanel.add(num5Button);
				numberKeyList.add(num5Button);

				JButton num6Button = new JButton("6");
				numberPanel.add(num6Button);
				numberKeyList.add(num6Button);

				JButton mulButton = new JButton("*");
				numberPanel.add(mulButton);
				numberKeyList.add(mulButton);

				JButton num1Button = new JButton("1");
				numberPanel.add(num1Button);
				numberKeyList.add(num1Button);

				JButton num2Button = new JButton("2");
				numberPanel.add(num2Button);
				numberKeyList.add(num2Button);

				JButton num3Button = new JButton("3");
				numberPanel.add(num3Button);
				numberKeyList.add(num3Button);

				JButton subButton = new JButton("-");
				numberPanel.add(subButton);
				numberKeyList.add(subButton);

				JButton num0Button = new JButton("0");
				numberPanel.add(num0Button);
				numberKeyList.add(num0Button);

				JButton dotButton = new JButton(".");
				numberPanel.add(dotButton);
				numberKeyList.add(dotButton);

				JButton commaButton = new JButton(",");
				numberPanel.add(commaButton);
				numberKeyList.add(commaButton);

				JButton addButton = new JButton("+");
				numberPanel.add(addButton);
				numberKeyList.add(addButton);

				Font numberKeyFont = new Font("Dialog", Font.BOLD, this.settingContainer.numberKeyFontSize);
				for (JButton button: numberKeyList) {
					button.setFont(numberKeyFont);
				}
			}

			// Create the panel on which mount behavior keys.
			gridConstraints.gridx = 2;
			gridConstraints.weightx = 1.0;
			gridConstraints.insets = new Insets(0, 0, 0, 0);
			JPanel behaviorPanel = new JPanel();
			behaviorPanel.setLayout(new GridLayout(4, 1));
			behaviorPanel.setBackground(new Color(0, 0, 0, 0));
			layout.setConstraints(behaviorPanel, gridConstraints);
			keyPanel.add(behaviorPanel);

			// Create behavior keys.
			{
				// Initialize the list for storing the references of all JButtons mounted on the number key panel.
				behaviorKeyList = new ArrayList<JButton>();

				JButton numRunButton = new JButton("=");
				numRunButton.setForeground(Color.BLUE);
				behaviorPanel.add(numRunButton);
				behaviorKeyList.add(numRunButton);

				JButton clearButton = new JButton("C");
				clearButton.setForeground(Color.RED);
				behaviorPanel.add(clearButton);
				behaviorKeyList.add(clearButton);

				JButton backspaceButton = new JButton("BS");
				clearButton.setForeground(Color.RED);
				behaviorPanel.add(backspaceButton);
				behaviorKeyList.add(backspaceButton);

				JButton fileButton = new JButton("Script");
				behaviorPanel.add(fileButton);
				behaviorKeyList.add(fileButton);

				Font behaviorKeyFont = new Font("Dialog", Font.BOLD, this.settingContainer.behaviorKeyFontSize);
				for (JButton button: behaviorKeyList) {
					button.setFont(behaviorKeyFont);
				}
			}
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


	/**
	 * Resizes the main/key panels on the window.
	 * 
	 * @param settingContainer The container storing setting values.
	 */
	public void resizePanels(SettingContainer settingContainer) throws InvocationTargetException, InterruptedException {
		if (SwingUtilities.isEventDispatchThread()) {
			new PanelResizer(settingContainer).run();
		} else {
			SwingUtilities.invokeAndWait(new PanelResizer(settingContainer));
		}
		this.initialized = true;
	}

	/**
	 * The Runnable implementation for resizing the main/key panels on the event-dispatcher thread.
	 */
	private class PanelResizer implements Runnable {
		SettingContainer settingContainer;

		/**
		 * Creates new resizer.
		 * 
		 * @param settingContainer The container storing setting values.
		 */
		public PanelResizer(SettingContainer settingContainer) {
			this.settingContainer = settingContainer;
		}

		@Override
		public void run() {
			int resizedWindowWidth = frame.getSize().width;
			int resizedWindowHeight = frame.getSize().height;

			// Resize the main panel.
			int mainPanelX = View.WINDOW_EDGE_WIDTH;
			int mainPanelY = View.WINDOW_EDGE_WIDTH;
			int mainPanelWidth = resizedWindowWidth - 2 * View.WINDOW_EDGE_WIDTH;
			int mainPanelHeight = this.settingContainer.retractedWindowHeight - View.WINDOW_EDGE_WIDTH;
			mainPanel.setBounds(mainPanelX, mainPanelY, mainPanelWidth, mainPanelHeight);

			// Resize the key panel.
			int keyPanelX = View.WINDOW_EDGE_WIDTH;
			int keyPanelY = this.settingContainer.retractedWindowHeight;
			int keyPanelWidth = resizedWindowWidth - 2 * View.WINDOW_EDGE_WIDTH;
			int keyPanelHeight = resizedWindowHeight - this.settingContainer.retractedWindowHeight - View.WINDOW_EDGE_WIDTH;
			if (keyPanelHeight < 20) {
				keyPanelHeight = 20;
			}
			keyPanel.setBounds(keyPanelX, keyPanelY, keyPanelWidth, keyPanelHeight);
		}
	}
}
