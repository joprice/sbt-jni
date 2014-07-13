sbt-jni
=======

Sbt plugin for projects with jni sources.

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
