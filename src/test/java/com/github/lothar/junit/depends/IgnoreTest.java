/*******************************************************************************
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package com.github.lothar.junit.depends;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DependencyRunner.class)
public class IgnoreTest {

    private static List<String> executed = new ArrayList<>();

    @AfterClass
    public static void assertion() {
        assertThat(executed).containsExactly( //
                "test_method1");
    }

    @Test
    public void test_method1() {
        executed.add("test_method1");
    }

    @Test
    @DependsOn("test_method3")
    public void test_method2() {
        executed.add("test_method2");
    }

    @Test
    @Ignore
    public void test_method3() {
        executed.add("test_method3");
    }
}
