package diamond.exception;

/**
 * @author Rodolfo Kaplan Depena
 */
public class MalformedRelationalExpressionException extends Exception {

    private static final long serialVersionUID = -9075536286923546241L;

    public MalformedRelationalExpressionException() {
        super("The relational expression is malformed. There should be an appropriate right operand, operator, and left operand");
    }

    public MalformedRelationalExpressionException(String message) {
        super(message);
    }
}