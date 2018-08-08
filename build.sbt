
name := "sbt-jni"

organization := "com.github.joprice"

sbtPlugin := true

publishArtifact in Test := false

scalaVersion := "2.12.4"

bintrayOrganization := Some("joprice")

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

scriptedBufferLog := false

scriptedLaunchOpts ++= Seq("-Xmx2G", "-Dplugin.version=" + version.value)

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8")
