/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.presenter;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.rinearn.processornano.view.ViewImpl;

public final class InputFieldMouseListener implements MouseListener {

	private ViewImpl view = null;

	protected InputFieldMouseListener(ViewImpl view) {
		this.view = view;
	}

	@Override
	public final void mouseClicked(MouseEvent e) {
		if(javax.swing.SwingUtilities.isRightMouseButton(e)){
			this.view.textFieldPopupMenu.show(this.view.inputField, e.getX(), e.getY());
		}
	}

	@Override
	public final void mousePressed(MouseEvent e) {
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
