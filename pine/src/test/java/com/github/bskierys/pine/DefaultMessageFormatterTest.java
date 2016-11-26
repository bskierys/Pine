package com.github.bskierys.pine;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

public class DefaultMessageFormatterTest {
    @Test public void testCreateLogMessage() throws Exception {
        MessageFormatter strategy = new DefaultMessageFormatter();

        String packageName = "com.github.bskierys.communication.bluetooth.wrappers";
        String className = "SppClientDaemonWrapper";
        String methodName = "onError";
        int line = 67;
        String message = "Hello world!";

        LogInfo logInfo = new AutoValue_LogInfo(packageName, className, methodName, line);
        MessageInfo messageInfo = new AutoValue_MessageInfo(logInfo, message);

        String expectedMessage = String.format(Locale.UK, "%s, %s, %d ---> %s", className, methodName, line,
                                               message);

        String actualMessage = strategy.format(messageInfo);

        assertEquals(expectedMessage, actualMessage);
    }
}