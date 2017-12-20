
val circeVersion = "0.8.0"

val circeLibrary = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

lazy val root = project.in(file("."))
  .settings(name := "cats-practice",
    version := "1.0",
    scalaVersion := "2.12.4",
    scalacOptions += "-Ypartial-unification",
    libraryDependencies ++= circeLibrary)
