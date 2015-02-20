package diamond.builtincall;

import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.ExprNode;
import diamond.data.SPO;

/**
 * Check if the languages of 2 elements match.
 * @author Slavcho Slavchev
 */
public class LangMatches extends BinaryBuiltInCall {

    public LangMatches(ExprNode lhs, ExprNode rhs) {
        super(SPO.EXPR, DataType.EXPR_OP, "LangMatches", lhs, rhs);
    }

    @Override
    public Element eval(Element lhs, Element rhs) {
        String lang1 = extractLang(lhs.getData()).toLowerCase().replace("\"", "");
        String lang2 = extractLang(rhs.getData()).toLowerCase().replace("\"", "");
        boolean sameLang = false;
        
        if(lang2.equals("*")) {
            sameLang = !lang1.equals(""); 
        } else {
            sameLang = lang1.contains(lang2);
        }
        return new Element(SPO.EXPR, DataType.NUMBER, sameLang ? "1" : "0");
    }
    
    private String extractLang(String data) {
        String lang = data;
        int idx1 = data.lastIndexOf('@');
        if(idx1 != -1) {
            String substr = data.substring(idx1+1, data.length());
            if(substr != null && substr.matches("[a-zA-Z]+")) {
                lang = '\"' + substr + '\"';
            }
        }
        return lang;
    }
    
    @Override
    public String toString() {
        return "LangMatches(" + leftChild() + ", " + rightChild() + ")";
    }
}