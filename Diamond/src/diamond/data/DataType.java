package diamond.data;

/**
 * Determine what kind of data type an Element is.
 * 
 * @author Slavcho Slavchev
 */
public enum DataType {
	LITERAL, NUMBER, URL, VARIABLE, BLANK_NODE, EXPR_OP;

	public static final String TYPE_DEF = "^^";
	public static final String INTEGER_DEF = "<http://www.w3.org/2001/XMLSchema#integer>";
	public static final String DOUBLE_DEF = "<http://www.w3.org/2001/XMLSchema#double>";
	public static final String DECIMAL_DEF = "<http://www.w3.org/2001/XMLSchema#decimal>";
	public static final String BOOLEAN_DEF = "<http://www.w3.org/2001/XMLSchema#boolean>";
	public static final String STRING_DEF = "<http://www.w3.org/2001/XMLSchema#string>";

	/**
	 * Determine if the data represents a URI
	 */
	public static boolean isURL(String data) {
		boolean isURL = false;
		if ((data.charAt(0) == '<' && data.charAt(data.length() - 1) == '>') || data.startsWith("http")
				|| data.startsWith("<http") || data.startsWith("urn:") || data.startsWith("<urn:")
				|| data.startsWith("<mailto:") || data.startsWith("mailto:")) {
			isURL = true;
		}
		return isURL;
	}

	/**
	 * Determine if the data represents a Numeric type
	 */
	public static boolean isNumber(String data) {
		boolean isNumber = false;
		if (data.contains(TYPE_DEF)) {
			if (data.contains(INTEGER_DEF) || data.contains(DOUBLE_DEF) || data.contains(DECIMAL_DEF)
					|| data.contains(BOOLEAN_DEF)) {
				isNumber = true;
			}
		} else {
			try {
				Double.parseDouble(data);
				isNumber = true;
			} catch (NumberFormatException e) {
			}
		}
		return isNumber;
	}

	/**
	 * Determine if the data represents a Blank Node
	 */
	public static boolean isBlankNode(String data) {
		boolean isBlankNode = false;
		if (data.startsWith("_:") || data.startsWith("node")) {
			isBlankNode = true;
		}
		return isBlankNode;
	}

	/**
	 * Determine the data type of a given data
	 */
	public static DataType determineDataType(String data) {
		if (isURL(data)) {
			return DataType.URL;
		} /*else if (isBlankNode(data)) {
			throw new IllegalArgumentException("Diamond does not support blank nodes: " + data);
		} */ else if (isNumber(data)) {
			return DataType.NUMBER;
		} else {
			return DataType.LITERAL;
		}
	}

	/**
	 * Extract the data without the meta-data
	 */
	public static String stripData(String data) {
		String resultData = data;
		try {
			if (data.charAt(0) == '"') {
				int idx_def = data.indexOf("\"^^");
				int idx_lang = data.indexOf("\"@");
				int dataLen = data.length();

				if (idx_def != -1) {
					resultData = data.substring(1, idx_def);
				} else if (idx_lang != -1) {
					resultData = data.substring(1, idx_lang);
				} else if (data.charAt(dataLen - 1) == '"') {
					resultData = data.substring(1, dataLen - 1);
				}
			}
		} catch (Exception e) {
			System.out.println(data);
		}
		return resultData;
	}
}