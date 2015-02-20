/**
 * @author Rodolfo Kaplan Depena
 */
package diamond.data;

/**
 * Like an RDFTriple, but may contain variables in place of subject, predicate,
 * or object.
 */
public class TriplePattern extends RDFTriple {
    
    /**
     * Create TriplePattern
     */
    public TriplePattern() {
        super();
    }

    /**
     * Create TriplePattern
     * 
     * @param tp
     */
    public TriplePattern(TriplePattern tp) {
        this.setSubject(tp.getSubject());
        this.setPredicate(tp.getPredicate());
        this.setObject(tp.getObject());
    }

    /**
     * Set Subject
     * 
     * @param subject
     */
    @Override
    public void setSubject(Element subject) {
        this.subject = subject;
    }

    /**
     * Set Predicate
     * 
     * @param predicate
     */
    @Override
    public void setPredicate(Element predicate) {
        this.predicate = predicate;
    }

    /**
     * Set Object
     * 
     * @param object
     */
    @Override
    public void setObject(Element object) {
        this.object = object;
    }

    /**
     * Compare this <code>TriplePattern</code> with that
     * <code>TriplePattern</code>
     * 
     * @return -1 if this is less than that, 0 if this is equal to that, 1 if
     *         that is greater than that
     */
    /*@Override
    public int compareTo(Object o) {
        // if the object o is not an instance of triple pattern
        if (o instanceof TriplePattern == false) {
            // then throw an exception
            throw new IllegalArgumentException("Not a Triple Pattern.");
        }

        // Since the object o is a triple pattern, we will cast it as such
        TriplePattern that = (TriplePattern) o;

        // if this scope is higher in rank than that scope
        if (this.getScope().getScopeId() < that.getScope().getScopeId()) {
            return 1;// then return 1
        }

        // if this scope is equal to that scope
        if (this.getScope().getScopeId() == that.getScope().getScopeId()) {
            return 0;// then return 0
        }

        // if this scope is lower in rank than that scope
        if (this.getScope().getScopeId() > that.getScope().getScopeId()) {
            return -1;// then return -1
        }

        throw new IllegalStateException("Triple Pattern should always have a scope and "
                + "a triple pattern in the compareTo() method does not");
    }

    /**
     * Method that indicates whether or not another object equals the calling
     * <code>TriplePattern</code>
     * 
     * @param <code>Object</code>
     * @return <code>true</code>, if other object equals
     *         <code>TriplePattern</code>, otherwise <code>false</code>
     */
    /*@Override
    public boolean equals(Object o) {
        if (o instanceof TriplePattern) {
            TriplePattern other = (TriplePattern) o;

            return subject.equals(other.getSubject()) && predicate.equals(other.getPredicate())
                    && object.equals(other.getObject()) && scope.equals(other.getScope());
        }

        return false;
    }*/

    /**
     * Return hash code
     * 
     * @return <code>int</code>
     */
    @Override
    public int hashCode() {
        int hash = 7;

        hash = 31 * hash + (null == subject ? 0 : subject.hashCode());
        hash = 31 * hash + (null == predicate ? 0 : predicate.hashCode());
        hash = 31 * hash + (null == object ? 0 : object.hashCode());

        return hash;
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