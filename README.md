sbt-jni
=======

[![Circle CI](https://circleci.com/gh/joprice/sbt-jni.svg?style=svg)](https://circleci.com/gh/joprice/sbt-jni)


Sbt plugin for projects with jni sources. 

On `compile`, the `jniCompile` task will be run. It first runs the `jniJavah` task, which generates JNI headers. Headers generated for jni classes will end up in `jniHeadersPath`, which defaults to `target/native/include`. The final library will be in `jniBinPath`, which defaults to `target/native/bin`.

See `src/sbt-test/sbt-jni/basic` for an example project.

Install
--------

```scala
resolvers += Resolver.url("joprice-sbt-plugins", url("http://dl.bintray.com/content/joprice/sbt-plugins"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.github.joprice" % "sbt-jni" % "0.1.1")
```

Testing
--------

Run `scripted` to sbt tests.

Usage
--------

Native sources should be placed in `jniNativeSources`, which defaults to `src/main/native`.

`sbt-jni` is an AutoPlugin, but is must be enabled explicitly.

```scala
enablePlugins(JniPlugin)

// this will be the name that you call `System.loadLibrary` with, prefixed with "lib"
jniLibraryName := "libMyApp"

jniGccFlags ++= Seq("-lpthread")

// defaults to gcc
jniNativeCompiler := "g++"

jniNativeClasses := Seq(
  "com.myapp.ClassWithJniCode"
)
```

