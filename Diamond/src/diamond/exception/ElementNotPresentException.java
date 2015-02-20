package diamond.exception;

/**
 * @author Rodolfo Kaplan Depena
 */
public class ElementNotPresentException extends Exception {

    private static final long serialVersionUID = 2189812473601769372L;

    public ElementNotPresentException(String message) {
        super(message);
    }
}