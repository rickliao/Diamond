/**
 * @author Rodolfo Kaplan Depena
 */
package diamond.retenetwork;

import diamond.data.DataType;
import diamond.data.TriplePattern;

/**
 * <code>AlphaMemory</code> is a <code>ReteNode</code> that is always a child of
 * a <code>TriplePatternTest</code> node in a <code>ReteNetwork</code>.
 */
public class AlphaMemory extends Memory {

    private TriplePatternTest parent;
    private boolean needCartesianProduct;

    /**
     * Create <code>AlphaMemory</code>
     * 
     * @param parent <code>TriplePatternTest</code> node
     */
    public AlphaMemory(TriplePatternTest parent) {
        super(parent);
        this.parent = parent;
        needCartesianProduct = shouldCartesianProductBeRequestedUponJoin();
    }

    /**
     * Request a <code>CartesianProduct</code> when
     * <code>TriplePatternTest</code> parent contains no variables.
     * 
     * @return true if a <code>CartesianProduct</code> should be used, otherwise false
     */
    private boolean shouldCartesianProductBeRequestedUponJoin() {
        // get pattern test
        TriplePattern patternTest = parent.getTestPattern();

        // data types
        DataType dataTypeOfPatternTestSubject = patternTest.getSubject().getDataType();
        DataType dataTypeOfPatternTestPredicate = patternTest.getPredicate().getDataType();
        DataType dataTypeOfPatternTestObject = patternTest.getObject().getDataType();

        // test that patternTest has no variables
        return dataTypeOfPatternTestSubject != DataType.VARIABLE && dataTypeOfPatternTestPredicate != DataType.VARIABLE
                && dataTypeOfPatternTestObject != DataType.VARIABLE;
    }

    /**
     * Method that returns true if a <code>CartesianProduct</code> should be
     * child of this <code>Memory</code>, otherwise false
     * 
     * @return <code>boolean</code>
     */
    public boolean isCartesianProductNecessary() {
        return needCartesianProduct;
    }

    /**
     * Returns a <code>String</code> representation of this
     * <code>AlphaMemory</code>
     * 
     * @return <code>String</code>
     */
    @Override
    public String toString() {
        return "AlphaMemory[" + getId() + "]";
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