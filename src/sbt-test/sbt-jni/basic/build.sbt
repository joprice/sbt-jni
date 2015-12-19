
enablePlugins(JniPlugin)

scalaVersion := "2.11.7"

jniNativeClasses := Seq(
  "com.joprice.Basic"
)

jniNativeCompiler := "g++"

jniLibraryName := "basic"

jniLibSuffix := {
  if (sys.props("os.name").toLowerCase.startsWith("mac")) "dylib" else "so"
}

TaskKey[Unit]("check") := {
  val files = jniSourceFiles.value
  require(files.length == 1, "missing source files")
  require(files.head.getName.endsWith("basic.cpp"))
  require((jniBinPath.value / s"lib${jniLibraryName.value}.${jniLibSuffix.value}").exists)
}
