package diamond.retenetwork;

import java.util.Map;

import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.TripleToken;

/**
 * <code>Filter</code> node used to filter out tokens in a Rete network.
 * 
 * @author Slavcho Slavchev
 */
public class Filter extends ReteNode {

    private final RelationalExpression relExpr;

    public Filter(RelationalExpression relExpr) {
        super();
        this.relExpr = relExpr;
    }

    public RelationalExpression getRelExpr() {
        return relExpr;
    }

    @Override
    public boolean eval(TripleToken tripleToken, ReteNode caller) throws Exception {
        boolean isEvaluated = false;
        
        /* Extract all variable bindings from the triple token */
        Map<String, Element> binds = tripleToken.getBinds();
        
        /* Filter the triple token */
        Element result = relExpr.eval(binds);
        
        if (result != null) {
            if(notFiltered(DataType.stripData(result.getData()))) {
                for (ReteNode child : getChildren()) {
                    isEvaluated |= child.eval(tripleToken, this);
                }
            }
        }

        return isEvaluated;
    }

    /**
     * Helper method to map String data to boolean
     */
    private boolean notFiltered(String data) {
        return !(data.equals("") || data.equals("0") || data.equals("0.0") || data.equals("false"));
    }
    
    @Override
    public String toString() {
        return "Filter[" + getId() + "]";
    }
}