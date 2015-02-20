package diamond.spin;

import java.util.List;

import diamond.data.TripleToken;

/**
 * @author Slavcho Slavchev
 */
public abstract class SpinRule {

    private static int spinRuleId = 0;
    private int id;

    public SpinRule() {
        this.id = spinRuleId++;
    }

    public abstract List<TripleToken> evalRule(TripleToken tripleToken) throws Exception;

    public int getId() {
        return id;
    }
}
