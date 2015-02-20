package diamond.retenetwork;

import java.util.HashMap;
import java.util.LinkedList;

import diamond.data.TokenTag;
import diamond.data.TripleToken;

/**
 * <code>LeftJoin</code> joins to <code>Memory</code> objects by implementing
 * the <code>LeftJoin</code> algorithm.
 * 
 * @author Rodolfo Kaplan Depena
 * @author Slavcho Slavchev
 */
public class LeftJoin extends Join {

    /**
     * Create <code>LeftJoin</code>
     * 
     * @param leftParent <code>Memory</code>
     * @param rightParent <code>Memory</code>
     */
    public LeftJoin(Memory leftParent, Memory rightParent) {
        super(leftParent, rightParent);
    }

    /**
     * Evaluate a single <code>TripleToken</code>.
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
        boolean isEvaluated = false;
        
        if (leftCall) { // Caller is the Left parent
            Memory joinMemory = getRightParent();
            boolean tokenIsJoined = false;

            Integer hashVal = tripleToken.getHashVal(joinMemory.getSchema());
            HashMap<Integer, LinkedList<TripleToken>> joinable = joinMemory.getHashFilter().get(hashVal);
            
            if(joinable != null)
            for(LinkedList<TripleToken> list : joinable.values())
            for (TripleToken tokenFromMem : list) {
                if (hasConsistentVariableBindings(tripleToken, tokenFromMem)) {

                    TripleToken joinedToken = join(tripleToken, tokenFromMem);
                    tripleToken.incrementJoinCounter();
                    tokenIsJoined = true;

                    for (ReteNode child : getChildren()) {
                        isEvaluated |= child.eval(joinedToken, this);
                    }
                }
            }
            if (!tokenIsJoined) {
                TripleToken newToken = new TripleToken(tripleToken.getTag(), tripleToken);

                for (ReteNode child : getChildren()) {
                    isEvaluated |= child.eval(newToken, this);
                }
            }
        } else { // Caller is the Right parent
            Memory joinMemory = getLeftParent();

            Integer hashVal = tripleToken.getHashVal(joinMemory.getSchema());
            HashMap<Integer, LinkedList<TripleToken>> joinable = joinMemory.getHashFilter().get(hashVal);
            
            if(joinable != null)
            for(LinkedList<TripleToken> list : joinable.values())
            for (TripleToken tokenFromMem : list) {
                if (hasConsistentVariableBindings(tripleToken, tokenFromMem)) {

                    TripleToken joinedToken = join(tripleToken, tokenFromMem);

                    for (ReteNode child : getChildren()) {
                        isEvaluated |= child.eval(joinedToken, this);
                    }
                    
                    TripleToken negatedToken = null;
                    if (tripleToken.getTag() == TokenTag.PLUS) {
                        if (tokenFromMem.getJoinCounter() == 0) {
                            negatedToken = new TripleToken(TokenTag.MINUS, tokenFromMem);
                        }
                        tokenFromMem.incrementJoinCounter();
                    } else if (tripleToken.getTag() == TokenTag.MINUS) {
                        if (tokenFromMem.getJoinCounter() == 1) {
                            negatedToken = new TripleToken(TokenTag.PLUS, tokenFromMem);
                        }
                        tokenFromMem.decrementJoinCounter();
                    }

                    if (negatedToken != null) {
                        for (ReteNode child : getChildren()) {
                            isEvaluated |= child.eval(negatedToken, this);
                        }
                    }
                }
            }
        }
        
        return isEvaluated;
    }

    /**
     * A <code>String</code> representation of <code>LeftJoin</code>.
     * 
     * @return <code>String</code>
     */
    @Override
    public String toString() {
        return "LeftJoin[" + getId() + "]";
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