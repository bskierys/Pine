package com.github.bskierys.pine;

import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

// TODO: 2016-11-09 write more tests --> see original timber tests
public class PineTest {
    private Pine tree;

    @Before public void setUp() throws Exception {
        tree = new Pine();
    }

    @Test public void testCreateLogTag1() throws Exception {
        String className = "pl.ipebk.tabi.utils.advancedHelpers.SearchMvpViewPresenterHelper";
        String expected = "tbi.tls.dvncd.hlprs";

        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);
        String actual = tree.createStackElementTag(element);

        assertEquals(expected, actual);
    }

    @Test public void testCreateLogTag2() throws Exception {
        String className = "pl.ipebk.tabi.communication.bluetooth.wrappers.SppClientDaemonWrapper";
        String expected = "tbi.cmmnctn.bltth.wrpprs";

        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);
        String actual = tree.createStackElementTag(element);

        assertEquals(expected, actual);
    }

    @Test public void testCreateLogTagForDifferentPackage() throws Exception {
        String className = "com.github.package.communication.bluetooth.wrappers.SppClientDaemonWrapper";
        String expected = "PACKAGE.cmmnctn.bltth.wrpprs";

        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);
        String actual = tree.createStackElementTag(element);

        assertEquals(expected, actual);
    }

    @Test public void testCreateLogMessage() throws Exception {
        String packageName = "pl.ipebk.tabi.communication.bluetooth.wrappers";
        String className = "SppClientDaemonWrapper";
        String fullClassName = packageName + "." + className;
        String methodName = "onError";
        int line = 67;
        String message = "Hello world!";

        String expectedTag = "tbi.cmmnctn.bltth.wrpprs";
        String expectedMessage = String.format(Locale.UK, "%s, %s, %d ---> %s", className, methodName, line, message);

        StackTraceElement element = new StackTraceElement(fullClassName, methodName, "fakeFile", line);
        String actualTag = tree.createStackElementTag(element);
        String actualMessage = tree.createStackElementMessage(message, element);

        assertEquals(expectedTag, actualTag);
        assertEquals(expectedMessage, actualMessage);
    }
}