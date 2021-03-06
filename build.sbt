import Dependencies._

lazy val root = project
  .in(file("."))
  .settings(
    name := "cats-practice",
    version := "1.0",
    scalaVersion := "2.13.3",
    //scalacOptions ++= Seq("-Ypartial-unification"),
    //scalafmtOnCompile := true,
    //scalacOptions ++= ScalaC.options,
    libraryDependencies ++= circeLibrary ++ pureconfig ++ `akka-http` ++ elastic4s ++ `cats-effect` ++ cats ++ scalaTest ++
      jose4j ++ alpakka ++ kantanCsv ++ shapeless ++ randomData ++ quill ++ http4s ++ `meow-mtl` ++ refined ++ scalaZ,
      addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)
  )

resolvers += Resolver.sonatypeRepo("snapshots")
