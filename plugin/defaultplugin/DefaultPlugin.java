package defaultplugin;

import org.vcssl.connect.ExternalNamespaceConnector1;
import org.vcssl.connect.ExternalFunctionConnector1;
import org.vcssl.connect.ExternalVariableConnector1;
import org.vcssl.connect.ConnectorException;
import org.vcssl.connect.ConnectorPermission;
import org.vcssl.connect.ArrayDataContainer1;
import javax.swing.JOptionPane;


// How to compile this plug-in:
// 
//     cd <software_directory>/plugin
//     javac -encoding UTF-8 defaultplugin/DefaultPlugin.java @sourcelist.txt

public class DefaultPlugin implements ExternalFunctionConnector1 {
		
		@Override
		public String getFunctionName() { return "testout"; }
		
		@Override
		public boolean hasParameterNames() { return true; }
		
		@Override
		public String[] getParameterNames() { return new String[]{ "value" }; }
		
		@Override
		public Class<?>[] getParameterClasses() { return new Class<?>[]{ double.class }; } 
		
		@Override
		public Class<?> getReturnClass(Class<?>[] parameterClasses) { return Void.class; }
		
		@Override
		public boolean isVariadic() { return false; }
		
		@Override
		public String[] getNecessaryPermissions() { return new String[]{ ConnectorPermission.NONE }; }
		
		@Override
		public String[] getUnnecessaryPermissions() { return new String[]{ ConnectorPermission.ALL }; }
		
		@Override
		public void initializeForConnection(Object engineConnector) throws ConnectorException { }
		
		@Override
		public void finalizeForDisconnection(Object engineConnector) throws ConnectorException { }
		
		@Override
		public void initializeForExecution(Object engineConnector) throws ConnectorException { }
		
		@Override
		public void finalizeForTermination(Object engineConnector) throws ConnectorException { }
		
		@Override
		public boolean isDataConversionNecessary() { return true; }
		
		@Override
		public Object invoke(Object[] arguments) throws ConnectorException {
			
			@SuppressWarnings("unchecked")
			Double argValue = (Double)arguments[0];
			JOptionPane.showMessageDialog(null, "arg=" + argValue, "TestPlugin.testout(float)", JOptionPane.PLAIN_MESSAGE);
			
			
			return null;
		}
}

