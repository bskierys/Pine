/*
* author: Bartlomiej Kierys
* date: 2016-11-25
* email: bskierys@gmail.com
*/
package com.github.bskierys.pine;

import com.google.auto.value.AutoValue;

/**
 * <p>Wrapper for information about where message was logged from. Available info:</p> <ul> <li>package name</li>
 * <li>class name</li> <li>method name</li> <li>line number</li> </ul>
 */
@AutoValue
public abstract class LogInfo {
    public abstract String packageName();
    public abstract String className();
    public abstract String methodName();
    public abstract int lineNumber();
}