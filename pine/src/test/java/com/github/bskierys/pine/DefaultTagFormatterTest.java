package com.github.bskierys.pine;

import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultTagFormatterTest {
    @Test public void testCreateLogTagRemovesVowels() throws Exception {
        TagFormatter strategy = new DefaultTagFormatter();

        String packageName = "{$1$}.utils.advancedHelpers";
        LogInfo info = new AutoValue_LogInfo(packageName, "fakeClass", "fakeMethod", 67);

        String expected = "{$1$}.tls.dvncdHlprs";
        String actual = strategy.format(info);

        assertEquals(expected, actual);
    }

    @Test public void testCreateLogTagDoesNotRemoveVowelOnlyComponents() throws Exception {
        TagFormatter strategy = new DefaultTagFormatter();

        String packageName = "{$1$}.communication.ui.wrappers";
        LogInfo info = new AutoValue_LogInfo(packageName, "fakeClass", "fakeMethod", 67);

        String expected = "{$1$}.cmmnctn.ui.wrpprs";
        String actual = strategy.format(info);

        assertEquals(expected, actual);
    }

    @Test public void testCreateLogTagWorksWhenNoReplacement() throws Exception {
        TagFormatter strategy = new DefaultTagFormatter();

        String packageName = "com.github.bskierys.communication.ui.wrappers";
        LogInfo info = new AutoValue_LogInfo(packageName, "fakeClass", "fakeMethod", 67);

        String expected = "cm.gthb.bskrs.cmmnctn.ui.wrpprs";
        String actual = strategy.format(info);

        assertEquals(expected, actual);
    }
}