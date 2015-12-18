import bintray.Keys._

name := "sbt-jni"

organization := "com.github.joprice"

sbtPlugin := true

scalaVersion := "2.10.5"

bintraySettings

bintrayResolverSettings

bintrayOrganization in bintray := Some("joprice")

repository in  bintray := "maven"

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

