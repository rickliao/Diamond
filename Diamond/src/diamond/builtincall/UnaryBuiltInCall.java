package diamond.builtincall;

import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.ExprNode;
import diamond.data.SPO;

/**
 * <code>UnaryBuiltInCall</code> evaluates one element.
 * @author Slavcho Slavchev
 */
public abstract class UnaryBuiltInCall extends BuiltInCall {

    protected UnaryBuiltInCall(SPO spo, DataType dataType, String data, ExprNode rhs) {
        super(spo, dataType, data);
        this.setRightChild(rhs);
    }
    
    public abstract Element eval(Element rhs);
}