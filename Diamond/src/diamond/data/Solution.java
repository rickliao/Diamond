/**
 * @author Rodolfo Kaplan Depena
 */
package diamond.data;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * <code>Solution</code> is a mapping between variable and <code>Element</code>
 */
public class Solution {

    // instance variables
    private List<String> selectList;
    private Map<String, Element> solution;
    private boolean conflicted;

    /**
     * Create Solution
     */
    public Solution(List<String> selectList) {
        // initialize the solution with the select list
        this.selectList = selectList;
        solution = new HashMap<String, Element>();
        for (String var : selectList) {
            solution.put(var, null);
        }

        conflicted = false;
    }

    /**
     * If a variable is being attempted to map to too
     * 
     * @return <code>true</code>, if conflicted, else <code>false</code>
     */
    public boolean isConflicted() {
        return conflicted;
    }

    /**
     * Return select list
     * 
     * @return select list
     */
    public List<String> getSelectList() {
        return selectList;
    }

    /**
     * Add variable and element to solution
     * 
     * @param possibleVar
     * @param e
     */
    public void addElement(Element possibleVar, Element e) {
        if (possibleVar.getDataType() == DataType.VARIABLE && selectList.contains(possibleVar.getData())) {
            if (solution.get(possibleVar.getData()) != null && solution.get(possibleVar.getData()).equals(e) == false) {
                conflicted = true;
            } else {
                solution.put(possibleVar.getData(), e);
            }
        }
    }

    /**
     * Add a variable (in String form) and element to solution
     * 
     * @param possibleVar
     * @param e
     */
    public void addElement(String possibleVar, Element e) {
        if (possibleVar == null || possibleVar.length() < 2 || possibleVar.charAt(0) != '?'
                || possibleVar.charAt(0) == '$' || selectList.contains(possibleVar) == false) {
            throw new IllegalArgumentException(possibleVar + " is not a valid argument when adding an Element");
        }

        if (solution.get(possibleVar) != null && solution.get(possibleVar).equals(e) == false) {
            conflicted = true;
        } else {
            solution.put(possibleVar, e);
        }
    }

    /**
     * Return the solution, a map between variable and element
     * 
     * @return solution
     */
    public Map<String, Element> getSolution() {
        return solution;
    }

    /**
     * If solution is empty return true, otherwise false
     * 
     * @return true if empty, otherwise false
     */
    public boolean isEmpty() {
        return solution.isEmpty();
    }

    /**
     * Find out if this object equals that object
     * 
     * @param obj
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Solution) {
            Solution otherSoln = (Solution) obj;
            return selectList.equals(otherSoln.getSelectList()) && solution.equals(otherSoln.getSolution());
        }
        return false;
    }

    /**
     * Compute and return hash code
     * 
     * @return hash code
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (null == solution ? 0 : solution.hashCode());
        hash = 31 * hash + (null == selectList ? 0 : selectList.hashCode());
        return hash;
    }

    /**
     * Return to String representation of solution
     * 
     * @return String representation of solution
     */
    @Override
    public String toString() {
        return solution.toString();
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