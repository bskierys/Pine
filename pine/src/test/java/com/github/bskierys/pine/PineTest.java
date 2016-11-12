package com.github.bskierys.pine;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class PineTest {
    private static final String MOCK_PACKAGE_NAME = "com.example.pine";
    private static final String SAMPLE_PACKAGE_TAG = "pine";

    @Mock Context context;
    private Pine tree;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(context.getPackageName()).thenReturn(MOCK_PACKAGE_NAME);

        tree = new Pine(context, SAMPLE_PACKAGE_TAG);
    }

    @Test public void testCreateLogTag1() throws Exception {
        String className = MOCK_PACKAGE_NAME + ".utils.advancedHelpers.SearchMvpViewPresenterHelper";
        String expected = SAMPLE_PACKAGE_TAG + ".tls.dvncd.hlprs";

        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);
        String actual = tree.createStackElementTag(element);

        assertEquals(expected, actual);
    }

    @Test public void testCreateLogTag2() throws Exception {
        String className = MOCK_PACKAGE_NAME + ".communication.bluetooth.wrappers.SppClientDaemonWrapper";
        String expected = SAMPLE_PACKAGE_TAG + ".cmmnctn.bltth.wrpprs";

        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);
        String actual = tree.createStackElementTag(element);

        assertEquals(expected, actual);
    }

    @Test public void testCreateLogTagForDifferentPackage() throws Exception {
        String className = "com.github.package.communication.bluetooth.wrappers.SppClientDaemonWrapper";
        String expected = "cm.gthb.pckg.cmmnctn.bltth.wrpprs";

        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);
        String actual = tree.createStackElementTag(element);

        assertEquals(expected, actual);
    }

    @Test public void testCreateLogMessage() throws Exception {
        String packageName = MOCK_PACKAGE_NAME + ".communication.bluetooth.wrappers";
        String className = "SppClientDaemonWrapper";
        String fullClassName = packageName + "." + className;
        String methodName = "onError";
        int line = 67;
        String message = "Hello world!";

        String expectedTag = SAMPLE_PACKAGE_TAG + ".cmmnctn.bltth.wrpprs";
        String expectedMessage = String.format(Locale.UK, "%s, %s, %d ---> %s", className, methodName, line, message);

        StackTraceElement element = new StackTraceElement(fullClassName, methodName, "fakeFile", line);
        String actualTag = tree.createStackElementTag(element);
        String actualMessage = tree.createStackElementMessage(message, element);

        assertEquals(expectedTag, actualTag);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test public void textCreateLogTagLongPackage() throws Exception {
        String longPackage = "com.example.pine.sample";
        when(context.getPackageName()).thenReturn(longPackage);
        tree = new Pine(context, SAMPLE_PACKAGE_TAG);

        String className = longPackage + ".utils.advancedHelpers.SearchMvpViewPresenterHelper";
        String expected = SAMPLE_PACKAGE_TAG + ".tls.dvncd.hlprs";

        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);
        String actual = tree.createStackElementTag(element);

        assertEquals(expected, actual);
    }
}