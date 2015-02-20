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
public class SpinModify extends SpinRule {

    private final List<TriplePattern> insertPatterns;
    private final List<TriplePattern> deletePatterns;
    private int executions, limit;
    private boolean modSoln;

    public SpinModify() {
        super();
        this.insertPatterns = new LinkedList<TriplePattern>();
        this.deletePatterns = new LinkedList<TriplePattern>();
        this.executions = 0;
        this.limit = -1;
        this.modSoln = false;
    }

    public void setInsertPatterns(List<TriplePattern> triplePatterns) {
        this.insertPatterns.addAll(triplePatterns);
    }

    public void setDeletePatterns(List<TriplePattern> triplePatterns) {
        this.deletePatterns.addAll(triplePatterns);
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

        for (TriplePattern tp : insertPatterns) {
            Binding insertBind = new Binding();
            Element subject = tp.getSubject();
            Element predicate = tp.getPredicate();
            Element object = tp.getObject();

            insertBind.setSatisfyingSubject(subject);
            if (subject.getDataType() == DataType.VARIABLE) {
                if(binds.get(subject.getData()) == null)
                    throw new MissingVarValueUponRuleFireException(subject.getData());
                insertBind.setRDFTripleSubject(binds.get(subject.getData()));
            } else {
                insertBind.setRDFTripleSubject(subject);
            }

            insertBind.setSatisfyingPredicate(predicate);
            if (predicate.getDataType() == DataType.VARIABLE) {
                if(binds.get(predicate.getData()) == null)
                    throw new MissingVarValueUponRuleFireException(predicate.getData());
                insertBind.setRDFTriplePredicate(binds.get(predicate.getData()));
            } else {
                insertBind.setRDFTriplePredicate(predicate);
            }

            insertBind.setSatisfyingObject(object);
            if (object.getDataType() == DataType.VARIABLE) {
                if(binds.get(object.getData()) == null)
                    throw new MissingVarValueUponRuleFireException(object.getData());
                insertBind.setRDFTripleObject(binds.get(object.getData()));
            } else {
                insertBind.setRDFTripleObject(object);
            }

            TripleToken newTT = new TripleToken(TokenTag.PLUS, Timestamp.nextTimestamp());
            newTT.addTriple(insertBind);
            consequentData.add(newTT);
        }

        for (TriplePattern tp : deletePatterns) {
            Binding deleteBind = new Binding();
            Element subject = tp.getSubject();
            Element predicate = tp.getPredicate();
            Element object = tp.getObject();

            deleteBind.setSatisfyingSubject(subject);
            if (subject.getDataType() == DataType.VARIABLE) {
                if(binds.get(subject.getData()) == null)
                    throw new MissingVarValueUponRuleFireException(subject.getData());
                deleteBind.setRDFTripleSubject(binds.get(subject.getData()));
            } else {
                deleteBind.setRDFTripleSubject(subject);
            }

            deleteBind.setSatisfyingPredicate(predicate);
            if (predicate.getDataType() == DataType.VARIABLE) {
                if(binds.get(predicate.getData()) == null)
                    throw new MissingVarValueUponRuleFireException(predicate.getData());
                deleteBind.setRDFTriplePredicate(binds.get(predicate.getData()));
            } else {
                deleteBind.setRDFTriplePredicate(predicate);
            }

            deleteBind.setSatisfyingObject(object);
            if (object.getDataType() == DataType.VARIABLE) {
                if(binds.get(object.getData()) == null)
                    throw new MissingVarValueUponRuleFireException(object.getData());
                deleteBind.setRDFTripleObject(binds.get(object.getData()));
            } else {
                deleteBind.setRDFTripleObject(object);
            }

            TripleToken newTT = new TripleToken(TokenTag.MINUS, Timestamp.nextTimestamp());
            newTT.addTriple(deleteBind);
            consequentData.add(newTT);
        }

        executions++;
        return consequentData;
    }

    @Override
    public String toString() {
        return "SpinModify[" + getId() + "]";
    }
}
