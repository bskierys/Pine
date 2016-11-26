/*
* author: Bartlomiej Kierys
* date: 2016-11-25
* email: bskierys@gmail.com
*/
package com.github.bskierys.pine;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to replace package names with abbreviated replacement.
 */
class PackageNameHelper {
    private final static String PACKAGE_REPLACEMENT_FORMAT = "{$%d$}";
    private final static Pattern PACKAGE_REPLACEMENT_PATTERN = Pattern.compile("\\{\\$(\\d+)\\$\\}");

    private final LinkedHashMap<String, String> packageReplacePatterns;

    PackageNameHelper(LinkedHashMap<String, String> packageReplacePatterns) {
        this.packageReplacePatterns = packageReplacePatterns;
    }

    /**
     * Checks whether provided string contains one of replaced packages.
     */
    boolean containsReplacePackage(String packageName) {
        for (String key : packageReplacePatterns.keySet()) {
            if (packageName.contains(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Replaces package from package name with pattern marking which package was used. Finish whole process with {@link
     * #replacePatternWithReplacement(String)} method.
     */
    String replacePackageWithPattern(String packageName) {
        int bestMatchIndex = -1;
        int bestMatchLength = 0;
        String bestMatchKey = null;

        int currentIndex = 0;
        for (String key : packageReplacePatterns.keySet()) {
            int matching = matchBeginning(key, packageName);
            if (matching == key.length()) {
                if (bestMatchLength < matching) {
                    bestMatchLength = matching;
                    bestMatchIndex = currentIndex;
                    bestMatchKey = key;
                }
            }

            currentIndex++;
        }

        if (bestMatchKey != null) {
            String replaceFormat = String.format(Locale.getDefault(), PACKAGE_REPLACEMENT_FORMAT, bestMatchIndex);
            packageName = packageName.replace(bestMatchKey, replaceFormat);
        }

        return packageName;
    }

    /**
     * Replaces pattern produced in {@link #replacePackageWithPattern(String)} method with proper replacement.
     */
    String replacePatternWithReplacement(String packageName) {
        Matcher m = PACKAGE_REPLACEMENT_PATTERN.matcher(packageName);

        int keyNumber = -1;
        while (m.find()) {
            String s = m.group(1);
            keyNumber = Integer.parseInt(s);
        }

        String replacement = null;
        int currentIndex = 0;
        for (Map.Entry<String, String> entry : packageReplacePatterns.entrySet()) {
            if (keyNumber == currentIndex) {
                replacement = entry.getValue();
            }

            currentIndex++;
        }

        if (keyNumber != -1 && replacement != null) {
            String replaceFormat = String.format(Locale.getDefault(), PACKAGE_REPLACEMENT_FORMAT, keyNumber);
            packageName = packageName.replace(replaceFormat, replacement);
        }

        return packageName;
    }

    /**
     * Checks if string is the beginning of another.
     *
     * @param beginning String to find at the beginning
     * @param whole String to look in
     * @return number of matching letters between one string and another. Beginning is fully matching if number of
     * matching letters correspond to length of first string
     */
    int matchBeginning(String beginning, String whole) {
        int matchingIndex = 0;
        while (matchingIndex < beginning.length() &&
                beginning.charAt(matchingIndex) == whole.charAt(matchingIndex)) {
            matchingIndex++;
        }

        return matchingIndex;
    }
}
