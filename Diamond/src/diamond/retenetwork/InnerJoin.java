package diamond.retenetwork;

import java.util.HashMap;
import java.util.LinkedList;

import diamond.data.TripleToken;

/**
 * A <code>Join</code> node that implements a <code>InnerJoin</code> algorithm.
 * Will join two <code>Memory</code> nodes' tokens together
 * 
 * @author Rodolfo Kaplan Depena
 * @author Slavcho Slavchev
 */
public class InnerJoin extends Join {
	
    /**
     * Create a <code>InnerJoin</code> Node
     * 
     * @param leftParent <code>Memory</code>
     * @param rightParent <code>Memory</code>
     */
    public InnerJoin(Memory leftParent, Memory rightParent) {
        super(leftParent, rightParent);
    }

    /**
     * Evaluate a <code>TripleToken</code> and will attempt to <code>Join</code>
     * with a <code>Set<TripleToken></code>.
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
        
        Integer hashVal = tripleToken.getHashVal(joinMemory.getSchema());
        HashMap<Integer, LinkedList<TripleToken>> joinable = joinMemory.getHashFilter().get(hashVal);
        if(joinable == null) return isEvaluated;
        
        for(LinkedList<TripleToken> list : joinable.values()) {
            for (TripleToken tokenFromMem : list) {
        	    if (hasConsistentVariableBindings(tripleToken, tokenFromMem)) {
        	        TripleToken joinedToken = join(tripleToken, tokenFromMem);
        		    
                    for (ReteNode child : getChildren()) {
                        isEvaluated |= child.eval(joinedToken, this);
                    }
        	    }
            }
        }
        
        return isEvaluated;
    }

    /**
     * Return a <code>String</code> representation of a <code>InnerJoin</code>
     * 
     * @return <code>String</code>
     */
    @Override
    public String toString() {
        return "InnerJoin[" + getId() + "]";
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