import Dependencies._

lazy val root = project
  .in(file("."))
  .settings(
    name := "cats-practice",
    version := "1.0",
    scalaVersion := "2.13.12",
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full),
    libraryDependencies ++= circeLibrary ++ pureconfig ++ `cats-effect` ++ cats ++ scalaTest ++
      kantanCsv ++ shapeless ++ randomData ++ http4s ++ refined
  )
