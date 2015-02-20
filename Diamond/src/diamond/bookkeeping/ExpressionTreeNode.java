package diamond.bookkeeping;

import java.util.List;
import java.util.ArrayList;

import diamond.retenetwork.Bind;
import diamond.retenetwork.Filter;
import diamond.data.TriplePattern;

/** 
 *  ExpressionTreeNode that is used to build the <code>ExpressionTree</code>
 *  @author Rodolfo Kaplan Depena
 *  @author Slavcho Slavchev
 */
public class ExpressionTreeNode {

    private final GraphPatternType patternType;
    private final List<TriplePattern> triplePatterns;
    private final List<Filter> filterNodes;
    private final List<Bind> bindNodes;
    private final List<ExpressionTreeNode> children;

    /**
     * Create <code>ExpressionTreeNode</code>
     */
    public ExpressionTreeNode(GraphPatternType patternType) {
        this.patternType = patternType;
        triplePatterns = new ArrayList<TriplePattern>();
        filterNodes = new ArrayList<Filter>();
        bindNodes = new ArrayList<Bind>();
        children = new ArrayList<ExpressionTreeNode>();
    }

    @Override
    public String toString() {
        return "ExpressionTreeNode [patternType=" + patternType + ", triplePatterns=" + triplePatterns + ", filterNodes="
                + filterNodes + ", bindNodes=" + bindNodes + ", children=" + children + "]";
    }

    /**
     * Add <code>TriplePattern</code>
     * 
     * @param <code>TriplePattern</code>
     */
    public void addTriplePattern(TriplePattern tp) {
        triplePatterns.add(tp);
    }

    /**
     * Add <code>Filter</code>
     * 
     * @param <code>Filter</code>
     */
    public void addFilter(Filter filter) {
        filterNodes.add(filter);
    }

    /**
     * Add <code>Bind</code>
     * 
     * @param <code>Bind</code>
     */
    public void addBind(Bind bind) {
        bindNodes.add(bind);
    }

    /**
     * Add <code>ExpressionTreeNode</code>
     * 
     * @param <code>ExpressionTreeNode</code>
     */
    public void addChild(ExpressionTreeNode expressionTreeNode) {
        children.add(expressionTreeNode);
    }

    /**
     * Get <code>GraphPatternType</code>
     * 
     * @return <code>GraphPatternType</code>
     */
    public GraphPatternType getGraphPatternType() {
        return patternType;
    }

    /**
     * Get <code>List<Filter></code>
     * 
     * @return <code>List<Filter></code>
     */
    public List<Filter> getFilterNodes() {
        return filterNodes;
    }

    /**
     * Get <code>List<Bind></code>
     * 
     * @return <code>List<Bind></code>
     */
    public List<Bind> getBindNodes() {
        return bindNodes;
    }

    /**
     * Get <code>List<TriplePattern></code>
     * 
     * @return <code>List<TriplePattern></code>
     */
    public List<TriplePattern> getTriplePatterns() {
        return triplePatterns;
    }

    /**
     * Get <code>List<ExpressionTreeNode></code>
     * 
     * @return <code>List<ExpressionTreeNode></code>
     */
    public List<ExpressionTreeNode> getChildren() {
        return children;
    }
    
    public void deepPrint() {
        System.out.print(this.getGraphPatternType());
        System.out.print(" { ");
        for(ExpressionTreeNode child : this.getChildren()) {
            child.deepPrint();
        }
        System.out.print(" } ");
    }
}
