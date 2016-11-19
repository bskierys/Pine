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
 * <p>Replacement for {@link timber.log.Timber.DebugTree} that constructs tags in way that can be easy searched within
 * android monitor.</p> <ul> <li>Default tag pattern for your package: {@link #packageTag}.{package name without base
 * package. Vowels are removed to match tag length limit}.</li> <li>Default log message: {class name}, {method name},
 * {line number} ---&gt; {message}. </li> <li>Default tag pattern for other packages: {package name without base
 * package. Vowels are removed to match tag length limit}. </li> </ul>
 */
public class Pine extends Timber.DebugTree {
    private static final int CALL_STACK_INDEX = 5;
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");

    private static final String PACKAGE_REPLACEMENT_INDICATOR = "{$$}";

    private String packageName;
    private String packageTag;

    private final TagStrategy tagStrategy;
    private final MessageStrategy messageStrategy;

    private Pine(Builder builder) {
        this.tagStrategy = builder.tagStrategy;
        this.messageStrategy = builder.messageStrategy;

        if (builder.packageReplacePattern != null) {
            this.packageName = builder.packageReplacePattern.first;
            this.packageTag = builder.packageReplacePattern.second;
        }
    }

    public static Pine growDefault() {
        return new Builder().grow();
    }

    @Override protected String createStackElementTag(StackTraceElement element) {
        if (tagStrategy == null) {
            throw new NullPointerException("Tag formatting strategy is null. This should not happen...");
        }

        String tag = tagStrategy.format(getLogInfo(element));

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
        if (messageStrategy == null) {
            throw new NullPointerException("Message formatting strategy is null. This should not happen...");
        }

        String formattedMessage = messageStrategy.format(getMessageInfo(element, message));

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

    @AutoValue
    public static abstract class LogInfo {
        public abstract String packageName();
        public abstract String className();
        public abstract String methodName();
        public abstract int lineNumber();
    }

    @AutoValue
    public static abstract class MessageInfo {
        abstract LogInfo logInfo();
        public abstract String message();

        public String packageName() {return logInfo().packageName();}
        public String className() {return logInfo().className();}
        public String methodName() {return logInfo().methodName();}
        public int lineNumber() {return logInfo().lineNumber();}
    }

    public interface TagStrategy {
        String format(LogInfo info);
    }

    public interface MessageStrategy {
        String format(MessageInfo info);
    }

    public static class Builder {
        private MessageStrategy messageStrategy;
        private TagStrategy tagStrategy;
        private Pair<String, String> packageReplacePattern;

        public Builder setMessageStrategy(MessageStrategy strategy) {
            this.messageStrategy = strategy;
            return this;
        }

        public Builder setTagStrategy(TagStrategy strategy) {
            this.tagStrategy = strategy;
            return this;
        }

        public Builder setPackageReplacePattern(String packageName, String replacement) {
            this.packageReplacePattern = new Pair<>(packageName, replacement);
            return this;
        }

        private void initEmptyFieldsWithDefaultValues() {
            if (messageStrategy == null) {
                messageStrategy = new DefaultMessageStrategy();
            }
            if (tagStrategy == null) {
                tagStrategy = new DefaultTagStrategy();
            }
        }

        public Pine grow() {
            initEmptyFieldsWithDefaultValues();
            return new Pine(this);
        }
    }

    public static class DefaultTagStrategy implements TagStrategy {
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

    public static class DefaultMessageStrategy implements MessageStrategy {
        private static final String DELIMITER = " ---> ";

        @Override public String format(MessageInfo info) {
            return info.logInfo().className()
                    + ", " + info.logInfo().methodName()
                    + ", " + Integer.toString(info.logInfo().lineNumber())
                    + DELIMITER + info.message();
        }
    }
}
