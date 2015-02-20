package diamond.exception;

/**
 * @author Slavcho Slavchev
 */
public class BindVariableNotUniqueException extends Exception {

    private static final long serialVersionUID = -5522904983051126126L;

    public BindVariableNotUniqueException(String message) {
        super(message);
    }
}
