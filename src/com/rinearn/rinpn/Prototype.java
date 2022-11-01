/*
 * Copyright(C) 2018-2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.math.RoundingMode;
import java.math.BigDecimal;
import java.math.MathContext;

import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.text.DefaultEditorKit;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;

import java.lang.reflect.InvocationTargetException;


/**
 * このコードは、本番の実装を作る前に、挙動やUIを色々と検討するために実装された、即席の試作版のコードです。
 * そのため、単一のクラス内にほとんど全ての処理が雑にベタ書きされていますが、整理などは行いません。
 * 現在は既に本番実装に移行しているため、このクラスはもう使われていません。
 */
public final class Prototype {

	/*
	public static void main(String[] args) {
		new Prototype().launch();
	}
	*/

	private static final String TEXT_FIELD_FONT_NAME = "Monospaced";
	private static final int TEXT_FIELD_FONT_TYPE = Font.BOLD;
	private static final Font LABEL_FONT = new Font("Dialog", Font.BOLD, 14);
	private static final Font BUTTON_FONT = new Font("Dialog", Font.BOLD, 14);
	private static final Font EXIT_BUTTON_FONT = new Font("Dialog", Font.BOLD, 14);

	private static final String SETTING_SCRIPT_PATH = "./Setting.vnano";
	private static final String SETTING_SCRIPT_ENCODING = "UTF-8";

	private static final int MAX_ROUNDING_LENGTH = 100;

	// 設定値を格納するクラス
	// （設定スクリプト（Setting.vnano）の解釈時にスクリプトエンジンにバインディングする）
	public class SettingContainer implements Cloneable {

		public int textFieldFontSize = 18;
		public boolean stayOnTopOfAllWindows = true;
		public int windowWidth = 400;
		public int windowHeight = 160;
		public double windowOpacity = 0.76;

		public int windowBackgroundColorR = 180;
		public int windowBackgroundColorG = 220;
		public int windowBackgroundColorB = 255;

		public int textFieldBackgroundColorR = 100;
		public int textFieldBackgroundColorG = 100;
		public int textFieldBackgroundColorB = 100;

		public int textFieldForegroundColorR = 140;
		public int textFieldForegroundColorG = 255;
		public int textFieldForegroundColorB = 180;

		public boolean inputNormalizerEnabled = true;
		public boolean outputRounderEnabled = true;
		public String roundingMode = "HALF_UP";
		public String roundingTarget = "SIGNIFICAND";
		public int roundingLength = 10;

		public boolean acceleratorEnabled = true;
		public boolean evalNumberAsFloat = true;
		public String libraryScriptPath = "./Library.vnano";
		public String libraryScriptEncoding = "UTF-8";

		public String localeCode = new Prototype.LocaleCode().getDefaultLocaleCode();

		public Prototype.SettingContainer clone() {
			try {
				return (Prototype.SettingContainer)super.clone();
			} catch (CloneNotSupportedException e) {
				if (localeCode.equals(LocaleCode.EN_US)) {
					showMessage("SettingContainer Clone Failed.", "FatalError");
				}
				if (localeCode.equals(LocaleCode.JA_JP)) {
					showMessage("SettingContainer の複製に失敗しました", "実装エラー");
				}
				e.printStackTrace();
				return null;
			}
		}
	}

	private class LocaleCode {
		public static final String JA_JP = "ja-jp";
		public static final String EN_US = "en-us";
		public static final String AUTO = "auto";

		public final boolean isSupported(String localeCode) {
			if (localeCode.equals(JA_JP)) {
				return true;
			}
			if (localeCode.equals(EN_US)) {
				return true;
			}
			if (localeCode.equals(AUTO)) {
				return true;
			}
			return false;
		}

		public final Locale toLocale(String localeCode) {
			Locale locale = null;
			if (localeCode.equals(JA_JP)) {
				return new Locale("ja", "JP");
			}
			if (localeCode.equals(EN_US)) {
				return new Locale("en", "US");
			}
			if (localeCode.equals(AUTO)) {
				return Locale.getDefault();
			}
			return locale;
		}

		private final String getDefaultLocaleCode() {
			Locale locale = Locale.getDefault();

			if (   ( locale.getLanguage()!=null && locale.getLanguage().toLowerCase().equals("ja") )
				   || ( locale.getCountry()!=null && locale.getCountry().toLowerCase().equals("jp") )   ) {

				return LocaleCode.JA_JP;

			} else {

				return LocaleCode.EN_US;
			}
		}
	}

	@SuppressWarnings("serial")
	private class PrototypeException extends Exception {
		public PrototypeException(Throwable parentThrowable) {
			super(parentThrowable);
		}
		public PrototypeException(String errorMessage) {
			super(errorMessage);
		}
	}

	private enum RoundingTarget {
		SIGNIFICAND,
		AFTER_FIXED_POINT
	}

	private class UI {
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

	private class State {
		public ScriptEngine engine = null; // 計算式やライブラリの処理を実行するためのVnanoのスクリプトエンジン
		public volatile boolean running = false;

		public volatile String inputText = "";
		public volatile String outputText = "";
	}


	public Prototype() {
		// 起動は、インスタンス生成後に明示的に launch() を呼ぶ
	}


	public final void launch() {

		Prototype.UI ui = new Prototype.UI();
		Prototype.State state = new Prototype.State();

		// 設定スクリプト解釈用のスクリプトエンジンを生成し、設定スクリプトを解釈
		Prototype.SettingContainer setting = null;
		try {
			setting = this.evaluateSettingScript(SETTING_SCRIPT_PATH, SETTING_SCRIPT_ENCODING);
			setting = this.checkAndNormalizeSettingValues(setting);

		} catch (PrototypeException e) {
			e.printStackTrace();
			this.exit(state, ui);
		}

		// 画面のUIを構築して初期化
		try {
			WindowMouseListener windowMouseListener = new WindowMouseListener(ui);
			RunButtonListener runButtonLister = new Prototype.RunButtonListener(state, ui, setting);
			ExitButtonListener exitButtonListener = new Prototype.ExitButtonListener(state, ui);
			KeyListener runKeyListener = new RunKeyListener(state, ui, setting);
			IOFieldMouseListener ioFieldMouseListener = new IOFieldMouseListener(ui);

			UIInitializer uiInitialiser = new Prototype.UIInitializer(
				ui, setting,
				windowMouseListener, windowMouseListener, runButtonLister, exitButtonListener,
				runKeyListener, ioFieldMouseListener
			);

			// UIスレッドでUIを構築
			SwingUtilities.invokeAndWait(uiInitialiser);

		} catch (InvocationTargetException | InterruptedException e) {

			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				this.showMessage("Unexpected exception occurred: " + e.getClass().getCanonicalName(), "Error");
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				this.showMessage("予期しない例外が発生しました: " + e.getClass().getCanonicalName(), "エラー");
			}
			e.printStackTrace();
			this.exit(state, ui);
		}

		// 計算実行用のスクリプトエンジンの準備など
		try {

			// ライブラリコードを読み込む
			String libraryCode = this.loadCode(
				setting.libraryScriptPath, setting.libraryScriptEncoding, setting.localeCode
			);

			// 実行用スクリプトエンジンに渡すオプション値マップを用意
			Map<String, Object> optionMap = new HashMap<String, Object>();
			optionMap.put("ACCELERATOR", setting.acceleratorEnabled);
			optionMap.put("EVAL_NUMBER_AS_FLOAT", setting.evalNumberAsFloat);
			optionMap.put("LIBRARY_SCRIPT_NAME", setting.libraryScriptPath);
			optionMap.put("LIBRARY_SCRIPT_CODE", libraryCode);
			optionMap.put("LOCALE", new LocaleCode().toLocale(setting.localeCode));

			// 実行用のスクリプトエンジンを生成して初期化
			state.engine = this.initializeVnanoEngine(optionMap, setting);

		} catch (Prototype.PrototypeException e) {
			e.printStackTrace();
			this.exit(state, ui);
		}
	}


	private final ScriptEngine initializeVnanoEngine(Map<String, Object> optionMap, Prototype.SettingContainer setting)
			throws PrototypeException {

		// 式やライブラリの解釈/実行用に、Vnanoのスクリプトエンジンを読み込んで生成
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("vnano");
		if (engine == null) {

			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				this.showMessage("Please put Vnano.jar in the same directory as RinearnProcessorNano.jar.", "Engine Loading Error");
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				this.showMessage("Vnano.jar を RinearnProcessorNano.jar と同じフォルダ内に配置してください。", "エンジン読み込みエラー");
			}
			throw new Prototype.PrototypeException("ScriptEngine of the Vnano could not be loaded.");
		}

		// スクリプトエンジンにオプションマップを設定
		engine.put("VNANO_OPTION", optionMap);

		return engine;
	}


	private final synchronized void requestScripting(final Prototype.State state, final Prototype.UI ui, Prototype.SettingContainer setting) {

		// 設定に応じて、まず入力フィールドの内容を正規化
		if (setting.inputNormalizerEnabled) {
			ui.inputField.setText(
				Normalizer.normalize(ui.inputField.getText(), Normalizer.Form.NFKC)
			);
		}

		// 入力フィールドの内容を取得してスクリプト実行をリクエストする
		state.inputText = ui.inputField.getText();

		// 入力された式を式文にするために末尾にセミコロンを追加（無い場合のみ）し、
		// 実行するスクリプトコードの内容としてセット
		String scriptCode = state.inputText;
		if (!scriptCode.trim().endsWith(";")) {
			scriptCode += ";";
		}

		// スクリプトの実行終了を受け取ってUIに反映させるリスナーを用意
		ScriptingListener scriptingListener = new ScriptingListener() {
			public void scriptingFinished() {
				SwingUtilities.invokeLater(new UIUpwardSynchronizer(state, ui));
			}
		};

		// スクリプト実行スレッドを生成して実行
		Prototype.AsynchronousScriptRunner asyncScriptRunner
				= new Prototype.AsynchronousScriptRunner(scriptCode, scriptingListener, state, setting);
		Thread scriptingThread = new Thread(asyncScriptRunner);
		scriptingThread.start();
	}


	private final synchronized void exit(Prototype.State state, Prototype.UI ui) {

		// スクリプトが実行中の場合は、強制終了するかどうか尋ねて、YESなら System.exit で強制終了する
		if (state.running) {
			String message = "The script is running. Do you want to force-quit this software ?";
			int quitOrNot = JOptionPane.showConfirmDialog(
				ui.frame, message, "!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
			);
			if (quitOrNot == JOptionPane.YES_OPTION) {
				System.exit(0);
			}

		// スクリプトが実行中でない場合は、普通にリソースを破棄して自然に終了させる
		} else {

			if (ui.initialized) {
				ui.frame.dispose();
				ui.frame = null;
				ui.basePanel = null;
				ui.midPanel = null;
				ui.inputField = null;
				ui.outputField = null;
				ui.inputLabel = null;
				ui.outputLabel = null;
				ui.runButton = null;
				ui.exitButton = null;
			}

			state.inputText = "";
			state.outputText = "";
			state.engine = null;
		}
	}


	private final String loadCode(String filePath, String encoding, String localeCode)
			throws PrototypeException {

		// 指定されたファイルが存在するか検査
		if (!new File(filePath).exists()) {
			if (localeCode.equals(LocaleCode.EN_US)) {
				this.showMessage("The file \"" + filePath + "\" does not exist.", "Code Loading Error");
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				this.showMessage("ファイル \"" + filePath + "\" が見つかりません。", "コード読み込みエラー");
			}
			throw new Prototype.PrototypeException("The file \"" + filePath + "\" does not exist.");
		}

		// 読み込み処理
		try {

			// 全行を読み込み、行単位で格納したListを取得
			List<String> lines = Files.readAllLines(Paths.get(filePath), Charset.forName(encoding));

			// 改行コードを挟みつつ全行を結合
			StringBuilder codeBuilder = new StringBuilder();
			String eol = System.getProperty("line.separator");
			for (String line: lines) {
				codeBuilder.append(line);
				codeBuilder.append(eol);
			}
			String code = codeBuilder.toString();

			// UTF-8ではBOMの有無を検査し、付いている場合は削除
			if(encoding.toLowerCase().equals("utf-8")) {
				// UTF-8のBOMは 0xEF 0xBB 0xBF だが、文字列内部表現がUTF-16な都合で、読み込み後は 0xFEFF が付いている
				final char bom = (char)0xFEFF;
				if(0 < code.length() && code.charAt(0) == bom){
					code = code.substring(1);
				}
			}

			return code;

		// 非対応の文字コードが指定された場合
		} catch (UnsupportedCharsetException uce) {

			if (localeCode.equals(LocaleCode.EN_US)) {
				this.showMessage("The encoding \"" + encoding + "\" is not supported.", "Code Loading Error");
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				this.showMessage("非対応の文字コード \"" + encoding + "\" が指定されています。", "コード読み込みエラー");
			}
			throw new Prototype.PrototypeException(uce);

		// 何らかの理由で読み込みに失敗した場合
		} catch (IOException ioe) {

			if (localeCode.equals(LocaleCode.EN_US)) {
				this.showMessage(
					"An error (IOException) occurred for the loading of \"" + filePath + "\".",
					"Code Loading Error"
				);
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				this.showMessage(
					"\"" + filePath + "\" の読み込みにおいて、エラー (IOException) が発生しました。",
					"コード読み込みエラー"
				);
			}
			throw new Prototype.PrototypeException(ioe);
		}
	}


	private final BigDecimal round(double value, RoundingMode mode, RoundingTarget target, int digits) {

		BigDecimal bdValue = new BigDecimal(value);

		switch (target) {
			case AFTER_FIXED_POINT : {
				bdValue = bdValue.setScale(digits, mode);
				break;
			}
			case SIGNIFICAND : {
				MathContext mathContext = new java.math.MathContext(digits, mode);
				bdValue = bdValue.round(mathContext);
				break;
			}
		}

		return bdValue;
	}


	private final String customizeExceptionMessage(String message) {

		// eval でスクリプトを渡した際のエラー箇所情報。
		// このソフトでは1行の内容しか渡せないため、行番号が表示されてもうれしくないので省略
		// (毎回同じ内容になる)
		String defaultErrorSourceDescription = ": in EVAL_SCRIPT at line number 1";

		String result = message;
		if (result.endsWith(defaultErrorSourceDescription)) {
			result = result.substring(0, result.length() - defaultErrorSourceDescription.length());
		}
		return result;
	}


	// メッセージを独立ウィンドウで表示
	private final void showMessage(String message, String title) {
		JDialog messageWindow = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE).createDialog(null, title);
		messageWindow.setAlwaysOnTop(true);
		messageWindow.setVisible(true);
		messageWindow.dispose();
	}


	private final Prototype.SettingContainer evaluateSettingScript(String settingScriptPath, String settingScriptEncoding)
			throws PrototypeException {

		String localeCode = new Prototype.LocaleCode().getDefaultLocaleCode();

		// 設定スクリプト解釈用に、Vnanoのスクリプトエンジンを読み込んで生成
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine settingVnanoEngine = manager.getEngineByName("vnano");

		if (settingVnanoEngine == null) {
			if (localeCode.equals(LocaleCode.EN_US)) {
				this.showMessage(
					"Please put Vnano.jar in the same directory as RinearnProcessorNano.jar.",
					"Engine Loading Error"
				);
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				this.showMessage(
					"Vnano.jar を、RinearnProcessorNano.jar と同じフォルダ内に配置してください。",
					"エンジン読み込みエラー"
				);
			}
			throw new Prototype.PrototypeException("ScriptEngine of the Vnano could not be loaded.");
		}

		// 設定スクリプトの値を格納するクラスのインスタンスを生成し、フィールドをスクリプトエンジンにバインディング
		Prototype.SettingContainer setting = new Prototype.SettingContainer();
		try {
			settingVnanoEngine.put(
				"stayOnTopOfAllWindows",
				new Object[] { setting.getClass().getField("stayOnTopOfAllWindows"), setting }
			);
			settingVnanoEngine.put(
				"textFieldFontSize",
				new Object[] { setting.getClass().getField("textFieldFontSize"), setting }
			);
			settingVnanoEngine.put(
				"windowWidth",
				new Object[] { setting.getClass().getField("windowWidth"), setting }
			);
			settingVnanoEngine.put(
				"windowHeight",
				new Object[] { setting.getClass().getField("windowHeight"), setting }
			);
			settingVnanoEngine.put(
				"windowOpacity",
				new Object[] { setting.getClass().getField("windowOpacity"), setting }
			);

			settingVnanoEngine.put(
				"windowBackgroundColorR",
				new Object[] { setting.getClass().getField("windowBackgroundColorR"), setting }
			);
			settingVnanoEngine.put(
				"windowBackgroundColorG",
				new Object[] { setting.getClass().getField("windowBackgroundColorG"), setting }
			);
			settingVnanoEngine.put(
				"windowBackgroundColorB",
				new Object[] { setting.getClass().getField("windowBackgroundColorB"), setting }
			);

			settingVnanoEngine.put(
				"textFieldBackgroundColorR",
				new Object[] { setting.getClass().getField("textFieldBackgroundColorR"), setting }
			);
			settingVnanoEngine.put(
				"textFieldBackgroundColorG",
				new Object[] { setting.getClass().getField("textFieldBackgroundColorG"), setting }
			);
			settingVnanoEngine.put(
				"textFieldBackgroundColorB",
				new Object[] { setting.getClass().getField("textFieldBackgroundColorB"), setting }
			);

			settingVnanoEngine.put(
				"textFieldForegroundColorR",
				new Object[] { setting.getClass().getField("textFieldForegroundColorR"), setting }
			);
			settingVnanoEngine.put(
				"textFieldForegroundColorG",
				new Object[] { setting.getClass().getField("textFieldForegroundColorG"), setting }
			);
			settingVnanoEngine.put(
				"textFieldForegroundColorB",
				new Object[] { setting.getClass().getField("textFieldForegroundColorB"), setting }
			);

			settingVnanoEngine.put(
				"inputNormalizerEnabled",
				new Object[] { setting.getClass().getField("inputNormalizerEnabled"), setting }
			);
			settingVnanoEngine.put(
				"outputRounderEnabled",
				new Object[] { setting.getClass().getField("outputRounderEnabled"), setting }
			);
			settingVnanoEngine.put(
				"roundingMode",
				new Object[] { setting.getClass().getField("roundingMode"), setting }
			);
			settingVnanoEngine.put(
				"roundingTarget",
				new Object[] { setting.getClass().getField("roundingTarget"), setting }
			);
			settingVnanoEngine.put(
				"roundingLength",
				new Object[] { setting.getClass().getField("roundingLength"), setting }
			);
			settingVnanoEngine.put(
				"acceleratorEnabled",
				new Object[] { setting.getClass().getField("acceleratorEnabled"), setting }
			);
			settingVnanoEngine.put(
				"evalNumberAsFloat",
				new Object[] { setting.getClass().getField("evalNumberAsFloat"), setting }
			);
			settingVnanoEngine.put(
				"libraryScriptPath",
				new Object[] { setting.getClass().getField("libraryScriptPath"), setting }
			);
			settingVnanoEngine.put(
				"libraryScriptEncoding",
				new Object[] { setting.getClass().getField("libraryScriptEncoding"), setting }
			);
			settingVnanoEngine.put(
				"localeCode",
				new Object[] { setting.getClass().getField("localeCode"), setting }
			);
		} catch (NoSuchFieldException | SecurityException e) {
			this.showMessage("Binding error occurred for SettingContainer.", "Fatal Error");
			throw new Prototype.PrototypeException(e);
		}

		// 設定スクリプト解釈用のスクリプトエンジンに渡すオプションを用意（スクリプト名を設定するだけ）
		Map<String, Object> optionMap = new HashMap<String, Object>();
		optionMap.put("EVAL_SCRIPT_NAME", settingScriptPath);
		settingVnanoEngine.put("VNANO_OPTION", optionMap);

		// 設定スクリプトを読み込み、実行して設定ファイルの記述内容を解釈する
		String settingScriptCode = this.loadCode(
			settingScriptPath, settingScriptEncoding, setting.localeCode
		);

		try {
			settingVnanoEngine.eval(settingScriptCode);
		} catch (ScriptException e) {
			String errorMessage = this.customizeExceptionMessage(e.getMessage());
			if (localeCode.equals(LocaleCode.EN_US)) {
				this.showMessage(errorMessage, "Input/Library Error");
			}
			if (localeCode.equals(LocaleCode.JA_JP)) {
				this.showMessage(errorMessage, "計算式やライブラリのエラー");
			}
			throw new Prototype.PrototypeException(e);
		}

		return setting;
	}


	private Prototype.SettingContainer checkAndNormalizeSettingValues(Prototype.SettingContainer setting)
			throws Prototype.PrototypeException {

		boolean errorOccurred = false;

		String errorMessage = null;
		String errorTitle = null;
		if (setting.localeCode.equals(LocaleCode.EN_US)) {
			errorTitle = "Setting Error";
		}
		if (setting.localeCode.equals(LocaleCode.JA_JP)) {
			errorTitle = "設定エラー";
		}


		Prototype.SettingContainer ret = setting.clone();

		if (ret.textFieldFontSize <= 0) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"textFieldFontSize\" should be grater than 0.";
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"textFieldFontSize\" の値は 0 よりも大きい必要があります。";
			}
			errorOccurred = true;
		}
		if (ret.windowWidth <= 0) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"windowWidth\" should be grater than 0.";
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"windowWidth\" の値は 0 よりも大きい必要があります。";
			}
			errorOccurred = true;
		}
		if (ret.windowHeight <= 0) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"windowHeight\" should be grater than 0.";
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"windowHeight\" の値は 0 よりも大きい必要があります。";
			}
			errorOccurred = true;
		}
		if (ret.windowOpacity <= 0) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"windowOpacity\" should be grater than 0.";
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"windowOpacity\" の値は 0 よりも大きい必要があります。";
			}
			errorOccurred = true;
		}

		if (ret.windowBackgroundColorR < 0 || 255 < ret.windowBackgroundColorR) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"windowBackgroundColorR\" should be in the range of 0 ~ 255.";
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"windowBackgroundColorR\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}
		if (ret.windowBackgroundColorG < 0 || 255 < ret.windowBackgroundColorG) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"windowBackgroundColorG\" should be in the range of 0 ~ 255.";
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"windowBackgroundColorG\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}
		if (ret.windowBackgroundColorB < 0 || 255 < ret.windowBackgroundColorB) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"windowBackgroundColorB\" should be in the range of 0 ~ 255.";
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"windowBackgroundColorB\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}

		if (ret.textFieldBackgroundColorR < 0 || 255 < ret.textFieldBackgroundColorR) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"textFieldBackgroundColorR\" should be in the range of 0 ~ 255.";
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"textFieldBackgroundColorR\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}
		if (ret.textFieldBackgroundColorG < 0 || 255 < ret.textFieldBackgroundColorG) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"textFieldBackgroundColorG\" should be in the range of 0 ~ 255.";
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"textFieldBackgroundColorG\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}
		if (ret.textFieldBackgroundColorB < 0 || 255 < ret.textFieldBackgroundColorB) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"textFieldBackgroundColorB\" should be in the range of 0 ~ 255.";
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"textFieldBackgroundColorB\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}

		if (ret.textFieldForegroundColorR < 0 || 255 < ret.textFieldForegroundColorR) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"textFieldForegroundColorR\" should be in the range of 0 ~ 255.";
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"textFieldForegroundColorR\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}
		if (ret.textFieldForegroundColorG < 0 || 255 < ret.textFieldForegroundColorG) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"textFieldForegroundColorG\" should be in the range of 0 ~ 255.";
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"textFieldForegroundColorG\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}
		if (ret.textFieldForegroundColorB < 0 || 255 < ret.textFieldForegroundColorB) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"textFieldForegroundColorB\" should be in the range of 0 ~ 255.";
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"textFieldForegroundColorB\" の値は 0 ~ 255 の範囲内で指定してください。";
			}
			errorOccurred = true;
		}


		try {
			RoundingMode.valueOf(ret.roundingMode);
		} catch (IllegalArgumentException e) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "Invalid value of \"roundingMode\": " + ret.roundingMode;
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"roundingMode\" に、正しい選択肢には無い値が指定されています: " + ret.roundingMode;
			}
			errorOccurred = true;
		}

		try {
			Prototype.RoundingTarget.valueOf(ret.roundingTarget);
		} catch (IllegalArgumentException e) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "Invalid value of \"roundingTarget\": " + ret.roundingMode;
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"roundingTarget\" に、正しい選択肢には無い値が指定されています: " + ret.roundingMode;
			}
			errorOccurred = true;
		}

		if (ret.roundingLength < 1 || MAX_ROUNDING_LENGTH < ret.roundingLength) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The value of \"roundingLength\" should be in the range of 1 ~ "
				             + MAX_ROUNDING_LENGTH + ".";
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "\"roundingLength\" の値は 1 ~ "
				             + MAX_ROUNDING_LENGTH + " の範囲内で指定してください。";
			}
			errorOccurred = true;
		}

		if ( !(new File(ret.libraryScriptPath).exists()) ) {
			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The library script file \"" + ret.libraryScriptPath + "\" does not exist.";
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "ライブラリスクリプトのファイル \"" + ret.libraryScriptPath + "\" が見つかりません。";
			}
			errorOccurred = true;
		}

		if ( !ret.libraryScriptEncoding.equals("UTF-8")
		     && !ret.libraryScriptEncoding.equals("Shift_JIS") ) {

			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The encoding \"" + ret.libraryScriptPath + "\" is not supported.";
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "非対応の文字コード \"" + ret.libraryScriptPath + "\" が指定されています。";
			}
			errorOccurred = true;
		}

		ret.localeCode = ret.localeCode.toLowerCase();
		if (!new LocaleCode().isSupported(ret.localeCode)) {

			if (setting.localeCode.equals(LocaleCode.EN_US)) {
				errorMessage = "The locale-code \"" + ret.localeCode + "\" is not supported.";
			}
			if (setting.localeCode.equals(LocaleCode.JA_JP)) {
				errorMessage = "非対応のロケールコード \"" + ret.localeCode + "\" が指定されています。";
			}
			errorOccurred = true;
		}
		if (ret.localeCode.equals(LocaleCode.AUTO)) {
			ret.localeCode = new LocaleCode().getDefaultLocaleCode();
		}

		if (errorOccurred) {
			this.showMessage(errorMessage, errorTitle);
			throw new Prototype.PrototypeException(errorMessage);
		} else {
			return ret;
		}
	}


	private interface ScriptingListener {

		// requestScripting メソッド内で匿名クラスに実装

		public void scriptingFinished();
	}


	private class RunKeyListener implements KeyListener {

		private Prototype.State state;
		private Prototype.UI ui;
		private Prototype.SettingContainer setting;

		public RunKeyListener(Prototype.State state, Prototype.UI ui, Prototype.SettingContainer setting) {
			this.state = state;
			this.ui = ui;
			this.setting = setting;
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				requestScripting(this.state, this.ui, this.setting);
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
	}


	private class RunButtonListener implements ActionListener {

		private Prototype.State state = null;
		private Prototype.UI ui = null;
		private SettingContainer setting = null;

		public RunButtonListener(Prototype.State state, Prototype.UI ui, Prototype.SettingContainer setting) {
			this.state = state;
			this.ui = ui;
			this.setting = setting;
		}

		public void actionPerformed(ActionEvent e) {
			requestScripting(this.state, this.ui, this.setting);
		}
	}


	private class ExitButtonListener implements ActionListener {

		private Prototype.State state = null;
		private Prototype.UI ui = null;

		public ExitButtonListener(Prototype.State state, Prototype.UI ui) {
			this.state = state;
			this.ui = ui;
		}

		public void actionPerformed(ActionEvent e) {
			exit(this.state, this.ui);
		}
	}


	private class IOFieldMouseListener implements MouseListener {

		private Prototype.UI ui = null;

		public IOFieldMouseListener(Prototype.UI ui) {
			this.ui = ui;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!(e.getSource() instanceof JTextField)) {
				showMessage(
					"IOFieldMouseListener is added to the invalid component: "
					+ e.getSource().getClass().getCanonicalName()
					+ " (should be JTextField)",
					"Fatal Error"
				);
				return;
			}
			if(javax.swing.SwingUtilities.isRightMouseButton(e)){
				JTextField ioField = (JTextField)e.getSource();
				this.ui.textFieldPopupMenu.show(ioField, e.getX(), e.getY() );
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}


	private class WindowMouseListener implements MouseListener, MouseMotionListener {

		private Prototype.UI ui = null;
		private int mousePressedX = -1;
		private int mousePressedY = -1;

		public WindowMouseListener(Prototype.UI ui) {
			this.ui = ui;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			mousePressedX = e.getX();
			mousePressedY = e.getY();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			int mouseCurrentX = e.getX();
			int mouseCurrentY = e.getY();

			int dx = mouseCurrentX - mousePressedX;
			int dy = mouseCurrentY - mousePressedY;

			int x = this.ui.frame.getX();
			int y = this.ui.frame.getY();

			this.ui.frame.setLocation(x + dx, y + dy);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}


	private class AsynchronousScriptRunner implements Runnable {

		private String scriptCode = null;
		private ScriptingListener scriptingListener = null;
		private Prototype.State state = null;
		private Prototype.SettingContainer setting = null;

		public AsynchronousScriptRunner(
				String scriptCode, ScriptingListener scriptingListener,
				Prototype.State state, Prototype.SettingContainer setting) {

			this.scriptCode = scriptCode;
			this.scriptingListener = scriptingListener;
			this.state = state;
			this.setting = setting;
		}

		@Override
		public void run() {

			// 実行処理を synchronized にすると、スクリプト内容が重い場合に実行ボタンが連打された際、
			// 実行待ちスレッドがどんどん積もっていって、全部消化されるまで待たなければいけなくなるので、
			// 実行中に実行リクエストがあった場合はその場で弾くようにする。

			if (this.state.running) {
				if (setting.localeCode.equals(LocaleCode.EN_US)) {
					showMessage("The previous calculation have not finished yet!", "!");
				}
				if (setting.localeCode.equals(LocaleCode.JA_JP)) {
					showMessage("まだ前の計算を実行中です !", "!");
				}
				return;
			}
			this.state.running = true;

			// 入力フィールドの式を評価して値を所得
			Object value = null;
			try {
				value = this.state.engine.eval(this.scriptCode);

			} catch (ScriptException e) {
				String errorMessage = customizeExceptionMessage(e.getMessage());
				if (setting.localeCode.equals(LocaleCode.EN_US)) {
					showMessage(errorMessage, "Input/Library Error");
				}
				if (setting.localeCode.equals(LocaleCode.JA_JP)) {
					showMessage(errorMessage, "計算式やライブラリのエラー");
				}
				e.printStackTrace();
				this.state.running = false;
				return;
			}

			// 値が浮動小数点数なら、設定内容に応じて丸める
			if (value instanceof Double && this.setting.outputRounderEnabled) {

				// ※ setting 内の設定値の正当性は checkSettingValues で検査済み
				RoundingMode mode = RoundingMode.valueOf(this.setting.roundingMode);
				RoundingTarget target = RoundingTarget.valueOf(this.setting.roundingTarget);
				int digits = this.setting.roundingLength;

				double doubleValue = ((Double)value).doubleValue();
				value = round(doubleValue, mode, target, digits); // 型は BigDecimal になる
			}

			// 値を出力フィールドの表示用文字列にセットし、UIスレッドでフィールドに反映
			if (value == null) {
				this.state.outputText = "";
			} else {
				this.state.outputText = value.toString();
			}
			this.scriptingListener.scriptingFinished();

			this.state.running = false;
		}
	}


	private class UIInitializer implements Runnable {

		private Prototype.UI ui = null;
		private Prototype.SettingContainer setting = null;

		MouseListener windowMouseListener = null;
		MouseMotionListener windowMouseMotionListener = null;

		ActionListener runButtonListener = null;
		ActionListener exitButtonListener = null;
		KeyListener runKeyListener = null;
		Prototype.IOFieldMouseListener ioFieldMouseListener = null;

		public UIInitializer(
				Prototype.UI ui,
				Prototype.SettingContainer setting,
				MouseListener windowMouseListener,
				MouseMotionListener windowMouseMotionListener,
				ActionListener runButtonListener,
				ActionListener exitButtonListener,
				KeyListener runKeyListener,
				Prototype.IOFieldMouseListener ioFieldMouseListener) {

			this.ui = ui;
			this.setting = setting;
			this.windowMouseListener = windowMouseListener;
			this.windowMouseMotionListener = windowMouseMotionListener;
			this.runButtonListener = runButtonListener;
			this.exitButtonListener = exitButtonListener;
			this.runKeyListener = runKeyListener;
			this.ioFieldMouseListener = ioFieldMouseListener;
		}

		@Override
		public void run() {

			// ウィンドウを生成
			this.ui.frame = new JFrame();
			this.ui.frame.addMouseListener(windowMouseListener);
			this.ui.frame.addMouseMotionListener(windowMouseMotionListener);
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
			this.ui.inputLabel.setFont(LABEL_FONT);
			this.ui.topPanel.add(this.ui.inputLabel);

			// 入出力フィールドのフォントを生成
			Font textFieldFont = new Font(
				TEXT_FIELD_FONT_NAME, TEXT_FIELD_FONT_TYPE, setting.textFieldFontSize
			);

			// 入力フィールドを生成して配置
			this.ui.inputField = new JTextField();
			this.ui.inputField.setFont(textFieldFont);
			this.ui.inputField.addKeyListener(runKeyListener);
			this.ui.inputField.addMouseListener(ioFieldMouseListener);
			this.ui.basePanel.add(this.ui.inputField);

			// 中段の水平パネル（「OUTPUT」のラベルがあるパネル）を生成して配置
			this.ui.midPanel = new JPanel();
			this.ui.midPanel.setLayout(new GridLayout(1, 2));
			//midPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			this.ui.basePanel.add(this.ui.midPanel);

			// 出力フィールドの上に「OUTPUT」と表示するラベルを生成して配置
			this.ui.outputLabel = new JLabel("  ▼OUTPUT   ", JLabel.LEFT);
			this.ui.outputLabel.setFont(LABEL_FONT);
			this.ui.midPanel.add(this.ui.outputLabel);

			// 出力フィールドを生成して配置
			this.ui.outputField = new JTextField();
			this.ui.outputField.setFont(textFieldFont);
			this.ui.outputField.addMouseListener(ioFieldMouseListener);
			this.ui.basePanel.add(this.ui.outputField);


			// 以下、水平パネルの上にボタンを並べる

			JPanel midButtonPanel = new JPanel();
			midButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			this.ui.midPanel.add(midButtonPanel);

			// 「RUN」ボタンを生成して配置
			this.ui.runButton = new JButton("=");
			this.ui.runButton.setFont(BUTTON_FONT);
			this.ui.runButton.addActionListener(this.runButtonListener);
			midButtonPanel.add(this.ui.runButton);

			// Exitボタンを右端に配置するためにもう一枚パネルを挟む
			JPanel topButtonPanel = new JPanel();
			topButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			this.ui.topPanel.add(topButtonPanel);

			// 「EXIT」ボタンを生成して配置
			this.ui.exitButton = new JButton("×");
			this.ui.exitButton.setFont(EXIT_BUTTON_FONT);
			this.ui.exitButton.setForeground(Color.RED);
			this.ui.exitButton.addActionListener(this.exitButtonListener);
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


	private class UIUpwardSynchronizer implements Runnable {

		private Prototype.State state = null;
		private Prototype.UI ui = null;

		public UIUpwardSynchronizer(Prototype.State state, Prototype.UI ui) {
			this.state = state;
			this.ui = ui;
		}

		@Override
		public void run() {
			this.ui.inputField.setText(state.inputText);
			this.ui.outputField.setText(state.outputText);
		}
	}


	@SuppressWarnings("unused")
	private class UIDownwardSynchronizer implements Runnable {

		private Prototype.State state = null;
		private Prototype.UI ui = null;

		public UIDownwardSynchronizer(Prototype.State state, Prototype.UI ui) {
			this.state = state;
			this.ui = ui;
		}

		@Override
		public void run() {
			state.inputText = this.ui.inputField.getText();
			state.outputText = this.ui.outputField.getText();
		}
	}

}
