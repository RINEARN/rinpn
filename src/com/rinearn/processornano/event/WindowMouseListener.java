/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.rinearn.processornano.ui.UIContainer;

public final class WindowMouseListener implements MouseListener, MouseMotionListener {

	private UIContainer ui = null;
	private int mousePressedX = -1;
	private int mousePressedY = -1;

	public WindowMouseListener(UIContainer ui) {
		this.ui = ui;
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

		int x = this.ui.frame.getX();
		int y = this.ui.frame.getY();

		this.ui.frame.setLocation(x + dx, y + dy);
	}

	@Override
	public final void mouseMoved(MouseEvent e) {
	}

	@Override
	public final void mouseClicked(MouseEvent e) {

	}

	@Override
	public final void mouseReleased(MouseEvent e) {
	}

	@Override
	public final void mouseEntered(MouseEvent e) {
	}

	@Override
	public final void mouseExited(MouseEvent e) {
	}
}
