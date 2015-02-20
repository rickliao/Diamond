package diamond.builtincall;

import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.ExprNode;
import diamond.data.SPO;

/**
 * Determine if an element is IRI.
 * @author Slavcho Slavchev
 */
public class IsIRI extends UnaryBuiltInCall {

    public IsIRI(ExprNode rhs) {
        super(SPO.EXPR, DataType.EXPR_OP, "IsIRI", rhs);
    }

    @Override
    public Element eval(Element rhs) {
        String isIRI = (rhs.getDataType() == DataType.URL ? "1" : "0");
        return new Element(SPO.EXPR, DataType.NUMBER, isIRI);
    }

    @Override
    public String toString() {
        return "IsIRI(" + rightChild() + ")";
    }
}