package diamond.retenetwork;

import java.util.Map;

import diamond.builtincall.BinaryBuiltInCall;
import diamond.builtincall.BuiltInCall;
import diamond.builtincall.UnaryBuiltInCall;
import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.ExprNode;
import diamond.data.SPO;

/**
 * A <code>RelationalExpression</code> used to evaluate a particular solution.
 * 
 * @author Slavcho Slavchev
 */
public class RelationalExpression {

    private final ExprNode root;

    public RelationalExpression(ExprNode root) {
        this.root = root;
    }

    public ExprNode getRoot() {
        return root;
    }

    /**
     * Recursive Evaluation of a Relational Expression given list of Bindings
     */
    public Element eval(Map<String, Element> bindings) throws Exception {
        if (root == null) {
            return null; /* Illegal RelExpression */
        }
        
        /* Normal Execution */
        return evalR(this.root, bindings);
    }

    private Element evalR(ExprNode root, Map<String, Element> bindings) throws Exception {
        if (root == null) { // Illegal RelExpression
            return null;
        } else if(root instanceof BuiltInCall) { // BuiltInCall
            if(root instanceof UnaryBuiltInCall) {
                Element rhs = evalR(root.rightChild(), bindings);
                return ((UnaryBuiltInCall) root).eval(rhs);
            } else if(root instanceof BinaryBuiltInCall) {
                Element lhs = evalR(root.leftChild(), bindings);
                Element rhs = evalR(root.rightChild(), bindings);
                return ((BinaryBuiltInCall) root).eval(lhs, rhs);
            }
        }
        
        DataType rootType = root.getDataType();
        if (rootType == DataType.VARIABLE) {
            String varName = root.getData();
            return bindings.get(varName);
        } else if (rootType == DataType.LITERAL || rootType == DataType.URL || rootType == DataType.NUMBER) {
            return root;
        }
        
        else if (rootType == DataType.EXPR_OP) {
            String op = root.getData();
            ExprNode lhs = root.leftChild();
            ExprNode rhs = root.rightChild();
            if (lhs == null && rhs != null) { /* Unary Operation */
                Element arg1 = evalR(rhs, bindings);
                return evalU(op, arg1);
            } else if (lhs != null && rhs != null) { /* Binary Operation */
                Element arg1 = evalR(lhs, bindings);
                Element arg2 = evalR(rhs, bindings);
                return evalB(op, arg1, arg2);
            }
        }
        return null; /* Illegal RelExpression */
    }

    /**
     * Evaluate Unary operation (!x, -x, +x)
     * 
     * @return null upon bad RelExpression
     */
    private Element evalU(String op, Element x) {
        if (x != null && x.getDataType() == DataType.NUMBER) {
            double val = toDouble(x.getData());
            if (op.equals("!")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(!toBool(val)));
            } else if (op.equals("-")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(-val));
            } else if (op.equals("+")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(val));
            }
        } else if(x != null && op.equals("!")) {
            return new Element(SPO.EXPR, DataType.NUMBER, wrap(x.getData().equals("\"\"")));
        }
        return null; /* Illegal RelExpression */
    }

    /**
     * Evaluate Binary operation (+, -, *, /, ==, !=, <, >, <=, >=, ||, &&)
     * 
     * @return null upon bad RelExpression
     */
    private Element evalB(String op, Element arg1, Element arg2) {
        //if(op.equals("!=")) {
        //    System.out.println(arg1 + " " + arg2); // query 5 + DISTINCT
        //}
        
        if(arg1 == null) { /* Illegal RelExpression */
            return null;
        } else if (arg2 == null) {
            if(arg1.getDataType() == DataType.NUMBER) {
                boolean t = toBool(toDouble(arg1.getData()));
                
                if (op.equals("||")) {
                    return new Element(SPO.EXPR, DataType.NUMBER, (t ? wrap(t) : null));
                } else if (op.equals("&&")) {
                    return new Element(SPO.EXPR, DataType.NUMBER, (t ? null : wrap(t)));
                }
            } else return null; /* Illegal RelExpression */
        }
        
        DataType arg1DataType = arg1.getDataType();
        DataType arg2DataType = arg2.getDataType();
        
        if (op.equals("||")) {
            return new Element(SPO.EXPR, DataType.NUMBER, wrap(toBool(arg1.getData()) | toBool(arg2.getData())));
        } else if (op.equals("&&")) {
            return new Element(SPO.EXPR, DataType.NUMBER, wrap(toBool(arg1.getData()) & toBool(arg2.getData())));
        }
        
        else if(arg1.getDataType() != arg2.getDataType()) {
            if (op.equals("=")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(false));
            } else if (op.equals("!=")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(true));
            } else if (op.equals("+")) {
                return new Element(SPO.EXPR, DataType.URL, arg1.getData() + arg2.getData());
            }
        }
        
        else if (arg1DataType == DataType.URL && arg2DataType == DataType.URL) {
            boolean match = arg1.getData().equals(arg2.getData());
            
            if (op.equals("=")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(match));
            } else if (op.equals("!=")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(!match));
            }
        }
        
        else if (arg1DataType == DataType.LITERAL && arg2DataType == DataType.LITERAL) {
            int diff = arg1.getData().compareTo(arg2.getData());
            
            if (op.equals("=")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(diff == 0));
            } else if (op.equals("!=")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(diff != 0));
            } else if (op.equals(">")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(diff > 0));
            } else if (op.equals(">=")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(diff >= 0));
            } else if (op.equals("<")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(diff < 0));
            } else if (op.equals("<=")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(diff <= 0));
            } else if (op.equals("+")) {
                return new Element(SPO.EXPR, DataType.URL, arg1.getData() + arg2.getData());
            }
        }
        
        else if (arg1DataType == DataType.NUMBER && arg2DataType == DataType.NUMBER) {
            double x = toDouble(arg1.getData());
            double y = toDouble(arg2.getData());
            double diff = x - y;
            
            if (op.equals("+")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(x + y));
            } else if (op.equals("-")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(x - y));
            } else if (op.equals("*")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(x * y));
            } else if (op.equals("/")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(x / y));
            } else if (op.equals("=")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(Math.abs(diff) < 1e-10));
            } else if (op.equals("!=")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(Math.abs(diff) > 1e-10));
            } else if (op.equals(">")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(diff > 1e-10));
            } else if (op.equals(">=")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(diff >= 0));
            } else if (op.equals("<")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(diff < -1e-10));
            } else if (op.equals("<=")) {
                return new Element(SPO.EXPR, DataType.NUMBER, wrap(diff <= 0));
            }
        }
        
        return null; /* Illegal RelExpression */
    }

    
    /**
     * Helper Methods to map Doubles and Booleans
     */
    private Double toDouble(String elData) {
        String strippedData = DataType.stripData(elData);

        try {
            return Double.parseDouble(strippedData);
        } catch (NumberFormatException e) {}

        if(strippedData.equals("true")) {
            return 1.0;
        } else if(strippedData.equals("false")) {
            return 0.0;
        }
        
        return null; /* Not a Numeric Element */
    }
    
    private boolean toBool(String elData) {
        Double val = toDouble(elData);
        return (val == null ? !(elData.equals("\"\"")) : toBool(val));
    }
    
    private boolean toBool(Double x) {
        return (Math.abs(x) > 1e-10);
    }

    private String wrap(boolean x) {
        return ((x == true) ? "1" : "0");
    }

    private String wrap(double x) {
        Double val = new Double(x);
        return val.toString();
    }

    @Override
    public String toString() {
        return root.toString();
    }
}