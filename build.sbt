
name := "sbt-jni"

organization := "com.github.joprice"

sbtPlugin := true

publishArtifact in Test := false

scalaVersion := "2.10.5"

bintrayOrganization := Some("joprice")

bintrayRepository in bintray := "maven"

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

ScriptedPlugin.scriptedSettings

scriptedSettings

scriptedBufferLog := false

scriptedLaunchOpts ++= Seq("-Xmx2G", "-Dplugin.version=" + version.value)

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8")

