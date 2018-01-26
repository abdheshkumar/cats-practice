import Dependencies._

lazy val root = project.in(file("."))
  .settings(name := "cats-practice",
    version := "1.0",
    scalaVersion := "2.12.4",
    scalacOptions += "-Ypartial-unification",
    libraryDependencies ++= circeLibrary ++ `akka-http` ++ freestyle ++ scalaTest ++ `cats-effect` ++ cats ++ elastic4s,
    addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full))
