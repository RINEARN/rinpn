/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.calculator;

public interface AsynchronousScriptListener {

	// event パッケージの RunButtonListener や RunKeyLister クラス内で匿名クラスとして実装

	public abstract void scriptingFinished();
}