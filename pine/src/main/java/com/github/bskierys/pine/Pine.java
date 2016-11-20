/*
* author: Bartlomiej Kierys
* date: 2016-11-09
* email: bskierys@gmail.com
*/
package com.github.bskierys.pine;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.google.auto.value.AutoValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
 * <p>Replacement for {@link timber.log.Timber.DebugTree} logs all info about where message was logged from.</p> <p>Use
 * {@link #growDefault()} to use default implementation or use {@link Builder} to customize.</p> <p>Default
 * implementation:</p> <ul> <li>Tag: by default your tag will be the name of your package. Vowels are removed to reduce
 * length of tag. You can use Builder method {@link Builder#setPackageReplacePattern(String, String)} to replace any
 * package with short phrase (your app name for example)</li> <li>Message: {class name}, {method name}, {line number}
 * ---&gt; {message}. </li> </ul>
 */
public class Pine extends Timber.DebugTree {
    private static final int CALL_STACK_INDEX = 5;
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");

    private static final String PACKAGE_REPLACEMENT_INDICATOR = "{$$}";

    private String packageName;
    private String packageTag;
    private final TagFormatter tagFormatter;
    private final MessageFormatter messageFormatter;

    private Pine(Builder builder) {
        this.tagFormatter = builder.tagFormatter;
        this.messageFormatter = builder.messageFormatter;

        if (builder.packageReplacePattern != null) {
            this.packageName = builder.packageReplacePattern.first;
            this.packageTag = builder.packageReplacePattern.second;
        }
    }

    /**
     * <p>Grows default implementation of pine.</p> <ul> <li>Tag: by default your tag will be the name of your package.
     * Vowels are removed to reduce length of tag</li> <li>Message: {class name}, {method name}, {line number} ---&gt;
     * {message}. </li> </ul>
     *
     * @return Default {@link Pine} implementation
     */
    public static Pine growDefault() {
        return new Builder().grow();
    }

    @Override protected String createStackElementTag(StackTraceElement element) {
        if (tagFormatter == null) {
            throw new NullPointerException("Tag formatting strategy is null. This should not happen...");
        }

        String tag = tagFormatter.format(getLogInfo(element));

        return applyPackageReplacing(tag);
    }

    private String applyPackageReplacing(String tag) {
        if (tag.contains(PACKAGE_REPLACEMENT_INDICATOR)) {
            tag = tag.replace(PACKAGE_REPLACEMENT_INDICATOR, packageTag);
        }

        return tag;
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
        if (messageFormatter == null) {
            throw new NullPointerException("Message formatting strategy is null. This should not happen...");
        }

        String formattedMessage = messageFormatter.format(getMessageInfo(element, message));

        super.log(priority, tag, applyPackageReplacing(formattedMessage), t);
    }

    private MessageInfo getMessageInfo(StackTraceElement element, String message) {
        return new AutoValue_Pine_MessageInfo(getLogInfo(element), message);
    }

    LogInfo getLogInfo(StackTraceElement element) {
        String packageName = getPackageName(element);
        if (this.packageName != null && packageName.contains(this.packageName)) {
            packageName = packageName.replace(this.packageName, PACKAGE_REPLACEMENT_INDICATOR);
        }

        String className = getClassName(element);
        String methodName = element.getMethodName();
        int lineNumber = element.getLineNumber();

        return new AutoValue_Pine_LogInfo(packageName, className, methodName, lineNumber);
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
        return getFullClassName(element).substring(0, className.lastIndexOf('.'));
    }

    @NonNull private String getClassName(StackTraceElement element) {
        String className = getFullClassName(element);
        return className.substring(className.lastIndexOf('.') + 1, className.length());
    }

    /**
     * <p>Wrapper for information about where message was logged from. Available info:</p> <ul> <li>package name</li>
     * <li>class name</li> <li>method name</li> <li>line number</li> </ul>
     */
    @AutoValue
    public static abstract class LogInfo {
        public abstract String packageName();
        public abstract String className();
        public abstract String methodName();
        public abstract int lineNumber();
    }

    /**
     * <p>Wrapper for information about where message was logged from. Available info:</p> <ul> <li>all available in
     * {@link LogInfo}</li> <li>actual message</li> </ul>
     */
    @AutoValue
    public static abstract class MessageInfo {
        abstract LogInfo logInfo();
        public abstract String message();

        public String packageName() {return logInfo().packageName();}

        public String className() {return logInfo().className();}

        public String methodName() {return logInfo().methodName();}

        public int lineNumber() {return logInfo().lineNumber();}
    }

    /**
     * Interface to format log tag the way you like. When formatting you can use all the data from {@link LogInfo}
     */
    public interface TagFormatter {
        String format(LogInfo info);
    }

    /**
     * Interface to format log message the way you like. When formatting you can use all the data from {@link
     * MessageInfo}
     */
    public interface MessageFormatter {
        String format(MessageInfo info);
    }

    /**
     * Builder for {@link Pine}
     */
    public static class Builder {
        private MessageFormatter messageFormatter;
        private TagFormatter tagFormatter;
        private Pair<String, String> packageReplacePattern;

        /**
         * You can format message you will see in monitor the way you like. When formatting you can use all the data
         * from {@link MessageInfo}. If not set {@link DefaultMessageFormatter} will be used.
         */
        public Builder setMessageFormatter(MessageFormatter formatter) {
            this.messageFormatter = formatter;
            return this;
        }

        /**
         * You can format tag you will see in monitor the way you like. When formatting you can use all the data from
         * {@link LogInfo}. If not set {@link DefaultTagFormatter} will be used.
         */
        public Builder setTagFormatter(TagFormatter formatter) {
            this.tagFormatter = formatter;
            return this;
        }

        /**
         * Replace package name with short phrase.
         *
         * @param packageName Package name to be replaced
         * @param replacement Phrase to replace package with
         */
        public Builder setPackageReplacePattern(String packageName, String replacement) {
            this.packageReplacePattern = new Pair<>(packageName, replacement);
            return this;
        }

        private void initEmptyFieldsWithDefaultValues() {
            if (messageFormatter == null) {
                messageFormatter = new DefaultMessageFormatter();
            }
            if (tagFormatter == null) {
                tagFormatter = new DefaultTagFormatter();
            }
        }

        /**
         * Builds {@link Pine} object
         */
        public Pine grow() {
            initEmptyFieldsWithDefaultValues();
            return new Pine(this);
        }
    }

    /**
     * Default tag formatter. Tag: by default your tag will be the name of your package. Vowels are removed to reduce
     * length of tag. You can use Builder method {@link Builder#setPackageReplacePattern(String, String)} to replace any
     * package with short phrase (your app name for example)
     */
    public static class DefaultTagFormatter implements TagFormatter {
        @Override public String format(LogInfo info) {
            StringBuilder tag = new StringBuilder();

            String packageName = info.packageName();
            if (packageName.contains(PACKAGE_REPLACEMENT_INDICATOR)) {
                packageName = packageName.replace(PACKAGE_REPLACEMENT_INDICATOR + ".", "");
                tag.append(PACKAGE_REPLACEMENT_INDICATOR);
                tag.append(".");
            }

            String[] path = packageName.split("\\.");

            for (int i = 0; i < path.length; i++) {
                String pathElement = path[i];
                String newElement = removeVowels(pathElement);

                if (i != 0) {
                    tag.append(".");
                }

                if (newElement.equals("")) {
                    // do not remove vowels if that removes whole name
                    tag.append(pathElement);
                } else {
                    tag.append(newElement);
                }
            }

            return tag.toString();
        }

        private String removeVowels(String original) {
            return original.replaceAll("[AEIOUYaeiouy]", "");
        }
    }

    /**
     * Default message formatter. Message: {class name}, {method name}, {line number} ---&gt; {message}.
     */
    public static class DefaultMessageFormatter implements MessageFormatter {
        private static final String DELIMITER = " ---> ";

        @Override public String format(MessageInfo info) {
            return info.logInfo().className()
                    + ", " + info.logInfo().methodName()
                    + ", " + Integer.toString(info.logInfo().lineNumber())
                    + DELIMITER + info.message();
        }
    }
}
