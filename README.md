#Pine
=====================
[![CircleCI](https://circleci.com/gh/bskierys/Pine/tree/master.svg?style=svg)](https://circleci.com/gh/bskierys/Pine/tree/master)
[ ![Download](https://api.bintray.com/packages/bskierys/Maven/Pine/images/download.svg) ](https://bintray.com/bskierys/Maven/Pine/_latestVersion)

![Pine logo](https://github.com/bskierys/Pine/blob/master/pine-sample/design/github-logo.png)

_Remember, the storm is a good opportunity for the pine and the cypress to show their strength and their stability._   
_**Ho Chi Minh**_


This is a tree for a great logger library [Timber](https://github.com/JakeWharton/timber) by Jake Wharton.

**Pine** is extension for default Timber DebugTree that logs class, method, and line number where it was invoked to help the developer locate log source easily.

Just like Jake Wharton I was tired of copying this one class to every single project, so I made it a library.

##Basic usage
----------------------
Pine is `Timber.Tree` implementation. Just plant it to Timber.

```java
Timber.plant(Pine.growDefault());
```
Default implementation tags logs with package name for class that called `Timber`. See section [Log format](#log-format) for more information.

##Replacing package names
----------------------
If you are tired of seeing full package of your app in every log, you can easy replace it with short word. Use `Pine.Builder`.

```java
Pine pineWithReplace = new Pine.Builder()
                .addPackageReplacePattern(getPackageName(), "PINE")
                .addPackageReplacePattern("com.github.simonpercic.oklog", "OKLOG")
                .grow();

// plant a pine at the beginning of application lifecycle
if (BuildConfig.DEBUG) {
    Timber.plant(pineWithReplace);
}
```

Your logs will start looking like this:
```
D/PINE.tls.dvncd.hlprs: MainActivity, lambda$onCreate$0, 51 ---> Debug message
```
Instead of this
```
D/cm.gthb.bskrs.smpl.tls.dvncd.hlprs: MainActivity, lambda$onCreate$0, 51 ---> Debug message
```

You can replace as many packages as you like. Or you can provide different phrases for different submodules of your app.

##Advanced usage
----------------------

If you like you can override default tag and message formatting using `setTagFormatter` and `setMessageFormatter` in `Pine.Builder` class. To format your tag or message you can use all the info available in `LogInfo` class.
Example of custom Pine planted in application class:

```java
Pine customPine = new Pine.Builder()
                .addPackageReplacePattern(getPackageName(), "PINE")
                .setTagFormatter(LogInfo::packageName)
                .setMessageFormatter(info -> {
                    return String.format(Locale.getDefault(), "%s#%s, %d ----> %s", info.className(),
                                         info.methodName(), info.lineNumber(), info.message());
                })
                .grow();
```

##Installation
----------------------

Available from jCenter.

Gradle:

```groovy
dependencies {
    compile 'com.github.bskierys.pine:pine:0.2.0'
}
```

##Log format

**Pine** logs consists of two sections. 

 * **TAG**: is basically a package of class that invoked Timber. It is however formatted to reduce tag length (see [this stackoverflow thread](http://stackoverflow.com/questions/28168622/the-logging-tag-can-be-at-most-23-characters). Your main package is replaced by tag of your choosing, and the rest of package is stripped out of vowels. 
 * **Body**: is not only a message. It is also name of the class, of the method and line number where log was invoked. Very handy when you have pretty heavy logged classes.
 
**Example log:**
```
D/PINE.tls.dvncd.hlprs: MainActivity, lambda$onCreate$0, 51 ---> Debug message
```
This means your log was invoked in `com.github.bskierys.pine.utis.advancedHelpers.MainActivity#onCreate` method (line 51). My package `com.github.bskierys.pine` was replaced with `PINE`.
See sample app for more examples.

##License
-------

    Copyright 2016 Bartłomiej Kierys

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
