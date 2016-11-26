/*
* author: Bartlomiej Kierys
* date: 2016-11-25
* email: bskierys@gmail.com
*/
package com.github.bskierys.pine;

/**
 * Default message formatter. Message: {class name}, {method name}, {line number} ---&gt; {message}.
 */
public class DefaultMessageFormatter implements MessageFormatter {
    private static final String DELIMITER = " ---> ";

    @Override public String format(MessageInfo info) {
        return info.logInfo().className()
                + ", " + info.logInfo().methodName()
                + ", " + Integer.toString(info.logInfo().lineNumber())
                + DELIMITER + info.message();
    }
}
