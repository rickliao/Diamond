/**
 * @author Rodolfo Kaplan Depena
 */
package diamond.data;

import java.net.URI;

/**
 * An RDFTriple that contains a subject, predicate, and object.
 */
public class RDFTriple implements Comparable<RDFTriple> {

	// instance variables
    Element subject;
    Element predicate;
    Element object;

    /**
     * Create RDFTriple
     */
    public RDFTriple() {
        subject = null;
        predicate = null;
        object = null;
    }
    
    public RDFTriple(Element subject, Element predicate, Element object) {
    	this.subject = subject;
    	this.predicate = predicate;
    	this.object = object;
    }

    /**
     * Create RDFTriple
     * 
     * @param rdfTriple
     */
    public RDFTriple(RDFTriple rdfTriple) {
        subject = rdfTriple.getSubject();
        predicate = rdfTriple.getPredicate();
        object = rdfTriple.getObject();
    }

    /**
     * Get subject
     * 
     * @return the subject
     */
    public Element getSubject() {
        return subject;
    }

    /**
     * Set subject as long as subject is not of type VARIABLE
     * 
     * @param subject the subject to set
     * @throws IllegalArgumentException which is invoked when RDF triple element
     *             is a VARIABLE.
     */
    public void setSubject(Element subject) {
        if (subject.getDataType() == DataType.VARIABLE) {
            throw new IllegalArgumentException(subject + " is of type VARIABLE");
        } else {
            this.subject = subject;
        }
    }

    /**
     * Get predicate
     * 
     * @return the predicate
     */
    public Element getPredicate() {
        return predicate;
    }

    /**
     * Set predicate as long as predicate is not of type VARIABLE
     * 
     * @param predicate the predicate to set
     * @throws IllegalArgumentException which is invoked when RDF triple element
     *             is a VARIABLE
     */
    public void setPredicate(Element predicate) {
        if (predicate.getDataType() == DataType.VARIABLE) {
            throw new IllegalArgumentException(predicate + " is of type VARIABLE");
        } else {
            this.predicate = predicate;
        }
    }
    
    public TripleToken convertToTripleToken(boolean isPlus, URI origin) {
        Binding binding = new Binding();
        binding.setRDFTripleSubject(subject);
        binding.setRDFTriplePredicate(predicate);
        binding.setRDFTripleObject(object);
        TripleToken tripleToken = null;
        if (isPlus) {
        	tripleToken = new TripleToken(TokenTag.PLUS, Timestamp.nextTimestamp());
        } else {
        	tripleToken = new TripleToken(TokenTag.MINUS, Timestamp.nextTimestamp());
        }
        tripleToken.addTriple(binding);
        tripleToken.urlWhereTripleTokenCameFrom = origin;
        return tripleToken;
    }

    /**
     * Get object
     * 
     * @return the object
     */
    public Element getObject() {
        return object;
    }

    /**
     * Set object as long as object is not of type VARIABLE
     * 
     * @param object the object to set
     * @throws IllegalArgumentException which is invoked when RDF triple element
     *             is a VARIABLE
     */
    public void setObject(Element object) {
        if (object.getDataType() == DataType.VARIABLE) {
            throw new IllegalArgumentException(object + " is of type VARIABLE");
        } else {
            this.object = object;
        }
    }

    /**
     * Return String representation of RDFTriple
     * 
     * @return String representation of rdf triple
     */
    @Override
    public String toString() {
        return "(" + getSubject() + ", " + getPredicate() + ", " + getObject() + ")";
    }

    /**
     * Overridden equals method.
     * 
     * @param obj
     * @return true if equal, otherwise false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RDFTriple) {
            RDFTriple thatRdfTriple = (RDFTriple) obj;

            return this.subject.equals(thatRdfTriple.getSubject()) // check if
                                                                   // spo's
                                                                   // equal
                    && this.predicate.equals(thatRdfTriple.getPredicate()) // check
                                                                           // if
                                                                           // data
                                                                           // types
                                                                           // equal
                    && this.object.equals(thatRdfTriple.getObject());// check if
                                                                     // both
                                                                     // pieces
                                                                     // of data
                                                                     // equal
        }

        return false;
    }

    /**
     * Override Object's hashCode because we overrode equals() method.
     * 
     * @return hash code
     */
    @Override
    public int hashCode() {
        int hash = 7;

        hash = 31 * hash + (null == subject ? 0 : subject.hashCode());
        hash = 31 * hash + (null == predicate ? 0 : predicate.hashCode());
        hash = 31 * hash + (null == object ? 0 : object.hashCode());

        return hash;
    }

    @Override
    public int compareTo(RDFTriple o) {
        // TODO Auto-generated method stub
        return this.toString().compareTo(o.toString());
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