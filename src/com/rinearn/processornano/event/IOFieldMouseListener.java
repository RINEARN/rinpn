/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextField;

import com.rinearn.processornano.ui.MessageManager;
import com.rinearn.processornano.ui.UIContainer;

public final class IOFieldMouseListener implements MouseListener {

	private UIContainer ui = null;

	public IOFieldMouseListener(UIContainer ui) {
		this.ui = ui;
	}

	@Override
	public final void mouseClicked(MouseEvent e) {
		if (!(e.getSource() instanceof JTextField)) {
			MessageManager.showErrorMessage(
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
