package diamond.builtincall;

import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.ExprNode;
import diamond.data.SPO;

/**
 * Determine if a variable is bound to a value.
 * @author Slavcho Slavchev
 */
public class Bound extends UnaryBuiltInCall {

    public Bound(ExprNode rhs) {
        super(SPO.EXPR, DataType.EXPR_OP, "Bound", rhs);
    }

    @Override
    public Element eval(Element rhs) {
        String bound = (rhs != null ? "1" : "0");
        return new Element(SPO.EXPR, DataType.NUMBER, bound);
    }

    @Override
    public String toString() {
        return "Bound(" + rightChild() + ")";
    }
}