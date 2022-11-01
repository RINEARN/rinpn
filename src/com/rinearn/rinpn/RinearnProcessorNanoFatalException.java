/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.rinpn;


@SuppressWarnings("serial")
public final class RinearnProcessorNanoFatalException extends RuntimeException {
	public RinearnProcessorNanoFatalException(Throwable parentThrowable) {
		super(parentThrowable);
	}
	public RinearnProcessorNanoFatalException(String errorMessage) {
		super(errorMessage);
	}
}
