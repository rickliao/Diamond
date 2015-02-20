package diamond.spin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import diamond.parser.syntaxtree.LimitClause;
import diamond.data.Binding;
import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.Timestamp;
import diamond.data.TokenTag;
import diamond.data.TriplePattern;
import diamond.data.TripleToken;
import diamond.exception.MissingVarValueUponRuleFireException;

/**
 * @author Slavcho Slavchev
 */
public class SpinConstruct extends SpinRule {

    private final List<TriplePattern> constructPatterns;
    private int executions, limit;
    private boolean modSoln;

    public SpinConstruct() {
        super();
        this.constructPatterns = new LinkedList<TriplePattern>();
        this.executions = 0;
        this.limit = -1;
        this.modSoln = false;
    }

    public void setConstructPatterns(List<TriplePattern> triplePatterns) {
        constructPatterns.addAll(triplePatterns);
    }

    public void setMods(ArrayList<LimitClause> mod) {
        modSoln = !mod.isEmpty();
        if (modSoln)
            limit = Integer.parseInt(mod.get(0).nodeToken1.tokenImage);
    }

    @Override
    public List<TripleToken> evalRule(TripleToken tripleToken) throws Exception {
        // Stores the new TripleTokens
        List<TripleToken> consequentData = new LinkedList<TripleToken>();

        if (modSoln && executions >= limit) {
            return consequentData;
        }

        /* Extract all variable bindings from the triple token */
        Map<String, Element> binds = tripleToken.getBinds();

        for (TriplePattern tp : constructPatterns) {
            Binding constructBind = new Binding();
            Element subject = tp.getSubject();
            Element predicate = tp.getPredicate();
            Element object = tp.getObject();

            constructBind.setSatisfyingSubject(subject);
            if (subject.getDataType() == DataType.VARIABLE) {
                if(binds.get(subject.getData()) == null)
                    throw new MissingVarValueUponRuleFireException(subject.getData());
                constructBind.setRDFTripleSubject(binds.get(subject.getData()));
            } else {
                constructBind.setRDFTripleSubject(subject);
            }

            constructBind.setSatisfyingPredicate(predicate);
            if (predicate.getDataType() == DataType.VARIABLE) {
                if(binds.get(predicate.getData()) == null)
                    throw new MissingVarValueUponRuleFireException(predicate.getData());
                constructBind.setRDFTriplePredicate(binds.get(predicate.getData()));
            } else {
                constructBind.setRDFTriplePredicate(predicate);
            }

            constructBind.setSatisfyingObject(object);
            if (object.getDataType() == DataType.VARIABLE) {
                if(binds.get(object.getData()) == null)
                    throw new MissingVarValueUponRuleFireException(object.getData());
                constructBind.setRDFTripleObject(binds.get(object.getData()));
            } else {
                constructBind.setRDFTripleObject(object);
            }
            
            TripleToken newTT = new TripleToken(TokenTag.PLUS, Timestamp.nextTimestamp());
            newTT.addTriple(constructBind);
            consequentData.add(newTT);
        }

        executions++;
        return consequentData;
    }

    @Override
    public String toString() {
        return "SpinConstruct[" + getId() + "]";
    }
}
