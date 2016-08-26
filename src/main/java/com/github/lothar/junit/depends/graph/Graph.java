/*******************************************************************************
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.github.lothar.junit.depends.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Graph<T> {
    /**
     * These are basically the nodes of the graph
     */
    private HashMap<T, GraphNode<T>> nodes = new HashMap<T, GraphNode<T>>();
    /**
     * The callback interface used to notify of the fact that a node just got the evaluation
     */
    private NodeValueListener<T> listener;
    /**
     * It holds a list of the already evaluated nodes
     */
    private List<GraphNode<T>> evaluatedNodes = new ArrayList<GraphNode<T>>();

    /**
     * The main constructor that has one parameter representing the callback mechanism used by this
     * class to notify when a node gets the evaluation.
     *
     * @param listener The callback interface implemented by the user classes
     */
    public Graph(NodeValueListener<T> listener) {
        this.listener = listener;
    }

    /**
     * Allows adding of new dependicies to the graph. "evalFirstValue" needs to be evaluated before
     * "evalAfterValue"
     *
     * @param evalFirstValue The parameter that needs to be evaluated first
     * @param evalAfterValue The parameter that needs to be evaluated after
     */
    public void addDependency(T evalFirstValue, T evalAfterValue) {
        GraphNode<T> firstNode = null;
        GraphNode<T> afterNode = null;
        if (nodes.containsKey(evalFirstValue)) {
            firstNode = nodes.get(evalFirstValue);
        } else {
            firstNode = createNode(evalFirstValue);
            nodes.put(evalFirstValue, firstNode);
        }
        if (nodes.containsKey(evalAfterValue)) {
            afterNode = nodes.get(evalAfterValue);
        } else {
            afterNode = createNode(evalAfterValue);
            nodes.put(evalAfterValue, afterNode);
        }
        firstNode.addGoingOutNode(afterNode);
        afterNode.addComingInNode(firstNode);
    }

    /**
     * Creates a graph node of the <T> generic type
     *
     * @param value The value that is hosted by the node
     * @return a generic GraphNode object
     */
    private GraphNode<T> createNode(T value) {
        GraphNode<T> node = new GraphNode<T>();
        node.value = value;
        return node;
    }

    /**
     *
     * Takes all the nodes and calculates the dependency order for them.
     *
     */
    public void generateDependencies() {
        List<GraphNode<T>> orphanNodes = getOrphanNodes();
        List<GraphNode<T>> nextNodesToDisplay = new ArrayList<GraphNode<T>>();
        for (GraphNode<T> node : orphanNodes) {
            listener.evaluating(node.value);
            evaluatedNodes.add(node);
            nextNodesToDisplay.addAll(node.getGoingOutNodes());
        }
        generateDependencies(nextNodesToDisplay);
    }

    /**
     * Generates the dependency order of the nodes passed in as parameter
     *
     * @param nodes The nodes for which the dependency order order is executed
     */
    private void generateDependencies(List<GraphNode<T>> nodes) {
        List<GraphNode<T>> nextNodesToDisplay = null;
        for (GraphNode<T> node : nodes) {
            if (!isAlreadyEvaluated(node)) {
                List<GraphNode<T>> comingInNodes = node.getComingInNodes();
                if (areAlreadyEvaluated(comingInNodes)) {
                    listener.evaluating(node.value);
                    evaluatedNodes.add(node);
                    List<GraphNode<T>> goingOutNodes = node.getGoingOutNodes();
                    if (goingOutNodes != null) {
                        if (nextNodesToDisplay == null)
                            nextNodesToDisplay = new ArrayList<GraphNode<T>>();
                        // add these too, so they get a chance to be displayed
                        // as well
                        nextNodesToDisplay.addAll(goingOutNodes);
                    }
                } else {
                    if (nextNodesToDisplay == null)
                        nextNodesToDisplay = new ArrayList<GraphNode<T>>();
                    // the checked node should be carried
                    nextNodesToDisplay.add(node);
                }
            }
        }
        if (nextNodesToDisplay != null) {
            generateDependencies(nextNodesToDisplay);
        }
        // here the recursive call ends
    }

    /**
     * Checks to see if the passed in node was aready evaluated A node defined as already evaluated
     * means that its incoming nodes were already evaluated as well
     *
     * @param node The Node to be checked
     * @return The return value represents the node evaluation status
     */
    private boolean isAlreadyEvaluated(GraphNode<T> node) {
        return evaluatedNodes.contains(node);
    }

    /**
     * Check to see if all the passed nodes were already evaluated. This could be thought as an and
     * logic between every node evaluation status
     *
     * @param nodes The nodes to be checked
     * @return The return value represents the evaluation status for all the nodes
     */
    private boolean areAlreadyEvaluated(List<GraphNode<T>> nodes) {
        return evaluatedNodes.containsAll(nodes);
    }

    /**
     *
     * These nodes represent the starting nodes. They are firstly evaluated. They have no incoming
     * nodes. The order they are evaluated does not matter.
     *
     * @return It returns a list of graph nodes
     */
    private List<GraphNode<T>> getOrphanNodes() {
        List<GraphNode<T>> orphanNodes = null;
        Set<T> keys = nodes.keySet();
        for (T key : keys) {
            GraphNode<T> node = nodes.get(key);
            if (node.getComingInNodes() == null) {
                if (orphanNodes == null)
                    orphanNodes = new ArrayList<GraphNode<T>>();
                orphanNodes.add(node);
            }
        }
        return orphanNodes;
    }
}
