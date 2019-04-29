package defaultplugin;

import org.vcssl.connect.ExternalLibraryConnector1;
import org.vcssl.connect.ExternalFunctionConnector1;
import org.vcssl.connect.ExternalFunctionException;
import org.vcssl.connect.ExternalVariableConnector1;
import org.vcssl.connect.ExternalVariableException;
import org.vcssl.connect.FieldXvci1Adapter;
import org.vcssl.connect.MethodXfci1Adapter;
import org.vcssl.connect.ExternalPermission;
import org.vcssl.connect.ArrayDataContainer1;
import javax.swing.JOptionPane;


// How to compile this plug-in:
// 
//     cd <software_directory>/plugin
//     javac -encoding UTF-8 defaultplugin/DefaultPlugin.java @sourcelist.txt

public class DefaultPlugin implements ExternalFunctionConnector1 {
	
		public String getFunctionName() { return "testout"; }
		public boolean hasParameterNames() { return true; }
		public String[] getParameterNames() { return new String[]{ "value" }; }
		public Class<?>[] getParameterClasses() { return new Class<?>[]{ double.class }; } 
		public Class<?> getReturnClass() { return Void.class; }
		public boolean isVariadic() { return false; }
		public String[] getNecessaryPermissions() { return new String[]{ ExternalPermission.NONE }; }
		public String[] getUnnecessaryPermissions() { return new String[]{ ExternalPermission.ALL }; }
		public void setEngine(Object engineConnector) { }
		public void initializeForConnection() { }
		public void finalizeForDisconnection() { }
		public void initializeForExecution() { }
		public void finalizeForTermination() { }

		public boolean isDataConversionNecessary() { return true; }
	
		public Object invoke(Object[] arguments) throws ExternalFunctionException {
			
			@SuppressWarnings("unchecked")
			Double argValue = (Double)arguments[0];
			JOptionPane.showMessageDialog(null, "arg=" + argValue, "TestPlugin.testout(float)", JOptionPane.PLAIN_MESSAGE);
			
			
			return null;
		}
}

