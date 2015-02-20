/**
 * @author Rodolfo Kaplan Depena
 * A re-creation of the Stopwatch.java program publicly available at 
 * http://www.cs.utexas.edu/~scottm/cs307/javacode/utilities/Stopwatch.java
 * Many thanks to Mike Scott for making this simple program available for use.
 */
package diamond.data;

public class Timer {

    // instance variables
    private long start;
    private long stop;
    private boolean active;

    public static final double NANOSECONDS_PER_SECONDS = 1000000000.0;

    public Timer() {
        active = false;
    }

    /**
     * Return status (active or not).
     * 
     * @return <code>boolean</code>
     */
    public boolean getStatus() {
        return active;
    }

    /**
     * Starts the timer
     */
    public void start() {
        active = true;
        start = System.nanoTime();
    }

    /**
     * Stops the timer
     */
    public void stop() {
        active = false;
        stop = System.nanoTime();
    }

    /**
     * Time represented in seconds
     * 
     * @return the time recorded on the stop watch in seconds
     */
    public double timeInSeconds() {
        if (active == false) {
            return (stop - start) / NANOSECONDS_PER_SECONDS;
        } else {
            return -1.0;
        }
    }

    /**
     * String Representation of time in Seconds
     * 
     * @return <code>String</code>
     */
    @Override
    public String toString() {
        return "elapsed time: " + timeInSeconds() + " seconds.";
    }

    /**
     * Time elapsed in nanoseconds.
     * 
     * @return <code>long</code>
     */
    public long timeInNanoseconds() {
        if (active == false) {
            return (stop - start);
        } else {
            return -1;
        }
    }
}
