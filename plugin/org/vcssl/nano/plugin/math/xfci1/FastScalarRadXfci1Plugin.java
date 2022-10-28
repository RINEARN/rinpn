package org.vcssl.nano.plugin.math.xfci1;

import org.vcssl.connect.Float64ScalarDataAccessorInterface1;
import org.vcssl.connect.ConnectorException;

public class FastScalarRadXfci1Plugin extends Float64ScalarOperationXfci1Plugin {

	@Override
	public final String getFunctionName() {
		return "rad";
	}

	@Override
	public final Object invoke(Object[] arguments) throws ConnectorException {
		double deg = ((Float64ScalarDataAccessorInterface1)arguments[1]).getFloat64ScalarData();
		double rad = Math.PI * deg / 180.0;
		((Float64ScalarDataAccessorInterface1)arguments[0]).setFloat64ScalarData(rad);
		return null;
	}
}
