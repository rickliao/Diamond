//Not used for now. Might be used later.

package diamond.managers;

import org.openrdf.model.Resource;

public class Query implements Resource {
	
	private static final long serialVersionUID = 1L;
	private String queryText;
	
	public Query(String text) {
		queryText = text;
	}

	public void setQueryText(String text) {
		queryText = text;
	}
	
	@Override
	public String stringValue() {
		return queryText;
	}
	
	public boolean equals(Query other) {
		return queryText.equals(other.stringValue());
	}
	
	public String toString() {
		return queryText;
	}

}
