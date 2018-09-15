import Dependencies._

lazy val macroAnnotationSettings = Seq(
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M11" cross CrossVersion.full),
  scalacOptions += "-Xplugin-require:macroparadise",
  scalacOptions in (Compile, console) ~= (_ filterNot (_ contains "paradise")) // macroparadise plugin doesn't work in repl yet.
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "cats-practice",
    version := "1.0",
    scalaVersion := "2.12.6",
    scalacOptions ++= Seq("-Ypartial-unification"),
    scalafmtOnCompile := true,
    //scalacOptions ++= ScalaC.options,
    libraryDependencies ++= circeLibrary ++ `akka-http` ++ freestyle ++ scalaTest ++
      `cats-effect` ++ cats ++ elastic4s ++ jose4j ++ alpakka ++ kantanCsv ++ shapeless ++ randomData ++ quill ++
      http4s ++ `meow-mtl` ++ pureconfig,
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full))
  .settings(macroAnnotationSettings)