/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn.presenter;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.rinearn.rinpn.view.View;

public final class InputFieldMouseListener implements MouseListener {

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
