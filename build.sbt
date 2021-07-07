import sbtrelease.ReleaseStateTransformations._
import xerial.sbt.Sonatype._

name := "sbt-jni"

organization := "io.github.joprice"

enablePlugins(SbtPlugin, ScriptedPlugin)

publishArtifact in Test := false

scalaVersion := "2.12.13"

licenses += ("Apache-2.0", url(
  "http://www.apache.org/licenses/LICENSE-2.0.html"
))

scriptedBufferLog := false

scriptedLaunchOpts ++= Seq(
  "-Xmx2048M",
  "-XX:MaxMetaspaceSize=512M",
  s"-Dplugin.version=${version.value}",
  // passes sbt directory to scripted tests to share caches
  s"-Dsbt.boot.directory=${file(sys.props("user.home")) / ".sbt" / "boot"}"
)

scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-encoding",
  "utf8"
)

addCommandAlias(
  "validate",
  Seq(
    "scripted",
    "scalafmtCheck",
    "scalafmtSbtCheck"
  ).mkString(";", ";", "")
)

addCommandAlias(
  "fmt",
  Seq(
    "scalafmtAll",
    "scalafmtSbt"
  ).mkString(";", ";", "")
)

publishTo := sonatypePublishToBundle.value
pomIncludeRepository := { _ => false }
sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
releasePublishArtifactsAction := PgpKeys.publishSigned.value

sonatypeProjectHosting := Some(
  GitHubHosting("joprice", "sbt-jni", "pricejosephd@gmail.com")
)

developers := List(
  Developer(
    id = "joprice",
    name = "Joseph Price",
    email = "pricejosephd@gmail.com",
    url = url("https://github.com/joprice/")
  )
)

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
