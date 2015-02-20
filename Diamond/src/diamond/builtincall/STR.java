package diamond.builtincall;

import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.ExprNode;
import diamond.data.SPO;

/**
 * Convert element to literal.
 * @author Slavcho Slavchev
 */
public class STR extends UnaryBuiltInCall {

    public STR(ExprNode rhs) {
        super(SPO.EXPR, DataType.EXPR_OP, "STR", rhs);
    }

    @Override
    public Element eval(Element element) {
        DataType elementDataType = element.getDataType();
        String elementStr = element.getData();
        String resultData = elementStr;
       
        if(elementStr.charAt(0) == '<' && elementStr.charAt(elementStr.length() - 1) == '>'
                && elementDataType == DataType.URL) {
            resultData = '"' + elementStr.substring(1, (elementStr.length() - 1)) + '"';
        } else {   
            resultData = '"' + DataType.stripData(elementStr) + '"';
        }
        return new Element(element.getSpo(), DataType.LITERAL, resultData);
    }
    
    @Override
    public String toString() {
        return "STR(" + rightChild() + ")";
    }
}