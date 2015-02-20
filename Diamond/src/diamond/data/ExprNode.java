package diamond.data;

/**
 * An <code>ExprNode</code> refers to an Element of a Relational Expression
 * 
 * @author Slavcho Slavchev
 */
public class ExprNode extends Element {

    private ExprNode leftChild = null;
    private ExprNode rightChild = null;

    public ExprNode(SPO spo, DataType dataType, String data) {
        super(spo, dataType, data);
    }

    public ExprNode(Element e) {
        super(e.getSpo(), e.getDataType(), e.getData());
    }

    public void setLeftChild(ExprNode n) {
        this.leftChild = n;
    }

    public void setRightChild(ExprNode n) {
        this.rightChild = n;
    }

    public ExprNode leftChild() {
        return this.leftChild;
    }

    public ExprNode rightChild() {
        return this.rightChild;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        boolean unary = true;
        if (leftChild != null && rightChild != null)
            unary = false;

        if (!unary)
            result.append("(");
        if (leftChild != null)
            result.append(leftChild).append(" ");
        result.append(this.getData());
        if (!unary)
            result.append(" ");
        if (rightChild != null)
            result.append(rightChild);
        if (!unary)
            result.append(")");
        return result.toString();
    }
}
