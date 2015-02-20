package diamond.builtincall;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.ExprNode;
import diamond.data.SPO;

/**
 * Check if an element follows a pattern.
 * @author Slavcho Slavchev
 */
public class Regex extends BinaryBuiltInCall {

    private final String flag;
    
    public Regex(ExprNode lhs, ExprNode rhs, String flag) {
        super(SPO.EXPR, DataType.EXPR_OP, "Regex", lhs, rhs);
        this.flag = flag;
    }

    /**
     * Evaluate whether or not the triple token contains this pattern
     * 
     * @param tripleToken <code>TripleToken</code>
     * @return true, if a pattern exists, otherwise false
     */
    @Override
    public Element eval(Element lhs, Element rhs) {
        if(lhs == null || rhs == null) {
            return null; // Illegal Expression
        }
        
        boolean flagSet = false;
        int flag = -1;

        if (this.flag != null) {
            if (this.flag.equals("\"i\"")) {
                flag = Pattern.CASE_INSENSITIVE;
                flagSet = true;
            } else if (this.flag.equals("\"x\"")) {
                flag = Pattern.COMMENTS;
                flagSet = true;
            } else if (this.flag.equals("\"m\"")) {
                flag = Pattern.MULTILINE;
                flagSet = true;
            } else if (this.flag.equals("\"s\"")) {
                flag = Pattern.DOTALL;
                flagSet = true;
            } else if (this.flag.equals("\"u\"")) {
                flag = Pattern.UNICODE_CASE;
                flagSet = true;
            } else if (this.flag.equals("\"d\"")) {
                flag = Pattern.UNIX_LINES;
                flagSet = true;
            } else {
                throw new IllegalArgumentException("Flag " + this.flag
                        + " not valid. Should either be i, x, m, s, u, or d");
            }
        }
        
        boolean matchesRegex = matchesRegex(lhs.getData(), rhs.getData(), flagSet, flag);
        return new Element(SPO.EXPR, DataType.NUMBER, matchesRegex ? "1" : "0");
    }

    private boolean matchesRegex(String input, String regex, boolean flagSet, int flag) {
        if (flagSet) {
            Pattern p = Pattern.compile(regex.replace("\"", ""), flag); // TODO Fix
            Matcher m = p.matcher(input.replace("\"", ""));
            boolean found = m.find();
            return found;
        } else {
            Pattern p = Pattern.compile(regex.replace("\"", ""));
            Matcher m = p.matcher(input.replace("\"", ""));
            boolean found = m.find();
            return found;
        }
    }

    @Override
    public String toString() {
        return "Regex(" + leftChild() + ", " + rightChild() + ", " + flag + ")";
    }
}