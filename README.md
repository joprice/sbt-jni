sbt-jni
=======

Sbt plugin for projects with jni sources.

Install
--------

```scala
resolvers += Resolver.url("joprice maven", url("http://dl.bintray.com/content/joprice/maven"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.github.joprice" % "sbt-jni" % "0.1.0")
```

Usage
--------

Put c/cpp sources in `nativeSource` (`src/main/native`).

```scala
import com.github.joprice.Jni
import Jni.Keys._

Jni.settings

// this will be the name that you call `System.loadLibrary` with, prefixed with "lib"
libraryName := "libMyApp"

gccFlags ++= Seq("-lpthread")

// defaults to gcc
nativeCompiler := "g++"

jniClasses := Seq(
  "com.myapp.ClassWithJniCode"
)
```

Headers generated for jni classes will end up in `headersPath` (`target/native/include`). 

The final library will be in `binPath` (`target/native/bin`).






