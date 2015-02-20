package diamond.builtincall;

import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.ExprNode;
import diamond.data.SPO;

/**
 * <code>BinaryBuiltInCall</code> evaluates two elements.
 * @author Slavcho Slavchev
 */
public abstract class BinaryBuiltInCall extends BuiltInCall {

    protected BinaryBuiltInCall(SPO spo, DataType dataType, String data, ExprNode lhs, ExprNode rhs) {
        super(spo, dataType, data);
        this.setLeftChild(lhs);
        this.setRightChild(rhs);
    }
    
    public abstract Element eval(Element lhs, Element rhs);
}