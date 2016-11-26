/*
* author: Bartlomiej Kierys
* date: 2016-11-25
* email: bskierys@gmail.com
*/
package com.github.bskierys.pine;

/**
 * Interface to format log message the way you like. When formatting you can use all the data from {@link MessageInfo}
 */
public interface MessageFormatter {
    /**
     * Format tag to user's liking. At this point {@link LogInfo#packageName()} return string with partialy replaced
     * package. If you have added replace pattern with {@link com.github.bskierys.pine.Pine
     * .Builder#addPackageReplacePattern(String,
     * String)} package you specified to replace will be replaced with package-inner-mark of pattern '{$%d$}' where '%d'
     * is the number of package you have added (chronological).
     */
    String format(MessageInfo info);
}
