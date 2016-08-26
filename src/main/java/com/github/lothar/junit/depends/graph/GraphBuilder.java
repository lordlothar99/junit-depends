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
import java.util.List;

public class GraphBuilder<T> {

    private Graph<T> graph;
    private List<T> nodes;

    public GraphBuilder() {
        nodes = new ArrayList<>();
        graph = new Graph<>(new NodeValueListener<T>() {
            public void evaluating(T nodeValue) {
                nodes.add(nodeValue);
            }
        });
    }

    public void addDependency(T before, T after) {
        graph.addDependency(before, after);
    }

    public List<T> getSortedValues() {
        graph.generateDependencies();
        return nodes;
    }
}