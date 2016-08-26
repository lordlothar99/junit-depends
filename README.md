# Junit-depends [![Build Status](https://travis-ci.org/lordlothar99/junit-depends.svg?branch=master)](https://travis-ci.org/lordlothar99/junit-depends) [![Coverage Status](https://coveralls.io/repos/github/lordlothar99/junit-depends/badge.svg)](https://coveralls.io/github/lordlothar99/junit-depends)

Junit extension allowing dependency between test methods

## Maven installation

Add Github as a maven repository :

	<repositories>
		<repository>
			<id>junit-depends-github-repo</id>
			<url>https://raw.github.com/lordlothar99/mvn-repo/master/</url>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
		</repository>
	</repositories>

Add required dependencies to your pom (latest version is highest tag created on Github) :

	<dependency>
		<groupId>com.github.lothar.junit.depends</groupId>
		<artifactId>junit-depends</artifactId>
		<version>[LATEST]</version>
		<scope>test</scope>
	</dependency>

## Usage

* Annotate your Junit test with `@RunWith(DependencyRunner.class)`
* Define dependencies between tests with `@DependsOn(<method names>)`

## Example

    @Test
    public void test_method1() {
        ...
    }

    @Test
    @DependsOn("test_method3")
    public void test_method2() {
        ...
    }

    @Test
    public void test_method3() {
        ...
    }

Methods will be executed in following order : test_method1 > test_method3 > test_method2
