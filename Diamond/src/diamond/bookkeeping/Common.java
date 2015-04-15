package diamond.bookkeeping;

import diamond.data.DataType;

public class Common {

	/**
	 * Helper Methods to map Doubles and Booleans
	 */
	public static Double toDouble(String elData) {
	    String strippedData = DataType.stripData(elData);
	
	    try {
	        return Double.parseDouble(strippedData);
	    } catch (NumberFormatException e) {}
	
	    if(strippedData.equals("true")) {
	        return 1.0;
	    } else if(strippedData.equals("false")) {
	        return 0.0;
	    }
	    
	    return null; /* Not a Numeric Element */
	}

	public static boolean toBool(String elData) {
	    Double val = toDouble(elData);
	    return (val == null ? !(elData.equals("\"\"")) : toBool(val));
	}

	public static boolean toBool(Double x) {
	    return (Math.abs(x) > 1e-10);
	}

	public static String wrap(boolean x) {
	    return ((x == true) ? "1" : "0");
	}
	
	public static String wrap(double x) {
	    Double val = new Double(x);
	    return val.toString();
	}
}
