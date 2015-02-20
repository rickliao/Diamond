package diamond.retenetwork;

import diamond.data.TripleToken;

/**
 * A <code>CartesianProduct</code> that will join two objects of type
 * <code>Memory</code> together
 * 
 * @author Rodolfo Kaplan Depena
 * @author Slavcho Slavchev
 */
public class CartesianProduct extends Join {

    /**
     * Create a <code>CartesianProduct</code>.
     * 
     * @param leftParent <code>Memory</code>
     * @param rightParent <code>Memory</code>
     */
    public CartesianProduct(Memory leftParent, Memory rightParent) {
        super(leftParent, rightParent);
    }

    /**
     * Evaluate a <code>TripleToken</code> by joining this token with the
     * <code>Memory</code> of other tokens.
     * 
     * @param tripleToken <code>TripleToken</code>
     * @param caller <code>ReteNode</code>
     * @return <code>true</code>, if <code>TripleToken</code> is successfully
     *         evaluated, otherwise <code>false</code>
     * @throws <code>Exception</code>
     */
    @Override
    public boolean eval(TripleToken tripleToken, ReteNode caller) throws Exception {
        boolean leftCall = (caller.getId() == getLeftParent().getId());
        Memory joinMemory = leftCall ? getRightParent() : getLeftParent();
        boolean isEvaluated = false;

        for (TripleToken tokenFromMem : joinMemory.getMemory()) {

            TripleToken joinedToken = join(tripleToken, tokenFromMem);

            for (ReteNode child : getChildren()) {
                isEvaluated |= child.eval(joinedToken, this);
            }
        }
        return isEvaluated;
    }

    /**
     * Return a <code>String</code> representation of Cartesian product.
     * 
     * @return <code>String</code> representation of <code>Union</code>
     */
    @Override
    public String toString() {
        return "CartesianProduct[" + getId() + "]";
    }
}