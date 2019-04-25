/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano;


@SuppressWarnings("serial")
public final class RinearnProcessorNanoException extends Exception {
	public RinearnProcessorNanoException(Throwable parentThrowable) {
		super(parentThrowable);
	}
	public RinearnProcessorNanoException(String errorMessage) {
		super(errorMessage);
	}
}
