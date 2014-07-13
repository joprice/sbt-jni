
name := "sbt-jni"

organization := "com.github.joprice"

sbtPlugin := true

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "com.eed3si9n" % "sbt-sequential" % "0.1.0" extra(
    "sbtVersion" -> sbtBinaryVersion.value,
    "scalaVersion" -> scalaBinaryVersion.value
  )
)

