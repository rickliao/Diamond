package diamond.spin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import diamond.data.Binding;
import diamond.data.DataType;
import diamond.data.RDFTriple;
import diamond.data.TokenTag;
import diamond.data.TripleToken;

public class WorkingMemory {

    private final Set<RDFTriple> data;

    public WorkingMemory() {
        data = new HashSet<RDFTriple>();
    }

    public Set<RDFTriple> getData() {
        return data;
    }

    public void accept(TripleToken token) {
        TokenTag tag = token.getTag();
        for (Binding binding : token.getBindings()) {
            if (tag == TokenTag.PLUS) {
                data.add(binding.getRdfTriple());
            } else {
                data.remove(binding.getRdfTriple());
            }
        }
    }

    @Override
    public String toString() {
        int len = data.size(), idx=0;
        RDFTriple[] sorted = new RDFTriple[len];
        for(RDFTriple rdf : data) sorted[idx++] = rdf;
        Arrays.sort(sorted);
        
        String tmp;
        StringBuilder result = new StringBuilder("");
        for (RDFTriple rdf : sorted) {
            tmp = rdf.getSubject().toString();
            if(rdf.getSubject().getDataType() == DataType.URL)
                tmp = "<" + rdf.getSubject() + ">";
            result.append(tmp).append(" ");
            
            tmp = rdf.getPredicate().toString();
            if(rdf.getPredicate().getDataType() == DataType.URL)
                tmp = "<" + rdf.getPredicate() + ">";
            result.append(tmp).append(" ");
            
            tmp = rdf.getObject().toString();
            if(rdf.getObject().getDataType() == DataType.URL)
                tmp = "<" + rdf.getObject() + ">";
            result.append(tmp).append(" .\n");
            
            //result.append(rdf).append("\n");
        }
        int lastIdx = result.length() - 1;
        if (lastIdx > -1) {
            result.deleteCharAt(lastIdx);
        }
        return result.toString();
    }
}
