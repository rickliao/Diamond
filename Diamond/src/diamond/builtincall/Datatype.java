package diamond.builtincall;

import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.ExprNode;
import diamond.data.SPO;

/**
 * <code>Datatype</code> is a SPARQL <code>BuiltInCall</code>.
 * @author Slavcho Slavchev
 */
public class Datatype extends UnaryBuiltInCall {

    public Datatype(ExprNode rhs) {
        super(SPO.EXPR, DataType.EXPR_OP, "Datatype", rhs);
    }

    @Override
    public Element eval(Element rhs) {
        DataType dataType = rhs.getDataType();
        String data = rhs.getData();
        String resultType = DataType.STRING_DEF;
        
        if(data.contains(DataType.TYPE_DEF)) {
            resultType = data.substring(data.indexOf(DataType.TYPE_DEF) + 2);
        } else if(dataType == DataType.NUMBER) {
            resultType = DataType.DOUBLE_DEF;
        }
        return new Element(SPO.EXPR, DataType.URL, resultType);
    }
    
    @Override
    public String toString() {
        return "Datatype(" + rightChild() + ")";
    }
}