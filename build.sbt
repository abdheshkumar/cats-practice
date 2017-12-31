lazy val root = project.in(file("."))
  .settings(name := "cats-practice",
    version := "1.0",
    scalaVersion := "2.12.4",
    scalacOptions += "-Ypartial-unification",
    libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.0-RC2",
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4"))
