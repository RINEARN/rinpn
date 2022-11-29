/*
 * Copyright(C) 2019 RINEARN
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn;


@SuppressWarnings("serial")
public final class RINPnFatalException extends RuntimeException {
	public RINPnFatalException(Throwable parentThrowable) {
		super(parentThrowable);
	}
	public RINPnFatalException(String errorMessage) {
		super(errorMessage);
	}
}
