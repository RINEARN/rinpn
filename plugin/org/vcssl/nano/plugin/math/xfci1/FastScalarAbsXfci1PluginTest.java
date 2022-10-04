package org.vcssl.nano.plugin.math.xfci1;

import org.vcssl.nano.vm.accelerator.Float64ScalarCache;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vcssl.connect.ConnectorException;
import org.vcssl.connect.ExternalFunctionConnectorInterface1;

public class FastScalarAbsXfci1PluginTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test() throws ConnectorException {
		ExternalFunctionConnectorInterface1 function = new FastScalarAbsXfci1Plugin();

		// Check function name
		// 関数名を検査
		assertEquals("abs", function.getFunctionName());

		// Check number and types of arguments
		// 引数の個数と型を検査
		assertEquals(1, function.getParameterClasses().length);
		assertTrue(function.getParameterClasses()[0] == double.class);
		assertFalse(function.getParameterArrayRankArbitrarinesses()[0]);

		// Check type of return value
		// 戻り値の型を検査
		assertEquals(double.class, function.getReturnClass(new Class<?>[] { double.class }));
		assertFalse(function.isReturnArrayRankArbitrary());

		// Prepare input/output data
		// 入出力データを用意
		Float64ScalarCache inputDataContainer = new Float64ScalarCache();
		Float64ScalarCache outputDataContainer = new Float64ScalarCache();

		// Operate data, and check results
		// 以下、演算を実行して結果を確認

		inputDataContainer.setFloat64ScalarData(0.0);
		outputDataContainer.setFloat64ScalarData(0.0);
		function.invoke(new Object[] { outputDataContainer, inputDataContainer });
		assertTrue(
			Double.valueOf( Math.abs(0.0) ).equals( outputDataContainer.getFloat64ScalarData() )
		);

		inputDataContainer.setFloat64ScalarData(-1234.5);
		outputDataContainer.setFloat64ScalarData(0.0);
		function.invoke(new Object[] { outputDataContainer, inputDataContainer });
		assertTrue(
			Double.valueOf( Math.abs(-1234.5) ).equals( outputDataContainer.getFloat64ScalarData() )
		);

		inputDataContainer.setFloat64ScalarData(1234.5);
		outputDataContainer.setFloat64ScalarData(0.0);
		function.invoke(new Object[] { outputDataContainer, inputDataContainer });
		assertTrue(
			Double.valueOf( Math.abs(1234.5) ).equals( outputDataContainer.getFloat64ScalarData() )
		);

	}

}
