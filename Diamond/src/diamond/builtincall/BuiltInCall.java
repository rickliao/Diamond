package diamond.builtincall;

import diamond.data.DataType;
import diamond.data.ExprNode;
import diamond.data.SPO;

/**
 * <code>BuiltInCall</code> is a SPARQL operator.
 * @author Slavcho Slavchev
 */
public abstract class BuiltInCall extends ExprNode {
    
    protected BuiltInCall(SPO spo, DataType dataType, String data) {
        super(spo, dataType, data);
    }
    
    @Override
    public abstract String toString();
}