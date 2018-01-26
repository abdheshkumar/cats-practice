import Dependencies._

lazy val root = project.in(file("."))
  .settings(name := "cats-practice",
    version := "1.0",
    scalaVersion := "2.12.4",
    scalacOptions += "-Ypartial-unification",
<<<<<<< HEAD
    libraryDependencies ++= circeLibrary ++ `akka-http` ++ freestyle ++ scalaTest ++ `cats-effect` ++ cats ++ elastic4s,
    addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full))
=======
    libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.0-RC2",
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4"))
>>>>>>> 217adf836cd6cdd53e6878c19056f17f3bd0d376
