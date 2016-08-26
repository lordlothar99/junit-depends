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
package com.github.lothar.junit.depends;

import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.intersection;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import com.github.lothar.junit.depends.graph.GraphBuilder;

/**
 * A runner which is aware of the {@link DependsOn} of its {@link org.junit.Test}s
 */
public class DependencyRunner extends BlockJUnit4ClassRunner {

    private static final String ROOT = "<root>";
    private DependencyListener dependencyListener;
    private List<String> invalidDependencies = new ArrayList<>();

    public DependencyRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> methods = super.computeTestMethods();

        // compute dependencies graph
        GraphBuilder<String> builder = new GraphBuilder<>();
        for (FrameworkMethod method : methods) {
            DependsOn depends = method.getAnnotation(DependsOn.class);
            if (depends == null || isEmpty(depends.value())) {
                builder.addDependency(ROOT, method.getName());
            } else {
                for (String dependency : depends.value()) {
                    builder.addDependency(dependency, method.getName());
                }
            }
        }

        // return ordered methods
        List<String> sortedMethodNames = builder.getSortedValues();
        List<FrameworkMethod> sortedMethods = new ArrayList<>();
        for (String methodName : sortedMethodNames) {
            if (ROOT.equals(methodName)) {
                continue;
            }
            sortedMethods.add(getFwkMethod(methods, methodName));
        }

        return sortedMethods;
    }

    private static FrameworkMethod getFwkMethod(List<FrameworkMethod> methods, String methodName) {
        for (FrameworkMethod method : methods) {
            if (methodName.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }

    protected Statement childrenInvoker(final RunNotifier notifier) {
        // inject dependency listener
        dependencyListener = new DependencyListener();
        notifier.addListener(dependencyListener);
        return super.childrenInvoker(notifier);
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        Collection<String> missingDependencies = getMissingDependencies(method);

        if (isEmpty(missingDependencies)) {
            super.runChild(method, notifier);

        } else {
            invalidDependencies.add(method.getName());

            // fire assumption failed
            Description description = describeChild(method);
            notifier.fireTestStarted(description);
            notifier.fireTestAssumptionFailed(new Failure(description,
                    new MissingDependencyException(join(missingDependencies, ","))));
            notifier.fireTestFinished(description);
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getMissingDependencies(FrameworkMethod method) {
        DependsOn depends = method.getAnnotation(DependsOn.class);
        if (depends == null) {
            return null;
        }

        return intersection(invalidDependencies, asList(depends.value()));
    }

    private class DependencyListener extends RunListener {

        @Override
        public void testAssumptionFailure(Failure failure) {
            String methodName = failure.getDescription().getMethodName();
            invalidDependencies.add(methodName);
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            String methodName = failure.getDescription().getMethodName();
            invalidDependencies.add(methodName);
        }

        @Override
        public void testIgnored(Description description) throws Exception {
            String methodName = description.getMethodName();
            invalidDependencies.add(methodName);
        }
    }
}
