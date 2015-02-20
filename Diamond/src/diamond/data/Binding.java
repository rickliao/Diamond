/**
 * @author Rodolfo Kaplan Depena
 */
package diamond.data;

/**
 * A <code>Binding</code> occurs between an <code>RDFTriple</code> and a
 * <code>TriplePattern</code>.
 */
public class Binding implements Comparable<Binding> {

    // instance variables
    private TriplePattern triplePattern;
    private RDFTriple rdfTriple;

    /**
     * Create <code>Binding</code>
     */
    public Binding() {
        triplePattern = new TriplePattern();
        rdfTriple = new RDFTriple();
    }

    /**
     * Create <code>Binding</code>
     * 
     * @param binding <code>Binding</code>
     */
    public Binding(Binding binding) {
        triplePattern = new TriplePattern();

        triplePattern.setSubject(binding.getSatisfyingSubject());
        triplePattern.setPredicate(binding.getSatisfyingPredicate());
        triplePattern.setObject(binding.getSatisfyingObject());

        rdfTriple = new RDFTriple();

        rdfTriple.setSubject(binding.getRDFTripleSubject());
        rdfTriple.setPredicate(binding.getRDFTriplePredicate());
        rdfTriple.setObject(binding.getRDFTripleObject());

    }

    /**
     * Create a <code>Binding</code> between a <code>TriplePattern</code> and a
     * <code>RDFTriple</code>.
     * 
     * @param triplePattern <code>TriplePattern</code>
     * @param rdfTriple <code>RDFTriple</code>
     */
    public Binding(TriplePattern triplePattern, RDFTriple rdfTriple) {
        this.triplePattern = triplePattern;
        this.rdfTriple = rdfTriple;
    }

    public RDFTriple getRdfTriple() {
        return rdfTriple;
    }

    public TriplePattern getTriplePattern() {
        return triplePattern;
    }

    /**
     * Get subject of <code>TriplePattern</code>.
     * 
     * @return <code>Element</code>
     */
    public Element getSatisfyingSubject() {
        return triplePattern.subject;
    }

    /**
     * Set subject of <code>TriplePattern</code>
     * 
     * @param subject <code>Element</code>
     */
    public void setSatisfyingSubject(Element subject) {
        this.triplePattern.setSubject(subject);
    }

    /**
     * Get predicate of <code>TriplePattern</code>
     * 
     * @return <code>Element</code>
     */
    public Element getSatisfyingPredicate() {
        return triplePattern.predicate;
    }

    /**
     * Set predicate of <code>TriplePattern</code>
     * 
     * @param predicate <code>Element</code>
     */
    public void setSatisfyingPredicate(Element predicate) {
        this.triplePattern.setPredicate(predicate);
    }

    /**
     * Get object from <code>TriplePattern</code>
     * 
     * @return <code>Element</code>
     */
    public Element getSatisfyingObject() {
        return triplePattern.object;
    }

    /**
     * Set object from <code>TriplePattern</code>
     * 
     * @param object <code>Element</code>
     */
    public void setSatisfyingObject(Element object) {
        triplePattern.setObject(object);
    }

    /**
     * Get <code>RDFTriple</code> subject
     * 
     * @return <code>Element</code>
     */
    public Element getRDFTripleSubject() {
        return rdfTriple.subject;
    }

    /**
     * Set <code>RDFTriple</code> subject
     * 
     * @param <code>Element</code>
     */
    public void setRDFTripleSubject(Element subject) {
        rdfTriple.setSubject(subject);
    }

    /**
     * Get <code>RDFTriple</code> predicate.
     * 
     * @return <code>Element</code>
     */
    public Element getRDFTriplePredicate() {
        return rdfTriple.predicate;
    }

    /**
     * Set <code>RDFTriple</code> predicate.
     * 
     * @param predicate <code>Element</code>
     */
    public void setRDFTriplePredicate(Element predicate) {
        rdfTriple.setPredicate(predicate);
    }

    /**
     * Get <code>RDFTriple</code> object.
     * 
     * @return <code>Element</code>
     */
    public Element getRDFTripleObject() {
        return rdfTriple.object;
    }

    /**
     * Set <code>RDFTriple</code> object.
     * 
     * @param <code>Element</code>
     */
    public void setRDFTripleObject(Element object) {
        rdfTriple.setObject(object);
    }

    /**
     * Returns <code>true</code> if this binding equals that binding, otherwise
     * <code>false</code>
     * 
     * @param <code>Object</code>
     * @return <code>true</code> if this binding equals that binding, otherwise
     *         <code>false</code>
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Binding) {
            Binding that = (Binding) o;
            return this.triplePattern.equals(that.triplePattern) && this.rdfTriple.equals(that.rdfTriple);
        }
        return false;
    }

    /**
     * Return hash code
     * 
     * @return <code>int</code>
     */
    @Override
    public int hashCode() {
        int hash = 29;

        hash = 31 * hash + (null == triplePattern ? 0 : triplePattern.hashCode());
        hash = 31 * hash + (null == rdfTriple ? 0 : rdfTriple.hashCode());

        return hash;
    }

    /**
     * Return <code>String</code> representation of a <code>Binding</code>.
     * 
     * @return <code>String</code>
     */
    @Override
    public String toString() {
        return "(" + triplePattern.subject + "=" + rdfTriple.subject + ", " + triplePattern.predicate + "="
                + rdfTriple.predicate + ", " + triplePattern.object + "=" + rdfTriple.object + ")";
    }

    @Override
    public int compareTo(Binding arg0) {
        return this.getRdfTriple().compareTo(arg0.getRdfTriple());
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