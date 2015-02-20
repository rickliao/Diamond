/**
 * @author Rodolfo Kaplan Depena
 */
package diamond.data;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.bag.HashBag;

/**
 * <code>SolutionSet</code> is a <code>HashBag</code> of solutions (a.k.a. -
 * multiset).
 */
public class SolutionSet extends HashBag {

    // instance variables
    private List<String> selectList;
    private static final long serialVersionUID = -2293500617304557403L;
    private int size;

    /**
     * Create SolutionSet
     */
    public SolutionSet(List<String> selectList) {
        super();
        this.selectList = selectList;
        size = 0;
    }

    /**
     * Add a solution to the set
     * 
     * @param solution
     */
    public boolean add(Solution s) {
        if (selectList.equals(s.getSelectList()) == false) {
            throw new IllegalArgumentException("Select list from solution does not"
                    + " match solution set. Solution is invalid.");
        }

        boolean isAdded = false;

        if (s.isConflicted() == false) {
            isAdded = super.add(s);
        }

        if (isAdded) {
            size++;
        }

        return isAdded;
    }

    /**
     * Determines if method is empty
     * 
     * @return <code>true</code> if solution set is empty, else
     *         <code>false</code>
     */
    @Override
    public boolean isEmpty() {
        if (size > 1) {
            return false;
        } else if (size == 0) {
            return true;
        } else if (size == 1) {
            boolean empty = true;

            // acquire iterator to iterate through solution set
            @SuppressWarnings("unchecked")
            Iterator<Solution> solutionIterator = this.iterator();

            // while there is a next solution
            while (solutionIterator.hasNext()) {
                // acquire solution
                Solution solution = solutionIterator.next();

                for (String var : solution.getSelectList()) {
                    if (solution.getSolution().get(var).toString().equals("") == false) {
                        empty = false;
                    }
                }

                return empty;
            }
        }

        return false;
    }

    /**
     * Add a solution to the solution set
     * 
     * @param s <code>Solution</code>
     * @return <code>true</code> if <code>Solution</code> was successfully
     *         removed, otherwise false
     */
    public boolean remove(Solution s) {

        boolean isRemoved = super.remove(s);

        if (isRemoved) {
            size--;
        }

        return isRemoved;

    }

    /**
     * Add size
     * 
     * @return <code>int</code>
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Overridden equals method.
     * 
     * @param <code>Object</code>
     * @return <code>true</code> if equal, otherwise <code>false</code>
     */
    @SuppressWarnings({ "rawtypes", "unused" })
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof SolutionSet == false) {
            return false;
        }

        SolutionSet other = (SolutionSet) object;
        if (other.size() != size()) {
            return false;
        }

        // if(other.selectList.equals(selectList))
        // {
        // return false;
        // }

        Iterator thisIt = this.getMap().keySet().iterator();
        Iterator thatIt = other.getMap().keySet().iterator();

        boolean present = false;
        for (Iterator thisit = this.getMap().keySet().iterator(); thisit.hasNext();) {
            present = false;
            Object thiselement = thisit.next();

            for (Iterator thatit = other.getMap().keySet().iterator(); thatit.hasNext();) {
                Object thatelement = thatit.next();

                if (thiselement.equals(thatelement)) {
                    present = true;
                }
            }

            if (present == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * Override Object's hashCode because we overrode equals() method.
     * 
     * @return <code>int</code>
     */
    @Override
    public int hashCode() {
        int hash = 7;

        hash = 31 * hash + (null == selectList ? 0 : selectList.hashCode());
        hash = 31 * hash + (null == getMap().keySet() ? 0 : getMap().keySet().hashCode());
        hash = 31 * hash + (null == getMap().values() ? 0 : getMap().values().hashCode());

        return hash;
    }

    /**
     * Returns a <code>String</code> representation of <code>SolutionSet</code>
     * 
     * @return <code>String</code>
     */
    @Override
    public String toString() {
        // acquires max length for each variable presented in solution set
        Map<String, Integer> maximumStringLengthForEachVariable = new HashMap<String, Integer>();

        // for each variable, initialize the maximum value
        for (String var : selectList) {
            maximumStringLengthForEachVariable.put(var, 0);
        }

        // for each variable, acquire the maximum string length of all solutions
        // in the solution set
        for (String var : selectList) {
            // acquire iterator to iterate through solution set
            @SuppressWarnings("unchecked")
            Iterator<Solution> solutionIterator = this.iterator();

            // while there is a next solution
            while (solutionIterator.hasNext()) {
                // acquire current max for this variable
                int currentMaxLengthForVar = maximumStringLengthForEachVariable.get(var);

                // acquire solution
                Solution solution = solutionIterator.next();

                // acquire potential max length for this variable
                int potentialMaxLengthForVar = solution.getSolution().get(var).getData().length();

                // compare a potential maximum length with the current maximum
                // length on hand
                if (potentialMaxLengthForVar > currentMaxLengthForVar) {
                    maximumStringLengthForEachVariable.put(var, potentialMaxLengthForVar);// set
                                                                                          // new
                                                                                          // maximum
                }
            }
        }

        // initialize number of dashes for each solution
        int numberOfDashesToDivideEachSolution = 0;

        // for each variable in select list
        for (String var : selectList) {
            // acquire the number of dashes for particular solution
            numberOfDashesToDivideEachSolution += maximumStringLengthForEachVariable.get(var) + 3;
        }

        // initialize dashes
        String dashes = "";

        // create specified number of dashes to divide each solution
        for (int i = 0; i < numberOfDashesToDivideEachSolution; ++i) {
            dashes += "-";
        }

        // initialize formatter which collects solution set in a formatted string
        @SuppressWarnings("resource")
        Formatter formatter = new Formatter();

        for (String var : selectList) {
            // initialize white space for top variable listing
            String whitespaces = "";

            // for each max length, produce a white space
            for (int i = 0; i < maximumStringLengthForEachVariable.get(var) + 1; ++i) {
                whitespaces += " ";

            }

            // append white spaces
            formatter.format("%s", var + whitespaces);
        }

        // if select list is not empty
        if (selectList.size() > 0) {
            formatter.format("\n");
            formatter.format(dashes);
            formatter.format("\n");
        }

        // acquire iterator to iterate through solution set
        @SuppressWarnings("unchecked")
        Iterator<Solution> solutionIterator = this.iterator();

        // while there is a next solution
        while (solutionIterator.hasNext()) {
            // formatter.format("\n");
            // formatter.format(dashes);
            // formatter.format("\n");
            formatter.format("|");

            Solution solution = solutionIterator.next();

            // append the rest of the solution
            for (int i = 0; i < selectList.size(); ++i) {
                if (i == 0) {
                    formatter.format(" %s", solution.getSolution().get(selectList.get(i)));

                    int minimumPadLength = ((maximumStringLengthForEachVariable.get(selectList.get(i))) + 2)
                            - (" " + solution.getSolution().get(selectList.get(i))).length();

                    String pad = "";

                    for (int j = 0; j < minimumPadLength + 1; ++j) {
                        pad += " ";
                    }

                    formatter.format(pad);
                } else {
                    formatter.format("| %s", solution.getSolution().get(selectList.get(i)));

                    int minimumPadLength = ((maximumStringLengthForEachVariable.get(selectList.get(i))) + 2)
                            - ("| " + solution.getSolution().get(selectList.get(i))).length();

                    String pad = "";

                    for (int j = 0; j < minimumPadLength + 1; ++j) {
                        pad += " ";
                    }

                    formatter.format(pad);
                }
            }

            formatter.format("|");
            formatter.format("\n");
            formatter.format(dashes);
            formatter.format("\n");
        }

        if (selectList.size() == 0 && formatter.toString().contains("||")) {
            return "No solution exists ...\n\n";
        }

        // return formatted string
        return formatter.toString();
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