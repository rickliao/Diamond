/**
 * @author Rodolfo Kaplan Depena
 */
package diamond.retenetwork;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import diamond.data.TripleToken;

/**
 * A <code>ReteNode</code> is an abstract class used to define nodes in a
 * <code>ReteNetwork</code>.
 */
public abstract class ReteNode {

    private static int staticId;
    private final int id;
    protected final Set<String> schema;
    private final List<ReteNode> children;
    
    /**
     * Create <code>ReteNode</code>
     */
    public ReteNode() {
        this.id = staticId++;
        this.schema = new HashSet<String>();
        this.children = new ArrayList<ReteNode>();
    }

    public Set<String> getSchema() {
    	return schema;
    }
    
    /**
     * Get children of type <code>ReteNode</code>
     * 
     * @return <code>List<ReteNode></code>
     */
    public List<ReteNode> getChildren() {
        return children;
    }

    /**
     * Get number of children (i.e. - size of list of child rete nodes)
     * 
     * @return <code>int</code>
     */
    public int numberOfChildren() {
        return getChildren().size();
    }

    /**
     * Add child node in <code>ReteNetwork</code>
     * 
     * @param <code>ReteNode</code>
     */
    public void addChild(ReteNode child) {
        if (children.contains(child) == false) {
            children.add(child);
        }
    }

    /**
     * Get particular child, out of list of children <code>ReteNode</code>
     * 
     * @param branchIdx <code>int</code>
     * @return <code>ReteNode</code>
     */
    public ReteNode getChild(int branchIdx) {
        return getChildren().get(branchIdx);
    }

    /**
     * Get id of <code>ReteNode</code>
     * 
     * @return id <code>int</code>
     */
    public int getId() {
        return id;
    }

    /**
     * Evaluate a <code>TripleToken</code>, propagated through the
     * <code>ReteNetwork</code>
     * 
     * @param tripleToken <code>TripleToken</code>
     * @param caller <code>ReteNode</code>
     * @return <code>true</code>, if <code>TripleToken</code> is successfully
     *         evaluated, otherwise <code>false</code>
     * @throws </code>Exception</code>
     */
    public abstract boolean eval(TripleToken tripleToken, ReteNode caller) throws Exception;

    /**
     * Return a <code>String</code> representation of this <code>ReteNode</code>
     * 
     * @return <code>String</code>
     */
    @Override
    public abstract String toString();

    public void clearMemories() {
        for (ReteNode child : getChildren()) {
            child.clearMemories();
        }
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