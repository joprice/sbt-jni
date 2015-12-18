import com.github.joprice.Jni
import Jni.Keys._

Jni.settings

scalaVersion := "2.11.7"

jniClasses := Seq(
  "com.joprice.Basic"
)

libraryName := "basic"

libSuffix := {
  if (sys.props("os.name").toLowerCase.startsWith("mac")) "dylib" else "so"
}

TaskKey[Unit]("check") := {
  val files = jniSourceFiles.value
  require(files.length == 1, "missing source files")
  require(files.head.getName.endsWith("basic.cpp"))
  require((binPath.value / s"lib${libraryName.value}.${libSuffix.value}").exists)
}
