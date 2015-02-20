package diamond.retenetwork;

/**
 * A <code>BetaMemory</code> to be used to capture results from a
 * <code>Join</code> node in a <code>ReteNetwork</code>.
 * 
 * @author Rodolfo Kaplan Depena
 */
public class BetaMemory extends Memory {

    public BetaMemory(ReteNode parent) {
        super(parent);
    }

    /**
     * Return a <code>String</code> representation of the
     * <code>BetaMemory</code>
     * 
     * @return <code>String</code>
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("BetaMemory[");
        sb.append(getId()).append("]: ");
        for(String el : schema) {
            sb.append(el).append(" ");
        }
        return sb.toString();
    }
}