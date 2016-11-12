/*
* author: Bartlomiej Kierys
* date: 2016-11-09
* email: bskierys@gmail.com
*/
package com.github.bskierys.pine;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
 * Replacement for {@link timber.log.Timber.DebugTree} that constructs tags in way that can be easy searched within
 * android monitor.
 *
 * Default tag pattern for your package: {@link #packageTag}.{package name without base package. Vowels
 * are removed to match tag length limit}.
 *
 * Default log message: {class name}, {method name}, {line number} --->
 * {message}
 *
 * Default tag pattern for other packages: {package name without base package. Vowels are removed to match tag
 * length limit}.
 */
public class Pine extends Timber.DebugTree {
    private static final int CALL_STACK_INDEX = 5;
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");
    private static final String DELIMITER = " ---> ";

    private String packageName;
    private String packageTag;

    public Pine(Context context, String packageTag) {
        this.packageTag = packageTag;
        this.packageName = context.getPackageName();
    }

    @Override protected String createStackElementTag(StackTraceElement element) {
        StringBuilder tag = new StringBuilder();

        String packageName = getPackageName(element);
        if (packageName.contains(this.packageName)) {
            packageName = packageName.replace(this.packageName + ".", "");
            tag.append(packageTag);
            tag.append(".");
        }

        String[] path = packageName.split("\\.");

        for (int i = 0; i < path.length; i++) {
            String pathElement = path[i];
            pathElement = replaceCamelsWithDots(pathElement).toLowerCase();
            String newElement = removeVowels(pathElement);

            if (i != 0) {
                tag.append(".");
            }

            if (newElement.equals("")) {
                tag.append(pathElement);
            } else {
                tag.append(newElement);
            }
        }

        return tag.toString();
    }

    @NonNull private String getFullClassName(StackTraceElement element) {
        String className = element.getClassName();
        Matcher m = ANONYMOUS_CLASS.matcher(className);
        if (m.find()) {
            className = m.replaceAll("");
        }

        return className;
    }

    @NonNull private String getPackageName(StackTraceElement element) {
        String className = getFullClassName(element);
        return getFullClassName(element).substring(0, className.lastIndexOf('.') + 1);
    }

    @NonNull private String getClassName(StackTraceElement element) {
        String className = getFullClassName(element);
        return className.substring(className.lastIndexOf('.') + 1, className.length());
    }

    private String replaceCamelsWithDots(String original) {
        String[] splitByCamel = splitCamelCase(original);
        if (splitByCamel.length > 0) {
            original = "";
            for (String el : splitByCamel) {
                original += el + ".";
            }
            original = original.substring(0, original.length() - 1);
        }
        return original;
    }

    protected String createStackElementMessage(String originalMessage, StackTraceElement element) {
        return getClassName(element)
                + ", " + element.getMethodName()
                + ", " + Integer.toString(element.getLineNumber())
                + DELIMITER + originalMessage;
    }

    private String removeVowels(String original) {
        return original.replaceAll("[AEIOUYaeiouy]", "");
    }

    private String[] splitCamelCase(String original) {
        return original.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    }

    @Override protected void log(int priority, String tag, String message, Throwable t) {
        // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
        // because Robolectric runs them on the JVM but on Android the elements are different.
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length <= CALL_STACK_INDEX) {
            throw new IllegalStateException(
                    "Synthetic stacktrace didn't have enough elements: are you using proguard?");
        }

        StackTraceElement element = stackTrace[CALL_STACK_INDEX];

        super.log(priority, tag, createStackElementMessage(message, element), t);
    }
}
