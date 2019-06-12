/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.model;

public interface AsynchronousCalculationListener {

	// event パッケージの RunButtonListener や RunKeyLister クラス内で匿名クラスとして実装

	public abstract void calculationFinished(String outputText);
}