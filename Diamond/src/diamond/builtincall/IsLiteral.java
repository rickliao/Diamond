package diamond.builtincall;

import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.ExprNode;
import diamond.data.SPO;

/**
 * Determine if an element is literal.
 * @author Slavcho Slavchev
 */
public class IsLiteral extends UnaryBuiltInCall {

    public IsLiteral(ExprNode rhs) {
        super(SPO.EXPR, DataType.EXPR_OP, "IsLiteral", rhs);
    }

    @Override
    public Element eval(Element rhs) {
        String isLiteral = (rhs.getDataType() == DataType.LITERAL ? "1" : "0");
        return new Element(SPO.EXPR, DataType.NUMBER, isLiteral);
    }

    @Override
    public String toString() {
        return "IsLiteral(" + rightChild() + ")";
    }
}