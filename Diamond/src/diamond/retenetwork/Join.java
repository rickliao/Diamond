package diamond.retenetwork;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import diamond.data.Element;
import diamond.data.TripleToken;

/**
 * A <code>Join</code> node will join two <code>Memory</code> nodes together in
 * a <code>ReteNetwork</code>.
 * 
 * @author Rodolfo Kaplan Depena
 * @author Slavcho Slavchev
 */
public abstract class Join extends ReteNode {

    private final Memory leftParent;
    private final Memory rightParent;

    /**
     * Create a <code>Join</code> node
     * 
     * @param leftParent <code>Memory</code>
     * @param rightParent <code>Memory</code>
     */
    public Join(Memory leftParent, Memory rightParent) {
        super();
        Set<String> lpSchema = leftParent.getSchema();
        Set<String> rpSchema = rightParent.getSchema();
        
        // Intersection
        Set<String> intersection = new HashSet<String>();
        for(String s1 : lpSchema) {
        	if(rpSchema.contains(s1)) {
        		intersection.add(s1);
        	}
        }
        
        this.schema.addAll(lpSchema);
        if(!(this instanceof LeftJoin)) this.schema.addAll(rpSchema);
        lpSchema.clear(); lpSchema.addAll(intersection);
        rpSchema.clear(); rpSchema.addAll(intersection);
        
        // set parents
        this.leftParent = leftParent;
        this.rightParent = rightParent;

        // set parent's child
        this.leftParent.addChild(this);
        this.rightParent.addChild(this);
    }

    /**
     * Evaluate a <code>TripleToken</code>
     * 
     * @param tripleToken <code>TripleToken</code>
     * @param caller <code>ReteNode</code>
     * @return <code>true</code>, if <code>TripleToken</code> is successfully
     *         evaluated, otherwise <code>false</code>
     * @throws <code>Exception</code>
     */
    @Override
    public abstract boolean eval(TripleToken tripleToken, ReteNode caller) throws Exception;

    /**
     * Get the left parent of <code>Join</code> node
     * 
     * @return left parent <code>Memory</code>
     */
    public Memory getLeftParent() {
        return leftParent;
    }

    /**
     * Get right parent of a <code>Join</code> node
     * 
     * @return right parent <code>Memory</code>
     */
    public Memory getRightParent() {
        return rightParent;
    }
    
    /**
     * Join 2 TripleTokens
     */
    public TripleToken join(TripleToken t1, TripleToken t2) {
        TripleToken joinedToken = new TripleToken(t1.getTag(), t1);
        joinedToken.addTriples(t2.getBindings());
        joinedToken.addBinds(t2.getBinds());
        joinedToken.getTTTimestamp().union(t2.getTTTimestamp());
        return joinedToken;
    }

    /**
     * Tests whether TripleTokens can be joined
     */
    protected boolean hasConsistentVariableBindings(TripleToken tt1, TripleToken tt2) {
        HashMap<String, Element> binds1 = tt1.getBinds();
        HashMap<String, Element> binds2 = tt2.getBinds();
        
        for (Entry<String, Element> el : binds1.entrySet()) {
            Element val = binds2.get(el.getKey());
            if(val != null && !val.equals(el.getValue())) return false;
        }
        return true;
    }

    @Override
    public abstract String toString();
}