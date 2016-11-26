package com.github.bskierys.pine;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PineTest {
    // TODO: 2016-11-19 test builder
    @Test public void testReplacesPackage() throws Exception {
        String packageName = "com.github.bskierys";
        String className = packageName + ".utils.advancedHelpers.SearchMvpViewPresenterHelper";

        Pine tree = new Pine.Builder().addPackageReplacePattern(packageName, "REP").grow();
        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);
        LogInfo info = tree.getLogInfo(element);

        String expectedPackage = "{$0$}.utils.advancedHelpers";
        assertEquals(expectedPackage, info.packageName());
    }

    @Test public void testReplacesLongPackage() throws Exception {
        String packageName = "com.github.bskierys.pine.sample";
        String className = packageName + ".utils.advancedHelpers.SearchMvpViewPresenterHelper";

        Pine tree = new Pine.Builder().addPackageReplacePattern(packageName, "REP").grow();
        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);
        LogInfo info = tree.getLogInfo(element);

        String expectedPackage = "{$0$}.utils.advancedHelpers";
        assertEquals(expectedPackage, info.packageName());
    }

    @Test public void testGetClassName() throws Exception {
        String packageName = "com.github.bskierys.pine.sample";
        String className = "SearchMvpViewPresenterHelper";
        String fullClassName = packageName + ".utils.advancedHelpers." + className;

        Pine tree = new Pine.Builder().addPackageReplacePattern(packageName, "REP").grow();
        StackTraceElement element = new StackTraceElement(fullClassName, "fakeMethod", "fakeFile", 67);
        LogInfo info = tree.getLogInfo(element);

        assertEquals(className, info.className());
    }

    @Test public void testFullReplacePackage() throws Exception {
        String packageName = "com.github.bskierys";
        String className = packageName + ".utils.advancedHelpers.SearchMvpViewPresenterHelper";

        Pine tree = new Pine.Builder().addPackageReplacePattern(packageName, "REP")
                                      .setTagFormatter(new TagFormatter() {
                                          @Override public String format(LogInfo info) {
                                              return info.packageName();
                                          }
                                      }).grow();

        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);

        String expectedTag = "REP.utils.advancedHelpers";
        String actualTag = tree.createStackElementTag(element);
        assertEquals(expectedTag, actualTag);
    }

    @Test public void testFullReplaceLongPackage() throws Exception {
        String packageName = "com.github.bskierys.pine.sample";
        String className = packageName + ".utils.advancedHelpers.SearchMvpViewPresenterHelper";

        Pine tree = new Pine.Builder().addPackageReplacePattern(packageName, "REP")
                                      .setTagFormatter(new TagFormatter() {
                                          @Override public String format(LogInfo info) {
                                              return info.packageName();
                                          }
                                      }).grow();

        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);

        String expectedTag = "REP.utils.advancedHelpers";
        String actualTag = tree.createStackElementTag(element);
        assertEquals(expectedTag, actualTag);
    }

    @Test public void testFullReplaceTwoPackages() throws Exception {
        String packageName1 = "com.example.package";
        String packageName2 = "com.pcim.package";
        String classPackage = ".utils.advancedHelpers";
        String className = ".SearchMvpViewPresenterHelper";
        String fullClassName1 = packageName1 + classPackage + className;
        String fullClassName2 = packageName2 + classPackage+ className;

        Pine tree = new Pine.Builder().addPackageReplacePattern(packageName1, "REP1")
                                      .addPackageReplacePattern(packageName2, "REP2")
                                      .setTagFormatter(new TagFormatter() {
                                          @Override public String format(LogInfo info) {
                                              return info.packageName();
                                          }
                                      }).grow();

        StackTraceElement element1 = new StackTraceElement(fullClassName1, "fakeMethod", "fakeFile", 67);
        StackTraceElement element2 = new StackTraceElement(fullClassName2, "fakeMethod", "fakeFile", 67);

        assertEquals("REP1" + classPackage, tree.createStackElementTag(element1));
        assertEquals("REP2" + classPackage, tree.createStackElementTag(element2));
    }

    @Test public void testFullNoReplacePackage() throws Exception {
        String packageName = "com.github.bskierys";
        String className = packageName + ".utils.advancedHelpers.SearchMvpViewPresenterHelper";

        Pine tree = new Pine.Builder().addPackageReplacePattern("com.example.package", "REP")
                                      .setTagFormatter(new TagFormatter() {
                                          @Override public String format(LogInfo info) {
                                              return info.packageName();
                                          }
                                      }).grow();

        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);

        String expectedTag = packageName + ".utils.advancedHelpers";
        String actualTag = tree.createStackElementTag(element);
        assertEquals(expectedTag, actualTag);
    }

    @Test public void testFullReplaceSimilarPackage() throws Exception {
        String packageName1 = "com.example.package";
        String packageName2 = "com.example.package.example";
        String classPackage = ".utils.advancedHelpers";
        String className = ".SearchMvpViewPresenterHelper";
        String fullClassName1 = packageName1 + classPackage + className;
        String fullClassName2 = packageName2 + classPackage+ className;

        Pine tree = new Pine.Builder().addPackageReplacePattern(packageName1, "REP1")
                                      .addPackageReplacePattern(packageName2, "REP2")
                                      .setTagFormatter(new TagFormatter() {
                                          @Override public String format(LogInfo info) {
                                              return info.packageName();
                                          }
                                      }).grow();

        StackTraceElement element1 = new StackTraceElement(fullClassName1, "fakeMethod", "fakeFile", 67);
        StackTraceElement element2 = new StackTraceElement(fullClassName2, "fakeMethod", "fakeFile", 67);

        assertEquals("REP1" + classPackage, tree.createStackElementTag(element1));
        assertEquals("REP2" + classPackage, tree.createStackElementTag(element2));
    }
}