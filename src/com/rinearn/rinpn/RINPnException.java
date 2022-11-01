/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn;


@SuppressWarnings("serial")
public final class RINPnException extends Exception {
	public RINPnException(Throwable parentThrowable) {
		super(parentThrowable);
	}
	public RINPnException(String errorMessage) {
		super(errorMessage);
	}
}
