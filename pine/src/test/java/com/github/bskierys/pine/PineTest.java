package com.github.bskierys.pine;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class PineTest {
    private static final String PACKAGE_REPLACEMENT_INDICATOR = "{$$}";

    // default strategies
    @Test public void testCreateLogTagRemovesVowels() throws Exception {
        Pine.TagStrategy strategy = new Pine.DefaultTagStrategy();

        String packageName = PACKAGE_REPLACEMENT_INDICATOR + ".utils.advancedHelpers";
        Pine.LogInfo info = new AutoValue_Pine_LogInfo(packageName, "fakeClass", "fakeMethod", 67);

        String expected = PACKAGE_REPLACEMENT_INDICATOR + ".tls.dvncdHlprs";
        String actual = strategy.format(info);

        assertEquals(expected, actual);
    }

    @Test public void testCreateLogTagDoesNotRemoveVowelOnlyComponents() throws Exception {
        Pine.TagStrategy strategy = new Pine.DefaultTagStrategy();

        String packageName = PACKAGE_REPLACEMENT_INDICATOR + ".communication.ui.wrappers";
        Pine.LogInfo info = new AutoValue_Pine_LogInfo(packageName, "fakeClass", "fakeMethod", 67);

        String expected = PACKAGE_REPLACEMENT_INDICATOR + ".cmmnctn.ui.wrpprs";
        String actual = strategy.format(info);

        assertEquals(expected, actual);
    }

    @Test public void testCreateLogTagWorksWhenNoReplacement() throws Exception {
        Pine.TagStrategy strategy = new Pine.DefaultTagStrategy();

        String packageName = "com.github.bskierys.communication.ui.wrappers";
        Pine.LogInfo info = new AutoValue_Pine_LogInfo(packageName, "fakeClass", "fakeMethod", 67);

        String expected = "cm.gthb.bskrs.cmmnctn.ui.wrpprs";
        String actual = strategy.format(info);

        assertEquals(expected, actual);
    }

    @Test public void testCreateLogMessage() throws Exception {
        Pine.MessageStrategy strategy = new Pine.DefaultMessageStrategy();

        String packageName = "com.github.bskierys.communication.bluetooth.wrappers";
        String className = "SppClientDaemonWrapper";
        String methodName = "onError";
        int line = 67;
        String message = "Hello world!";

        Pine.LogInfo logInfo = new AutoValue_Pine_LogInfo(packageName, className, methodName, line);
        Pine.MessageInfo messageInfo = new AutoValue_Pine_MessageInfo(logInfo, message);

        String expectedMessage = String.format(Locale.UK, "%s, %s, %d ---> %s", className, methodName, line,
                                               message);

        String actualMessage = strategy.format(messageInfo);

        assertEquals(expectedMessage, actualMessage);
    }

    // TODO: 2016-11-19 test #getLogInfo
    // TODO: 2016-11-19 test builder
    @Test public void testReplacesPackage() throws Exception {
        String packageName = "com.github.bskierys";
        String className = packageName + ".utils.advancedHelpers.SearchMvpViewPresenterHelper";

        Pine tree = new Pine.Builder().setPackageReplacePattern(packageName, "REP").grow();
        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);
        Pine.LogInfo info = tree.getLogInfo(element);

        String expectedPackage = PACKAGE_REPLACEMENT_INDICATOR + ".utils.advancedHelpers";
        assertEquals(expectedPackage, info.packageName());
        // TODO: 2016-11-19 test final output (#createStackElementTag)
    }

    @Test public void testReplacesLongPackage() throws Exception {
        String packageName = "com.github.bskierys.pine.sample";
        String className = packageName + ".utils.advancedHelpers.SearchMvpViewPresenterHelper";

        Pine tree = new Pine.Builder().setPackageReplacePattern(packageName, "REP").grow();
        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);
        Pine.LogInfo info = tree.getLogInfo(element);

        String expectedPackage = PACKAGE_REPLACEMENT_INDICATOR + ".utils.advancedHelpers";
        assertEquals(expectedPackage, info.packageName());
    }
}