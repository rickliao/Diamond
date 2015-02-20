package diamond.exception;

/**
 * @author Slavcho Slavchev
 */
public class MissingVarValueUponRuleFireException extends Exception {

    private static final long serialVersionUID = -2684925290802828609L;

    public MissingVarValueUponRuleFireException(String message) {
        super(message);
    }
}