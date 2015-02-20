package diamond.retenetwork;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.List;

import diamond.bookkeeping.GraphPatternType;
import diamond.bookkeeping.ExpressionTreeNode;
import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.Solution;
import diamond.data.SolutionSet;
import diamond.data.TriplePattern;
import diamond.data.TripleToken;
import diamond.data.Binding;
import diamond.spin.SpinRule;

/**
 * A rete network based on a SPARQL query
 * 
 * @author Rodolfo Kaplan Depena
 * @author Slavcho Slavchev
 */
public class ReteNetwork {

    private final ReteNode root;
    private Memory lastMemory;
    
    private List<String> selectList;
    private ExpressionTreeNode expressionTreeNode;
    private List<TriplePatternTest> listOfTriplePatternTestNodes;
    private HashSet<String> setOfNonVarElem;
    private SpinRule spinRule;

    /**
     * Create a <code>ReteNetwork</code>
     */
    public ReteNetwork() {
        this.root = new Root();
        this.lastMemory = null;
        
        expressionTreeNode = null;
        listOfTriplePatternTestNodes = new ArrayList<TriplePatternTest>();
        setOfNonVarElem = new HashSet<String>();
    }

    public ReteNetwork(List<String> selectList) {
        this();
        this.selectList = selectList;
    }

    public void setSpinRule(SpinRule rule) {
        this.spinRule = rule;
    }

    public SpinRule getSpinRule() {
        return spinRule;
    }

    public HashSet<String> getSetOfNonVarElem() {
        return setOfNonVarElem;
    }

    /**
     * Set <code>ExpressionTreeNode</code>
     * 
     * @param <code>ExpressionTreeNode</code>
     */
    public void setExpressionTreeNode(ExpressionTreeNode exprTreeNode) {
        expressionTreeNode = exprTreeNode;
    }

    // ****************************************************

    public void createNetwork() {
        lastMemory = eval(expressionTreeNode);
    }

    private Memory eval(ExpressionTreeNode expressionTreeNode) {
        if(expressionTreeNode.getGraphPatternType() == GraphPatternType.UNION_GRAPH_PATTERN) {
            Memory last = null;
            for (ExpressionTreeNode node : expressionTreeNode.getChildren()) {
                Memory mem = eval(node);
                last = (last == null) ? mem : new BetaMemory(new Union(last, mem));
            }
            return last;
        }
        
        Memory last = eval(expressionTreeNode.getTriplePatterns());
        for (ExpressionTreeNode node : expressionTreeNode.getChildren()) {
            GraphPatternType graphPatternType = node.getGraphPatternType();
            Memory mem = eval(node);
            if(last == null) {
                last = mem;
            } else if (graphPatternType == GraphPatternType.OPTIONAL_GRAPH_PATTERN) {
                last = new BetaMemory(new LeftJoin(last, mem));
            } else if (graphPatternType == GraphPatternType.GROUP_GRAPH_PATTERN) {
                last = new BetaMemory(new InnerJoin(last, mem));
            } else if (graphPatternType == GraphPatternType.UNION_GRAPH_PATTERN) {
                last = new BetaMemory(new InnerJoin(last, mem));
            }
        }
        last = initializeFilterNodes(last, expressionTreeNode.getFilterNodes());
        last = initializeBindNodes(last, expressionTreeNode.getBindNodes());
        return last;
    }

    private Memory eval(List<TriplePattern> triplePatterns) {
        Memory leftMemory = null;
        Memory rightMemory = null;

        while (triplePatterns.isEmpty() == false) {
            TriplePattern tp = triplePatterns.remove(0);

            filterURIsInTriplePattern(tp);

            TriplePatternTest tpt = new TriplePatternTest(tp);

            listOfTriplePatternTestNodes.add(tpt);

            root.addChild(tpt);

            AlphaMemory alphaMemory = new AlphaMemory(tpt);

            if (leftMemory == null) {
                leftMemory = alphaMemory;
            } else {
                rightMemory = alphaMemory;

                Join join = null;

                if (alphaMemory.isCartesianProductNecessary()) {
                    join = new CartesianProduct(leftMemory, rightMemory);
                } else {
                    join = new InnerJoin(leftMemory, rightMemory);
                }

                BetaMemory beta = new BetaMemory(join);

                leftMemory = beta;
                rightMemory = null;
            }
        }

        return leftMemory;
    }

    private Memory initializeFilterNodes(Memory last, List<Filter> filterNodes) {
        Memory lastMemory = last;
        for (Filter filter : filterNodes) {
            lastMemory.addChild(filter);
            BetaMemory betaMemory = new BetaMemory(filter);
            lastMemory = betaMemory;
        }
        return lastMemory;
    }

    private Memory initializeBindNodes(Memory last, List<Bind> bindNodes) {
        Memory lastMemory = last;
        for (Bind bind : bindNodes) {
            lastMemory.addChild(bind);
            BetaMemory betaMemory = new BetaMemory(bind);
            lastMemory = betaMemory;
        }
        return lastMemory;
    }

    // *******************************************************

    /**
     * Get <code>SolutionSet</code> of query
     * 
     * @return <code>SolutionSet</code>
     */
    public SolutionSet getSolutionSet() {
        SolutionSet solutionSet = new SolutionSet(selectList);

        // acquire memory with triple tokens
        Set<TripleToken> memory = lastMemory.getMemory();

        // iterate through each triple token in memory
        for (TripleToken tripleToken : memory) {
            Solution solution = extractSolution(tripleToken);
            if (solution.isEmpty() == false) {
                solutionSet.add(solution);
            }
        }

        return solutionSet;
    }

    /**
     * Extract a <code>Solution</code> from a <code>TripleToken</code>
     * 
     * @param tripleToken <code>TripleToken</code>
     * @return <code>Solution</code>
     */
    public Solution extractSolution(TripleToken tripleToken) {
        // initialize solution to return
        Solution solution = new Solution(selectList);

        if (solution.getSolution().containsValue(null) == true) {
            attemptToExtractASolutionFromASingleTripleToken(solution, tripleToken);
        } else {
            return solution;
        }

        // if solution contains null value, replace with empty element
        for (String selectVar : solution.getSolution().keySet()) {
            if (solution.getSolution().get(selectVar) == null) {
                Element emptyElem = new Element(null, null, "");
                solution.addElement(selectVar, emptyElem);
            }
        }

        return solution;
    }

    /**
     * Attempt to extract a <code>Solution</code> given a single
     * <code>TripleToken</code>
     * 
     * @param tripleToken <code>TripleToken</code>
     * @return <code>Solution</code>
     */
    private void attemptToExtractASolutionFromASingleTripleToken(Solution solution, TripleToken tripleToken) {
        // used to track progress of filling all the variables in the select
        // list, we are done searching if true;
        int trackProgress = solution.getSelectList().size();

        // iterate through bindings of triple token trying to fill their
        // solution
        Iterator<Binding> iteratorOverBindings = tripleToken.getBindings().iterator();
        while (iteratorOverBindings.hasNext() && trackProgress > 0) {
            Binding binding = iteratorOverBindings.next();

            // get subject, predicate, and object of satisfying triple pattern
            String subj = binding.getSatisfyingSubject().getData();
            String pred = binding.getSatisfyingPredicate().getData();
            String obj = binding.getSatisfyingObject().getData();

            // if the solution's variable has not received a value yet, then
            // input
            if (solution.getSolution().get(subj) == null && solution.getSelectList().contains(subj)) {
                solution.addElement(subj, binding.getRDFTripleSubject());
                trackProgress--;
            }

            // if the solution's variable has not received a value yet, then
            // input
            if (solution.getSolution().get(pred) == null && solution.getSelectList().contains(pred)) {
                solution.addElement(pred, binding.getRDFTriplePredicate());
                trackProgress--;
            }

            // if the solution's variable has not received a value yet, then
            // input
            if (solution.getSolution().get(obj) == null && solution.getSelectList().contains(obj)) {
                solution.addElement(obj, binding.getRDFTripleObject());
                trackProgress--;
            }
        }
    }

    /**
     * Get <code>Root</code> node of <code>ReteNetwork</code>.
     * 
     * @return <code>Root</code>
     */
    public ReteNode getRoot() {
        return root;
    }

    /**
     * Insert the next RDFTriple token in the ReteNetwork
     * @return true if the RDFTriple matched at least one TriplePattern 
     */
    public boolean insertTokenIntoNetwork(TripleToken tripleToken) throws Exception {
        return root.eval(tripleToken, root);
    }

    /**
     * Filter URIs in <code>TriplePattern</code>
     * 
     * @param tp <code>TriplePattern</code>
     */
    private void filterURIsInTriplePattern(TriplePattern tp) {
        // filter url in subject
        Element subjectElement = tp.getSubject();
        if (subjectElement.getDataType() == DataType.URL) {
            String subjectString = subjectElement.getData();
            if (subjectString.charAt(0) == '<' && subjectString.charAt(subjectString.length() - 1) == '>') {
                subjectString = subjectString.substring(1, subjectString.length() - 1);
                subjectElement.setData(subjectString);
            }
        }

        // filter url in predicate
        Element predicateElement = tp.getPredicate();
        if (predicateElement.getDataType() == DataType.URL) {
            String predicateString = predicateElement.getData();
            if (predicateString.charAt(0) == '<' && predicateString.charAt(predicateString.length() - 1) == '>') {
                predicateString = predicateString.substring(1, predicateString.length() - 1);
                predicateElement.setData(predicateString);
            }
        }

        // filter url in object
        Element objectElement = tp.getObject();
        if (objectElement.getDataType() == DataType.URL) {
            String objectString = objectElement.getData();
            if (objectString.charAt(0) == '<' && objectString.charAt(objectString.length() - 1) == '>') {
                objectString = objectString.substring(1, objectString.length() - 1);
                objectElement.setData(objectString);
            }
        }

    }

    /**
     * Get list of rete nodes in network to traverse through network in a
     * breadth-first fashion
     * 
     * @return ArrayList<ArrayList<ReteNode>>
     */
    public ArrayList<ArrayList<ReteNode>> getListOfReteNodesInNetwork() {
        // initialize nodes in network
        ArrayList<ArrayList<ReteNode>> nodesInNetwork = new ArrayList<ArrayList<ReteNode>>();

        // get the maximum number of levels within the rete network
        int numberOfLevels = getMaximumNumberOfLevels(root);

        // build an array list to contain the particular nodes present in the
        // network at each level
        for (int i = 0; i <= numberOfLevels; ++i) {
            nodesInNetwork.add(new ArrayList<ReteNode>());
        }

        // populate the list of nodes at each level of the network
        nodesInNetwork = addNodes(root, nodesInNetwork, 0);

        return nodesInNetwork;
    }

    /**
     * Get maximum number of levels of the network
     * 
     * @param reteNode <code>ReteNode</code>
     * @return <code>int</code>
     */
    private int getMaximumNumberOfLevels(ReteNode reteNode) {
        return getMaximumNumberOfLevelsHelper(0, reteNode);
    }

    /**
     * Get maximum number of levels of the network
     * 
     * @param maximumLevel <code>int</code>
     * @param reteNode <code>ReteNode</code>
     * @return <code>int</code>
     */
    private int getMaximumNumberOfLevelsHelper(int maximumLevel, ReteNode reteNode) {
        int currentLevel = maximumLevel;

        if (reteNode == null)// if the rete node is null, then return -1 since
                             // to signal there are no levels in the network
        {
            return -1;
        } else if (reteNode.getChildren().size() == 0)// if the rete node has no
                                                      // children, then return
                                                      // the maximum level of
                                                      // the time
        {
            return maximumLevel;
        } else {
            for (ReteNode child : reteNode.getChildren())// iterate through each
                                                         // child of the rete
                                                         // node
            {
                // acquire the potential maximum level through a recursive
                // function on the child
                int potentialMaximumLevel = getMaximumNumberOfLevelsHelper(currentLevel + 1, child);

                // if the current maximum level is less than the potential
                // maximum level found
                if (maximumLevel < potentialMaximumLevel) {
                    maximumLevel = potentialMaximumLevel;// then set the current
                                                         // maximum level to
                                                         // its potential
                }
            }
            return maximumLevel;// finally return the maximum level found
        }
    }

    /**
     * Populate the list of nodes at each level of the network
     * 
     * @param reteNode <code>ReteNode</code>
     * @param nodesInNetwork <code>ArrayList<ArrayList<ReteNode>>
     * @param currentLevel <code>int</code>
     * @return ArrayList<ArrayList<ReteNode>>
     */
    private ArrayList<ArrayList<ReteNode>> addNodes(ReteNode reteNode, ArrayList<ArrayList<ReteNode>> nodesInNetwork,
            int currentLevel) {
        if (reteNode == null)// if rete node is null
        {
            return new ArrayList<ArrayList<ReteNode>>();// return an empty
        } else {
            // if the current level does not contain the particular rete node
            if (nodesInNetwork.get(currentLevel).contains(reteNode) == false) {
                // add the rete node to its corresponding level
                nodesInNetwork.get(currentLevel).add(reteNode);

                // iterate through each child
                for (ReteNode child : reteNode.getChildren()) {
                    currentLevel++;
                    addNodes(child, nodesInNetwork, currentLevel);// add child
                    currentLevel--;
                }
            }
            return nodesInNetwork;
        }
    }

    /**
     * Get last the <code>Memory</code> in a <code>ReteNetwork</code>. It should
     * be a leaf node in the network.
     * 
     * @return <code>Memory</code>
     */
    public Memory getLastMemory() {
        return lastMemory;
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