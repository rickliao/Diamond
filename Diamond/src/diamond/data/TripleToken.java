package diamond.data;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * An ordered tuple that carries a tag and graphs that represent rdf data.
 * @author Rodolfo Kaplan Depena
 * @author Slavcho Slavchev
 */
public class TripleToken {

    private static int tripleTokenId = 0;
    
    private final int id;
    private LinkedList<Binding> bindings;
    private final TokenTag tag;
    private final Timestamp timestamp;
    private int joinCounter;
    public URI urlWhereTripleTokenCameFrom;
    
    // Store all variable binds
    private final HashMap<String, Element> varBinds;
    
    /**
     * Create <code>TripleToken</code>
     */
    public TripleToken(TokenTag tag, Timestamp ts) {
        this.id = tripleTokenId++;
        bindings = new LinkedList<Binding>();
        urlWhereTripleTokenCameFrom = null;
        this.tag = tag;
        this.timestamp = ts.clone();
        this.varBinds = new HashMap<String, Element>();
    }

    /**
     * Create <code>TripleToken</code>
     * 
     * @param tripleToken <code>TripleToken</code>
     */
    public TripleToken(TokenTag tag, TripleToken tripleToken) {
        this(tag, tripleToken.getTTTimestamp());
        this.bindings.addAll(tripleToken.getBindings());
        this.varBinds.putAll(tripleToken.getBinds());
    }
    
    public int getId() {
        return id;
    }

    /**
     * Create <code>TripleToken</code>
     * 
     * @param tag <code>TokenTag</code>
     * @param bindings <code>Graphs</code>
     */
    public Timestamp getTTTimestamp() {
        return this.timestamp;
    }
    
    /**
     * Add a triple in the form of a <code>Binding</code>
     * 
     * @param binding <code>Binding</code>
     */
    public void addTriple(Binding binding) {
        Binding bind = new Binding(binding);
        bindings.add(bind);
    }

    /**
     * Add triples
     * 
     * @param graphs <code>Graphs</code>
     */
    public void addTriples(Collection<Binding> graphs) {
        this.bindings.addAll(graphs);
    }
    
    public void addBinds(HashMap<String, Element> binds) {
        this.varBinds.putAll(binds);
    }

    /**
     * Get <code>Graphs</code>
     * 
     * @return <code>Graphs</code>
     */
    public LinkedList<Binding> getBindings() {
        return bindings;
    }

    /**
     * Return <code>TokenTag</code>
     * 
     * @return <code>TokenTag</code>
     */
    public TokenTag getTag() {
        return tag;
    }

    /**
     * JoinConter used in handling 'Optional'
     */
    public int getJoinCounter() {
        return joinCounter;
    }

    public void incrementJoinCounter() {
        ++joinCounter;
    }

    public void decrementJoinCounter() {
        --joinCounter;
    }

    /**
     * Variable binds.
     */
    public HashMap<String, Element> getBinds() {
        return varBinds;
    }

    /**
     * Attempts to find out whether or not another object will equal the calling
     * <code>TripleToken</code>
     * 
     * @param o <code>Object</code>
     * @return <code>true</code> if two objects equal, otherwise
     *         <code>false</code>
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof TripleToken) {
            TripleToken that = (TripleToken) o;
            return this.bindings.equals(that.bindings);
        }
        return false;
    }
    
    public int getHashVal(Set<String> schema) {
    	int hash = 0;
    	for(String el : schema) {
    		Element val = varBinds.get(el);
    		hash += el.hashCode() * (val != null ? val.hashCode() : 0); 
    	}
    	return hash;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
    	for(Entry<String, Element> el : varBinds.entrySet()) {
    		hash += el.getKey().hashCode() * el.getValue().getData().hashCode(); 
    	}
        return hash;
    }

    /**
     * Return <code>String</code> representation of a <code>TripleToken</code>
     * 
     * @return <code>String</code>
     */
    @Override
    public String toString() {
        return "(" + tag + ", [" + timestamp + "], " + bindings + ", " + varBinds + ")";
    }
    
    public String toString2() {
        PriorityQueue<Binding> sorted = new PriorityQueue<Binding>(); 
        StringBuilder sb = new StringBuilder();
        sb.append(tag).append(", [").append(timestamp).append("]\n");
        for(Binding binding : bindings) {
            sorted.add(binding);
        }
        int size = sorted.size();
        for(int i = 0; i < size; ++i) {
            sb.append(sorted.poll().getRdfTriple()).append("\n");
        }
        sb.append(varBinds).append("\n").append("\n");
        return sb.toString();
    }
}
/*
 * Copyright (c) 2010, Rodolfo Kaplan Depena All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions and use of source code, binary forms, and documentation
 * are for personal and educational use only. 2. Redistributions and use of
 * source code, binary forms, and documentation must not be used for monetary
 * gain and/or for business purposes (PROFIT AND NON-PROFIT) of any sort without
 * the express written permission of Rodolfo Kaplan Depena. 3. Redistributions
 * of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. 4. Redistributions in binary form
 * must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided
 * with the distribution. 5. All advertising materials mentioning features or
 * use of this software must display the following acknowledgment: This product
 * includes software developed by Rodolfo Kaplan Depena. Any use of this
 * software for monetary gain (or business purposes) of any sort without the
 * express written consent of Rodolfo Kaplan Depena is not allowed. 6. Neither
 * the name of the Rodolfo Kaplan Depena nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY RODOLFO KAPLAN DEPENA (AND CONTRIBUTORS) ''AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL RODOLFO KAPLAN DEPENA (AND
 * CONTRIBUTORS) BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */