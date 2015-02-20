package diamond.retenetwork;

import java.util.Map;

import diamond.data.Element;
import diamond.data.TripleToken;
import diamond.exception.BindVariableNotUniqueException;

/**
 * Binds a variable to the result of a RelationalExpression.
 * 
 * @author Slavcho Slavchev
 */
public class Bind extends ReteNode {

    private final String varName;
    private final RelationalExpression relExpr;

    public Bind(String varName, RelationalExpression relExpr) {
        super();
        this.varName = varName;
        this.relExpr = relExpr;
    }

    public String getVar() {
        return varName;
    }

    public RelationalExpression getRelExpr() {
        return relExpr;
    }

    @Override
    public boolean eval(TripleToken tripleToken, ReteNode caller) throws Exception {
        boolean isEvaluated = false;
        
        /* Extract all variable bindings from the triple token */
        Map<String, Element> binds = tripleToken.getBinds();
        if(binds.get(varName) != null)
            throw new BindVariableNotUniqueException(varName);
        
        /* Bind the variable to the result of relExpr */
        Element result = relExpr.eval(binds);
        
        if (result != null) {
            TripleToken newToken = new TripleToken(tripleToken.getTag(), tripleToken);
            newToken.getBinds().put(varName, result);
            
            for (ReteNode child : getChildren()) {
                isEvaluated |= child.eval(newToken, this);
            }
        }
        
        return isEvaluated;
    }

    @Override
    public String toString() {
        return "Bind[" + getId() + "]";
    }
}
