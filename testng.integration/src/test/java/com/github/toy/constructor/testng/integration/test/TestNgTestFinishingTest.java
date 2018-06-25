package com.github.toy.constructor.testng.integration.test;

import com.github.toy.constructor.testng.integration.properties.RefreshEachTimeBefore;
import com.github.toy.constructor.testng.integration.test.ignored.IgnoredStubTest;
import com.github.toy.constructor.testng.integration.test.ignored.entries.IgnoredStubTest2;
import org.testng.TestNG;
import org.testng.annotations.Test;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.List;

import static com.github.toy.constructor.testng.integration.properties.RefreshEachTimeBefore.*;
import static com.github.toy.constructor.testng.integration.properties.TestNGRefreshStrategyProperty.REFRESH_STRATEGY_PROPERTY;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestNgTestFinishingTest {

    private void runBeforeTheChecking() {
        TestNG testNG=new TestNG();

        List<XmlSuite> testSuites=new ArrayList<>();

        XmlSuite suite=new XmlSuite();
        suite.setName("FinishSuite");

        XmlTest test = new XmlTest(suite);

        List<XmlClass> testClasses=new ArrayList<>();
        testClasses.add(new XmlClass(TestNgInstantiationTest.class.getName()));
        testClasses.add(new XmlClass(TestNgStubTest.class.getName()));
        testClasses.add(new XmlClass(IgnoredStubTest2.class.getName()));
        testClasses.add(new XmlClass(IgnoredStubTest.class.getName()));

        test.setXmlClasses(testClasses);
        testSuites.add(suite);

        testNG.setXmlSuites(testSuites);
        testNG.run();
    }

    @Test
    public void shuttingDownTest() {
        runBeforeTheChecking();
        assertThat(TestNgInstantiationTest.testNgInstantiationTest.getStepClass2()
                .getStopCount(), is(2));
    }

    @Test
    public void whenNoRefreshingStrategyIsDefined() {
        runBeforeTheChecking();
        assertThat(TestNgStubTest.testNgStubTest
                .getStepClass2().getRefreshCount(), is(3));
    }

    @Test
    public void whenNoRefreshingStrategyIsBeforeSuite() {
        REFRESH_STRATEGY_PROPERTY.accept(SUITE_STARTING.name());
        try {
            runBeforeTheChecking();
            assertThat(TestNgStubTest.testNgStubTest
                    .getStepClass2().getRefreshCount(), is(2));
        }
        finally {
            System.getProperties().remove(REFRESH_STRATEGY_PROPERTY.getPropertyName());
        }
    }

    @Test
    public void whenNoRefreshingStrategyIsBeforeTest() {
        REFRESH_STRATEGY_PROPERTY.accept(ALL_TEST_STARTING.name());
        try {
            runBeforeTheChecking();
            assertThat(TestNgStubTest.testNgStubTest
                    .getStepClass2().getRefreshCount(), is(2));
        }
        finally {
            System.getProperties().remove(REFRESH_STRATEGY_PROPERTY.getPropertyName());
        }
    }

    @Test
    public void whenNoRefreshingStrategyIsBeforeClass() {
        REFRESH_STRATEGY_PROPERTY.accept(CLASS_STARTING.name());
        try {
            runBeforeTheChecking();
            assertThat(TestNgStubTest.testNgStubTest
                    .getStepClass2().getRefreshCount(), is(3));
        }
        finally {
            System.getProperties().remove(REFRESH_STRATEGY_PROPERTY.getPropertyName());
        }
    }

    @Test
    public void whenNoRefreshingStrategyIsBeforeMethod() {
        REFRESH_STRATEGY_PROPERTY.accept(BEFORE_METHOD_STARTING.name());
        try {
            runBeforeTheChecking();
            assertThat(TestNgStubTest.testNgStubTest
                    .getStepClass2().getRefreshCount(), is(7));
        }
        finally {
            System.getProperties().remove(REFRESH_STRATEGY_PROPERTY.getPropertyName());
        }
    }

    @Test
    public void whenNoRefreshingStrategyIsCombined() {
        REFRESH_STRATEGY_PROPERTY.accept(String.join(",", stream(RefreshEachTimeBefore.values())
                .map(Enum::name).collect(toList())));
        try {
            runBeforeTheChecking();
            assertThat(TestNgStubTest.testNgStubTest
                    .getStepClass2().getRefreshCount(), is(17));
        }
        finally {
            System.getProperties().remove(REFRESH_STRATEGY_PROPERTY.getPropertyName());
        }
    }
}
