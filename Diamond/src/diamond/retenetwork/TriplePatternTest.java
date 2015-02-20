package diamond.retenetwork;

import diamond.data.DataType;
import diamond.data.Binding;
import diamond.data.Element;
import diamond.data.RDFTriple;
import diamond.data.TriplePattern;
import diamond.data.TripleToken;

import java.util.HashMap;

/**
 * A <code>ReteNode</code> that tests if a <code>TripleToken</code> matches a
 * given <code>TriplePattern</code>
 * 
 * @author Slavcho Slavchev
 */
public class TriplePatternTest extends ReteNode {

    private final TriplePattern testPattern;

    /**
     * Creates a <code>TriplePatternTest</code> node to test incoming objects of
     * type <code>TripleToken</code>
     * 
     * @param triplePattern <code>TriplePattern</code>
     */
    public TriplePatternTest(TriplePattern testPattern) {
        super();
        if(testPattern.getSubject().getDataType() == DataType.VARIABLE) {
        	this.schema.add(testPattern.getSubject().getData());
        }
        if(testPattern.getPredicate().getDataType() == DataType.VARIABLE) {
        	this.schema.add(testPattern.getPredicate().getData());
        }
        if(testPattern.getObject().getDataType() == DataType.VARIABLE) {
        	this.schema.add(testPattern.getObject().getData());
        }
        
        this.testPattern = testPattern;
    }

    /**
     * Evaluates a <code>TripleToken</code> to see if the corresponding
     * <code>TriplePattern</code> matches
     * 
     * @param tripleToken <code>TripleToken</code>
     * @param caller <code>ReteNode</code>
     * @return <code>true</code>, if <code>TripleToken</code> is successfully
     *         evaluated, otherwise <code>false</code>
     * @throws <code>Exception</code>
     */
    @Override
    public boolean eval(TripleToken tripleToken, ReteNode caller) throws Exception {
        boolean isEvaluated = false;

        for (Binding binding : tripleToken.getBindings()) {
            RDFTriple rdf = binding.getRdfTriple();
            TriplePattern testPattern = getTestPattern();

            HashMap<String, Element> varMatches = new HashMap<String, Element>();
            boolean subjMatch = false;
            boolean objMatch = false;
            boolean predMatch = false;

            Element testSubj = testPattern.getSubject();
            if (testSubj.getDataType() == DataType.VARIABLE) {
                varMatches.put(testSubj.getData(), rdf.getSubject());
                binding.setSatisfyingSubject(testSubj);
                subjMatch = true;
            } else if (testSubj.getDataType() == DataType.URL) {
                if (rdf.getSubject().getData().equals(testSubj.getData())) {
                    binding.setSatisfyingSubject(testSubj);
                    subjMatch = true;
                }
            }

            Element testPred = testPattern.getPredicate();
            if (testPred.getDataType() == DataType.VARIABLE) {
                Element match = varMatches.get(testPred.getData());
                if (match == null) {
                    varMatches.put(testPred.getData(), rdf.getPredicate());
                    binding.setSatisfyingPredicate(testPred);
                    predMatch = true;
                } else if (match.equals(rdf.getPredicate())) {
                    binding.setSatisfyingPredicate(testPred);
                    predMatch = true;
                }
            } else if (testPred.getDataType() == DataType.URL) {
                if (rdf.getPredicate().getData().equals(testPred.getData())) {
                    binding.setSatisfyingPredicate(testPred);
                    predMatch = true;
                }
            }

            Element testObj = testPattern.getObject();
            if (testObj.getDataType() == DataType.VARIABLE) {
                Element match = varMatches.get(testObj.getData());
                if (match == null) {
                    varMatches.put(testObj.getData(), rdf.getObject());
                    binding.setSatisfyingObject(testObj);
                    objMatch = true;
                } else if (match.equals(rdf.getObject())) {
                    binding.setSatisfyingObject(testObj);
                    objMatch = true;
                }
            } else if (testObj.getDataType() != DataType.VARIABLE) {
                if (rdf.getObject().getData().equals(testObj.getData())) {
                    binding.setSatisfyingObject(testObj);
                    objMatch = true;
                }
            }

            if (subjMatch && predMatch && objMatch) {
                TripleToken newToken = new TripleToken(tripleToken.getTag(), tripleToken.getTTTimestamp());
                newToken.addTriple(binding);
                newToken.addBinds(varMatches);
                
                for (ReteNode child : getChildren()) {
                    isEvaluated |= child.eval(newToken, this);
                }
                isEvaluated = true;
            }
        }

        return isEvaluated;
    }

    /**
     * Returns a <code>TriplePattern</code> the test is based on
     * 
     * @return <code>TriplePattern</code>
     */
    public TriplePattern getTestPattern() {
        return testPattern;
    }

    /**
     * Returns a <code>String</code> representation of this
     * <code>TriplePatternTest</code> node
     * 
     * @return <code>String</code>
     */
    @Override
    public String toString() {
        return testPattern.toString();
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