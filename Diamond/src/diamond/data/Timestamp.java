package diamond.data;

import java.util.Iterator;
import java.util.LinkedList;

public class Timestamp implements Comparable<Timestamp> {

    // List of integers sorted in non-increasing order
    private final LinkedList<Integer> stamp;
    private static int nextTimestamp = 1;
    
    private Timestamp() {
        this.stamp = new LinkedList<Integer>();
    }
    
    public static Timestamp nextTimestamp() {
        Timestamp timestamp = new Timestamp();
        timestamp.stamp.add(nextTimestamp++);
        return timestamp;
    }
    
    @Override
    public Timestamp clone() {
        Timestamp newTimestamp = new Timestamp();
        for(Integer i : this.stamp) newTimestamp.addExclSorted(i);
        return newTimestamp;
    }
    
    // Union operation
    public void union(Timestamp t) {
        for(Integer i : t.stamp) this.addExclSorted(i);
    }
    
    // Insert in decreasing sorted order (eliminating duplicates)
    private void addExclSorted(Integer x) {
        int idx = 0;
        for(Integer i : stamp) {
            if(i == x) {
                return; // no duplicates
            } else if(i < x) {
                stamp.add(idx, x); return; // decreasing order
            }
            ++idx;
        } stamp.add(idx, x); // insert at the end
    }
    
    @Override
    public int compareTo(Timestamp t) {
        Iterator<Integer> it1 = stamp.iterator();
        Iterator<Integer> it2 = t.stamp.iterator();
        while(it1.hasNext() && it2.hasNext()) {
            Integer i1 = it1.next(), i2 = it2.next();
            if(i1 > i2) return 1;
            else if(i1 < i2) return -1;
        }
        if(it1.hasNext()) return 1;
        else if(it2.hasNext()) return -1;
        else return 0;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Integer i : stamp) sb.append(i).append(",");
        int size = sb.length();
        if(size > 0) sb.deleteCharAt(size-1);
        return sb.toString();
    }
}
