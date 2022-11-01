/*
 * Copyright(C) 2019-2022 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.presenter;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.rinearn.processornano.view.View;

public final class OutputFieldMouseListener implements MouseListener {

	private View view = null;

	protected OutputFieldMouseListener(View view) {
		this.view = view;
	}

	@Override
	public final void mouseClicked(MouseEvent e) {
		if(javax.swing.SwingUtilities.isRightMouseButton(e)){
			this.view.textFieldPopupMenu.show(this.view.outputField, e.getX(), e.getY());
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
