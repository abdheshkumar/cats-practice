import Dependencies._

lazy val macroAnnotationSettings = Seq(
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full),
  scalacOptions += "-Xplugin-require:macroparadise",
  scalacOptions in(Compile, console) ~= (_ filterNot (_ contains "paradise")) // macroparadise plugin doesn't work in repl yet.
)

lazy val root = project.in(file("."))
  .settings(
    name := "cats-practice",
    version := "1.0",
    scalaVersion := "2.12.4",
    scalacOptions ++= ScalaC.options,
    libraryDependencies ++= circeLibrary ++ `akka-http` ++ freestyle ++ scalaTest ++ `cats-effect` ++ cats ++ elastic4s ++ jose4j,
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4"))
  .settings(macroAnnotationSettings)