package diamond.spin;

import diamond.data.Timestamp;
import diamond.data.TripleToken;

/**
 * <code>ConflictSet</code> element that corresponds to a time-stamped 
 * (Production rule, List of elements matched by its LHS) pair
 */
public class ConflictSetUnit implements Comparable<ConflictSetUnit> {

    private final Timestamp timestamp;
    private final SpinRule production;
    private final TripleToken instantiation;

    public ConflictSetUnit(SpinRule parentRule, TripleToken TT) {
        this.timestamp = TT.getTTTimestamp();
        this.production = parentRule;
        this.instantiation = TT;
    }

    public Timestamp getTimeStamp() {
        return this.timestamp;
    }
    
    public SpinRule getRuleToFire() {
        return this.production;
    }

    public TripleToken getInstantiation() {
        return this.instantiation;
    }

    @Override
    public String toString() {
        return production.toString() + ": " + instantiation.toString2();
    }

    @Override
    public int compareTo(ConflictSetUnit csu) {
        return this.timestamp.compareTo(csu.timestamp) * -1;
    }
}
