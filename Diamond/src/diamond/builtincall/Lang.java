package diamond.builtincall;

import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.ExprNode;
import diamond.data.SPO;

/**
 * Extract the language type of an element.
 * @author Slavcho Slavchev
 */
public class Lang extends UnaryBuiltInCall {

    public Lang(ExprNode rhs) {
        super(SPO.EXPR, DataType.EXPR_OP, "Lang", rhs);
    }

    @Override
    public Element eval(Element rhs) {
        String data = rhs.getData(), lang = "";
        int idx1 = data.lastIndexOf('@');
        if(idx1 != -1) {
            lang = '"' + data.substring(idx1+1, data.length()) + '"';
        }
        return new Element(SPO.EXPR, DataType.LITERAL, lang);
    }

    @Override
    public String toString() {
        return "Lang(" + rightChild() + ")";
    }
}