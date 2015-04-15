package diamond.managers;
import diamond.data.SolutionSet;

//Returned by LinkedDataManager as the result of executing a SPRQL query

public class QueryStats {
    private SolutionSet solutionSet;
    private int dereferencedURLs;
    private int numTriples;
    
    public QueryStats(SolutionSet s, int dU, int nT) {
    	solutionSet = s;
    	dereferencedURLs = dU;
    	numTriples = nT;
    }
    
	public SolutionSet getSolutionSet() {
		return solutionSet;
	}
	public void setSolutionSet(SolutionSet solutionSet) {
		this.solutionSet = solutionSet;
	}
	public int getDereferencedURLs() {
		return dereferencedURLs;
	}
	public void setDereferencedURLs(int dereferencedURLs) {
		this.dereferencedURLs = dereferencedURLs;
	}
	public int getNumTriples() {
		return numTriples;
	}
	public void setNumTriples(int numTriples) {
		this.numTriples = numTriples;
	}
	
	public boolean equals(QueryStats qs) {
		return this.solutionSet.size() == qs.getSolutionSet().size() && 
				this.dereferencedURLs == qs.getDereferencedURLs() &&
				this.numTriples == qs.getNumTriples();
	}
}
