import sbt._

object Dependencies {
  lazy val circeVersion = "0.9.0"
  lazy val freesV = "0.6.3"
  val elastic4sVersion = "6.1.3"
  val alpakkaV = "0.18"
  val AwsSdkVersion = "1.11.226"
  lazy val circeLibrary = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)
  lazy val `akka-http` = Seq(
    "com.typesafe.akka" %% "akka-http" % "10.0.11",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.0.11" % Test
  )
  lazy val scalaTest = Seq(
    "org.scalatest" %% "scalatest" % "3.0.4" % Test,
    "org.scalacheck" %% "scalacheck" % "1.13.5" % Test
  )

  val kantanCsv = Seq(
    "com.nrinaudo" %% "kantan.csv" % "0.4.0",
    "com.nrinaudo" %% "kantan.csv-generic" % "0.4.0")

  val shapeless = Seq(
    "com.chuusai" %% "shapeless" % "2.3.3"
  )
  val alpakka = Seq(
    "com.amazonaws" % "aws-java-sdk-core" % AwsSdkVersion,
    "com.lightbend.akka" %% "akka-stream-alpakka-s3" % alpakkaV,
    "com.lightbend.akka" %% "akka-stream-alpakka-file" % alpakkaV,
    "com.lightbend.akka" %% "akka-stream-alpakka-csv" % alpakkaV
  )
  lazy val freestyle = Seq(
    "io.frees" %% "frees-core" % freesV,
    "io.frees" %% "frees-fetch" % freesV,
    "io.frees" %% "frees-logging" % freesV,
    "io.frees" %% "frees-effects" % freesV
  )

  val jose4j = Seq("org.bitbucket.b_c" % "jose4j" % "0.6.3")
  val elastic4s = Seq(
    "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion,
    "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sVersion,
    "com.sksamuel.elastic4s" %% "elastic4s-testkit" % elastic4sVersion % "test",
    "com.sksamuel.elastic4s" %% "elastic4s-embedded" % elastic4sVersion % "test"
  )
  val cats = Seq("org.typelevel" %% "cats-core" % "1.0.1" withSources())
  val `cats-effect` = Seq("org.typelevel" %% "cats-effect" % "0.5")
}
