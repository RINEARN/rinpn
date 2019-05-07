/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.presenter;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.rinearn.processornano.view.ViewInterface;

public final class WindowMouseListener implements MouseListener, MouseMotionListener {

	private ViewInterface view = null;
	private int mousePressedX = -1;
	private int mousePressedY = -1;

	protected WindowMouseListener(ViewInterface view) {
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

		int x = this.view.getLocationX();
		int y = this.view.getLocationY();

		this.view.setLocation(x + dx, y + dy);
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
