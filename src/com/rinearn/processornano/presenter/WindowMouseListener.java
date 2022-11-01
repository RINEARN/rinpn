/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.presenter;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.rinearn.processornano.view.View;

public final class WindowMouseListener implements MouseListener, MouseMotionListener {

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
