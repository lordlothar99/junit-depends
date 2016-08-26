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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

public class GraphBuilderTest {

    @Test
    public void should_values_be_sorted() {
        GraphBuilder<String> builder = new GraphBuilder<>();
        builder.addDependency("a", "b");
        builder.addDependency("a", "c");
        builder.addDependency("a", "f");
        builder.addDependency("c", "d");
        builder.addDependency("d", "g");
        builder.addDependency("f", "d");
        builder.addDependency("h", "e");
        List<String> sortedValues = builder.getSortedValues();
        assertThat(sortedValues).containsExactly("a", "h", "b", "c", "f", "e", "d", "g");
    }
}
