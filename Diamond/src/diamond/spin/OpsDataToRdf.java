package diamond.spin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;

public class OpsDataToRdf {

    public static void main(String[] args) throws IOException {
        if(args.length != 1) {
            System.out.println("Specify datafile");
            System.exit(0);
        }
        
        String file = args[0], next = null;
        BufferedReader br = new BufferedReader(new FileReader(file));
        TreeSet<Triple> triples = new TreeSet<Triple>();
        
        while((next = br.readLine()) != null) {
            next = next.substring(6, next.length()-1);
            next = next.replaceAll("\\^", "");
            //System.out.print(next); // debug
            String[] tkns = next.split(" ");
            if(tkns.length == 0) continue;
            
            if(tkns.length == 2) { // variable
                triples.add(new Triple("ex:" + tkns[0], "ex:value", tkns[1]));
            } else { // structure
                int size = tkns.length;
                String idUrl = "ex:url" + tkns[2];
                triples.add(new Triple(idUrl, "ex:type", "ex:" + tkns[0]));
                for(int i = 1; i < size; i+=2) {
                    triples.add(new Triple(idUrl, "ex:" + tkns[i], tkns[i+1]));
                }
            }
        }
        br.close();
        
        int idx = 0;
        String[] x = new String[triples.size()];
        for(Triple t : triples) x[idx++] = t.toString();
        Arrays.sort(x);
        System.out.println("@prefix ex: <http://www.example.org/> .\n");
        for(String t : x) System.out.println(t);
    }
}

class Triple implements Comparable<Triple> {
    
    private final String subject;
    private final String predicate;
    private final String object;
    
    public Triple(String subj, String pred, String obj) {
        this.subject = subj;
        this.predicate = pred;
        if(!obj.contains("ex:")) try {
            Double.parseDouble(obj);
        } catch(Exception e) {
            obj = '\"' + obj + '\"';
        }
        this.object = obj;
    }
    
    @Override
    public int compareTo(Triple t) {
        if(!subject.equals(t.subject)) return subject.compareTo(t.subject);
        else if(!predicate.equals(t.predicate)) return predicate.compareTo(t.predicate);
        else if(!object.equals(t.object)) return object.compareTo(t.object);
        else return 0;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(subject).append(" ");
        sb.append(predicate).append(" ");
        sb.append(object).append(" .");
        return sb.toString();
    }
}
