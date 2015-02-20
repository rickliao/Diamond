/**
 * @author Rodolfo Kaplan Depena
 */
package diamond.data;

/**
 * An Element refers to a particular piece of data in a Resource Description
 * Framework (RDF) triple.
 */
public class Element {

    // instance variables
    private SPO spo;
    private DataType dataType;
    private String data;

    /**
     * Create an element
     * 
     * @param spo
     * @param dataType
     * @param data
     * 
     */
    public Element(SPO spo, DataType dataType, java.lang.String data) {
        this.spo = spo;
        this.dataType = dataType;
        this.data = data;
    }

    /**
     * Get SPO
     * 
     * @return the spo
     */
    public SPO getSpo() {
        return spo;
    }

    /**
     * Set SPO
     * 
     * @param spo the spo to set
     */
    public void setSpo(SPO spo) {
        this.spo = spo;
    }

    /**
     * Get DataType
     * 
     * @return the dataType
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Set DataType
     * 
     * @param dataType the dataType to set
     */
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Get data
     */
    public String getData() {
        return data;
    }

    /**
     * Set data
     * 
     * @param data the data to set
     * 
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Return String representation of Element
     */
    @Override
    public String toString() {
        // return "< " + data + "; " + spo + "; " + dataType + " >";
        return data;
    }

    /**
     * Overridden equals method.
     * 
     * @param obj
     * @return true if equal, otherwise false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Element) {
            Element thatElem = (Element) obj;

            // return this.spo == thatElem.getSpo() //check if spo's equal
            // && this.dataType == thatElem.getDataType() //check if data types
            // equal
            // && this.data.equals(thatElem.getData());//check if both pieces of
            // data equal
            // if(this.spo == thatElem.getSpo() && this.dataType ==
            // thatElem.getDataType())
            // {
            if (this.getDataType() == DataType.BLANK_NODE && thatElem.getDataType() == DataType.BLANK_NODE) {
                return this.data.startsWith("node") && thatElem.data.startsWith("node");
            } else {
                return this.data.equals(thatElem.getData());
            }
            // }
            // else
            // {
            // return false;
            // }
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

        hash = 31 * hash + (null == spo ? 0 : spo.hashCode());
        hash = 31 * hash + (null == dataType ? 0 : dataType.hashCode());
        hash = 31 * hash + (null == data ? 0 : data.hashCode());

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