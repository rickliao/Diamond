/**
 * @author Rodolfo Kaplan Depena
 * @author Slavcho Slavchev
 */
package diamond.retenetwork;

import diamond.data.Binding;
import diamond.data.TripleToken;
import diamond.data.TokenTag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * <code>Memory</code> nodes, either an <code>AlphaMemory</code> or a
 * <code>BetaMemory</code>.
 */
public abstract class Memory extends ReteNode {
	
	private final HashMap<Integer, HashMap<Integer, LinkedList<TripleToken>>> hashFilter;
	private final Set<TripleToken> memory;
	
	public Memory(ReteNode parent) {
		super();
		this.schema.addAll(parent.getSchema());
		
		this.hashFilter = new HashMap<Integer, HashMap<Integer, LinkedList<TripleToken>>>();
		this.memory = new HashSet<TripleToken>();
		parent.addChild(this);
	}

	public HashMap<Integer, HashMap<Integer, LinkedList<TripleToken>>> getHashFilter() {
		return hashFilter;
	}

	/**
	 * Evaluate a <code>TripleToken</code> by adding it to <code>Memory</code>.
	 * 
	 * @param tripleToken
	 *            <code>TripleToken</code>
	 * @param caller
	 *            <code>ReteNode</code>
	 * @return <code>true</code>, if <code>TripleToken</code> is successfully
	 *         evaluated, otherwise <code>false</code>
	 * @throws <code>Exception</code>
	 */
	@Override
	public boolean eval(TripleToken tripleToken, ReteNode caller)
			throws Exception {
	    boolean successfullyProcessed = false;
		boolean isEvaluated = false;

		Integer hashL1 = tripleToken.getHashVal(schema);
		Integer hashL2 = tripleToken.hashCode();

		// Insert token
		if (tripleToken.getTag() == TokenTag.PLUS) {
			HashMap<Integer, LinkedList<TripleToken>> hfL2 = hashFilter.get(hashL1);
			if (hfL2 == null) {
				hashFilter.put(hashL1, hfL2 = new HashMap<Integer, LinkedList<TripleToken>>());
			}
			LinkedList<TripleToken> tokens = hfL2.get(hashL2);
			if (tokens == null) {
				hfL2.put(hashL2, tokens = new LinkedList<TripleToken>());
			}
			tokens.add(tripleToken);
			successfullyProcessed = memory.add(tripleToken);
		}

		// Delete Token
		else {
			Set<TripleToken> toDel = new HashSet<TripleToken>();
			boolean isContained, allContained;

			LinkedList<TripleToken> matchable = this.hashFilter.get(hashL1).get(hashL2);

			for (TripleToken tokenFromThisMemory : matchable) {
				if (tripleToken.getBindings().size() >= tokenFromThisMemory.getBindings().size()) {
					isContained = false;
					allContained = true;
					for (Binding tripleBind : tripleToken.getBindings()) {
						for (Binding memoryBind : tokenFromThisMemory.getBindings()) {
							if (tripleBind.getRdfTriple().equals(memoryBind.getRdfTriple())) {
								isContained = true;
								break;
							}
						}
						if (!isContained) {
							allContained = false;
							break;
						}
						isContained = false;
					}
					if (allContained) {
						toDel.add(tokenFromThisMemory);
					}
				}
			}

			LinkedList<TripleToken> tokens = hashFilter.get(hashL1).get(hashL2);
			for (TripleToken t : toDel) {
				if (tokens != null) {
					tokens.remove(t);
				}
				memory.remove(t);
			}
			successfullyProcessed = true;
		}

		if(successfullyProcessed) {
		    for (ReteNode child : getChildren()) {
			    isEvaluated |= child.eval(tripleToken, this);
		    }
		}

		return isEvaluated;
	}

	/**
	 * Get <code>Memory</code>, represented as a <code>Set<TripleToken></code>.
	 * 
	 * @return <code>Set<TripleToken></code>
	 */
	public Set<TripleToken> getMemory() {
		return memory;
	}

	@Override
	public void clearMemories() {
		memory.clear();
		for (ReteNode child : getChildren()) {
			child.clearMemories();
		}
	}

	/**
	 * Return a <code>String</code> representation of this <code>Memory</code>
	 * node
	 * 
	 * @return <code>String</code>
	 */
	@Override
	public abstract String toString();
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
