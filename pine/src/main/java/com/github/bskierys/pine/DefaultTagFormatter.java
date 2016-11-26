/*
* author: Bartlomiej Kierys
* date: 2016-11-25
* email: bskierys@gmail.com
*/
package com.github.bskierys.pine;

/**
 * Default tag formatter. Tag: by default your tag will be the name of your package. Vowels are removed to reduce
 * length of tag. You can use Builder method {@link Pine.Builder#addPackageReplacePattern(String, String)} to replace any
 * package with short phrase (your app name for example)
 */
public class DefaultTagFormatter implements TagFormatter {
    @Override public String format(LogInfo info) {
        StringBuilder tag = new StringBuilder();
        String packageName = info.packageName();
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
