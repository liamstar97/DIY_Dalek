val ScalatraVersion = "2.5.4"

// javaOptions += "-Djava.library.path=/usr/lib/jni"

javaOptions += "-Djava.library.path=/Library/Java/JavaVirtualMachines/jdk1.8.0_152.jdk/Contents/Home/lib/ext"

organization := "ai.skaro"

name := "dalekserver"

version := "0.1"

scalaVersion := "2.12.3"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.2.15.v20160210" % "container;compile",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided"
)

enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)
