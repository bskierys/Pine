/*
* author: Bartlomiej Kierys
* date: 2016-11-25
* email: bskierys@gmail.com
*/
package com.github.bskierys.pine;

import com.google.auto.value.AutoValue;

/**
 * <p>Wrapper for information about where message was logged from. Available info:</p> <ul> <li>all available in
 * {@link LogInfo}</li> <li>actual message</li> </ul>
 */
@AutoValue
public abstract class MessageInfo {
    abstract LogInfo logInfo();
    public abstract String message();
    public String packageName() {return logInfo().packageName();}
    public String className() {return logInfo().className();}
    public String methodName() {return logInfo().methodName();}
    public int lineNumber() {return logInfo().lineNumber();}
}
