resolvers += Resolver.sbtPluginRepo("releases")

addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.4")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.8")

resolvers += Resolver.bintrayRepo("sbt", "maven-releases")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

