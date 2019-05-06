package com.rinearn.processornano.view;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface ViewInterface {

	public abstract void setOutputText(String text);

	public abstract String getInputText();

	public abstract void setLocation(int x, int y);

	public abstract int getLocationX();

	public abstract int getLocationY();

	public abstract void popupInputFieldMenu(int x, int y);

	public abstract void popupOutputFieldMenu(int x, int y);

	public abstract void addFrameMouseListener(MouseListener listener);

	public abstract void addFrameMouseMotionListener(MouseMotionListener listener);

	public abstract void addInputFieldKeyListener(KeyListener listener);

	public abstract void addInputFieldMouseListener(MouseListener listener);

	public abstract void addOutputFieldMouseListener(MouseListener listener);

	public abstract void addRunButtonActionListener(ActionListener listener);

	public abstract void addExitButtonActionListener(ActionListener listener);
}
