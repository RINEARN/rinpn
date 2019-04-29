/*
 * Copyright(C) 2019 RINEARN (Fumihiro Matsui)
 * This software is released under the MIT License.
 */

package com.rinearn.processornano.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.rinearn.processornano.RinearnProcessorNanoException;
import com.rinearn.processornano.spec.LocaleCode;

public class PluginLoaderTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws RinearnProcessorNanoException {

		Object loadedPlugin = PluginLoader.loadPlugin(
			"com.rinearn.processornano.util.TestPlugin",
			new String[] { "./" },
			LocaleCode.getDefaultLocaleCode()
		);

		assertTrue(loadedPlugin instanceof TestPlugin);

		TestPlugin castedPlugin = (TestPlugin)loadedPlugin;

		assertEquals("Hello, I am a test plugin !", castedPlugin.invokeExamplePluginFeature());

		assertEquals(124318312, castedPlugin.examplePluginData);
		castedPlugin.examplePluginData = 21103212;
		assertEquals(21103212, castedPlugin.examplePluginData);
	}

}
