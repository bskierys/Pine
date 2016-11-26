package com.github.bskierys.pine;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PackageNameHelperTest {
    private LinkedHashMap<String, String> packageReplacePatterns;
    private PackageNameHelper helper;

    @Before public void setUp() throws Exception {
        packageReplacePatterns = new LinkedHashMap<>();
        helper = new PackageNameHelper(packageReplacePatterns);
    }

    @Test public void testFindPackage() throws Exception {
        packageReplacePatterns.put("com.example.package", "REP");

        assertTrue(helper.containsReplacePackage("com.example.package.ui.main.presenter"));
    }

    @Test public void testNotFindPackage() throws Exception {
        packageReplacePatterns.put("com.example.package", "REP");

        assertFalse(helper.containsReplacePackage("com.kalafior.package.ui.main.presenter"));
    }

    @Test public void testFindSecondPackage() throws Exception {
        packageReplacePatterns.put("com.example.package", "REP");
        packageReplacePatterns.put("com.kalafior.package", "KEP");

        assertTrue(helper.containsReplacePackage("com.kalafior.package.ui.main.presenter"));
    }

    @Test public void testFindSimilarPackage() throws Exception {
        packageReplacePatterns.put("com.example.package", "REP");
        packageReplacePatterns.put("com.example.package.sample", "KEP");

        assertTrue(helper.containsReplacePackage("com.example.package.sample.ui.main.presenter"));
    }

    @Test public void testReplacePackageWithPattern() throws Exception {
        packageReplacePatterns.put("com.example.package", "REP");

        String expected = "{$0$}.sample.ui.main.presenter";
        String actual = helper.replacePackageWithPattern("com.example.package.sample.ui.main.presenter");

        assertEquals(expected, actual);
    }

    @Test public void testReplacePackageWithSecondPattern() throws Exception {
        packageReplacePatterns.put("com.example.package", "REP");
        packageReplacePatterns.put("com.kalafior.package", "KEP");

        String expected = "{$1$}.sample.ui.main.presenter";
        String actual = helper.replacePackageWithPattern("com.kalafior.package.sample.ui.main.presenter");

        assertEquals(expected, actual);
    }

    @Test public void testNotReplacePackageWithPattern() throws Exception {
        packageReplacePatterns.put("com.example.package", "REP");

        String expected = "com.kalafior.package.sample.ui.main.presenter";
        String actual = helper.replacePackageWithPattern("com.kalafior.package.sample.ui.main.presenter");

        assertEquals(expected, actual);
    }

    @Test public void testReplacePackageWithPatternSimilar() throws Exception {
        packageReplacePatterns.put("com.example.package", "REP");
        packageReplacePatterns.put("com.example.package.sample", "KEP");

        String expected = "{$1$}.ui.main.presenter";
        String actual = helper.replacePackageWithPattern("com.example.package.sample.ui.main.presenter");

        assertEquals(expected, actual);
    }

    @Test public void testReplaceWithReplacementFirstAndOnly() throws Exception {
        packageReplacePatterns.put("com.example.package", "REP");

        String expected = "REP.ui.main.presenter";
        String actual = helper.replacePatternWithReplacement("{$0$}.ui.main.presenter");

        assertEquals(expected, actual);
    }

    @Test public void testReplaceWithReplacementSecondOfMany() throws Exception {
        packageReplacePatterns.put("com.example.package", "REP");
        packageReplacePatterns.put("com.kalafior.package", "KEP");
        packageReplacePatterns.put("com.marynata.package", "MEP");

        String expected = "KEP.ui.main.presenter";
        String actual = helper.replacePatternWithReplacement("{$1$}.ui.main.presenter");

        assertEquals(expected, actual);
    }

    @Test public void testReplaceWithReplacementNotFound() throws Exception {
        packageReplacePatterns.put("com.example.package", "REP");
        packageReplacePatterns.put("com.kalafior.package", "KEP");

        String expected = "{$4$}.ui.main.presenter";
        String actual = helper.replacePatternWithReplacement("{$4$}.ui.main.presenter");

        assertEquals(expected, actual);
    }

    @Test public void testMatchBeginningNoMatch() throws Exception {
        assertEquals(0, helper.matchBeginning("org.apache.common", "com.example.package.ui.main.presenter"));
    }

    @Test public void testMatchBeginningShortMatch() throws Exception {
        assertEquals(19, helper.matchBeginning("com.example.package", "com.example.package.ui.main.presenter"));
    }

    @Test public void testMatchBeginningLongMatch() throws Exception {
        assertEquals(20, helper.matchBeginning("com.example.package.sample", "com.example.package.ui.main.presenter"));
    }

    @Test public void testMatchBeginningInTheMiddle() throws Exception {
        assertEquals(0, helper.matchBeginning("com.example.package", "org.pl.example.package.ui.main.presenter"));
    }
}