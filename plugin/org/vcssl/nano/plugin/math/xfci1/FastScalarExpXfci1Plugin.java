package org.vcssl.nano.plugin.math.xfci1;

import org.vcssl.connect.Float64ScalarDataAccessorInterface1;
import org.vcssl.connect.ConnectorException;

public class FastScalarExpXfci1Plugin extends Float64ScalarOperationXfci1Plugin {

	@Override
	public final String getFunctionName() {
		return "exp";
	}

	@Override
	public final Object invoke(Object[] arguments) throws ConnectorException {
		((Float64ScalarDataAccessorInterface1)arguments[0]).setFloat64ScalarData(
			Math.exp(
				((Float64ScalarDataAccessorInterface1)arguments[1]).getFloat64ScalarData()
			)
		);
		return null;
	}
}
