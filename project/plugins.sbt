resolvers += Resolver.sbtPluginRepo("releases")
resolvers += Resolver.bintrayRepo("sbt", "maven-releases")

addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.6")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.10")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.0")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
