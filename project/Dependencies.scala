import sbt.Keys.resolvers
import sbt._

object Dependencies {
  lazy val circeVersion         = "0.13.0"
  lazy val freesV               = "0.8.2"
  val elastic4sVersion          = "7.17.4"
  val http4sVersion             = "0.21.0-M4"
  val alpakkaV                  = "5.0.0"
  val meowMtl                   = "0.3.0-M1"
  val AwsSdkVersion             = "1.12.331"
  val quillV                    = "4.6.0"
  private val catsVersion       = "2.2.0"
  private val catsEffectVersion = "2.5.5"
  val scalazVersion             = "7.2.26"

  val protobuf = "com.thesamet.scalapb" %% "scalapb-runtime" % "0.11.12-tmp" % "protobuf"

  lazy val circeLibrary = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-generic-extras",
    "io.circe" %% "circe-literal",
    "io.circe" %% "circe-jawn"
  ).map(_ % circeVersion)

  val scalaZ = Seq(
    "org.scalaz" %% "scalaz-core" % "7.3.6"
  )

  lazy val `akka-http` = Seq(
    "com.typesafe.akka" %% "akka-http"         % "10.4.0",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.4.0" % Test
  )

  lazy val scalaTest = Seq(
    "org.scalatest"     %% "scalatest"       % "3.2.14"   % Test,
    "org.scalacheck"    %% "scalacheck"      % "1.17.0"  % Test,
    "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0" % Test
  )

  val kantanCsv =
    Seq("com.nrinaudo" %% "kantan.csv" % "0.7.0", "com.nrinaudo" %% "kantan.csv-generic" % "0.7.0")

  val shapeless = Seq(
    "com.chuusai" %% "shapeless" % "2.3.10"
  )
  val randomData = Seq(
    "com.danielasfregola" %% "random-data-generator" % "2.9"
  )

  val quill = Seq(
    "io.getquill" %% "quill-cassandra" % quillV
  )
  val `meow-mtl` = Seq("com.olegpy" %% "meow-mtl" % meowMtl)
  val http4s = Seq(
    "org.http4s" %% "http4s-dsl"          % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "org.http4s" %% "http4s-circe"        % http4sVersion
  )

  val pureconfig = Seq(
    "com.github.pureconfig" %% "pureconfig" % "0.17.1"
  )

  val refined = Seq(
    "eu.timepit" %% "refined" % "0.10.1"
  )
  val alpakka = Seq(
    "com.amazonaws"      % "aws-java-sdk-core"         % AwsSdkVersion,
    "com.lightbend.akka" %% "akka-stream-alpakka-s3"   % alpakkaV,
    "com.lightbend.akka" %% "akka-stream-alpakka-file" % alpakkaV,
    "com.lightbend.akka" %% "akka-stream-alpakka-csv"  % alpakkaV
  )
  lazy val freestyle = Seq(
    "io.frees" %% "frees-core"    % freesV,
    "io.frees" %% "frees-fetch"   % freesV,
    "io.frees" %% "frees-logging" % freesV,
    "io.frees" %% "frees-effects" % freesV
  )

  val jose4j = Seq("org.bitbucket.b_c" % "jose4j" % "0.9.1")
  val elastic4s = Seq(
    "com.sksamuel.elastic4s" %% "elastic4s-core"          % elastic4sVersion,
    "com.sksamuel.elastic4s" %% "elastic4s-testkit"       % elastic4sVersion % "test",
    "com.sksamuel.elastic4s" %% "elastic4s-json-circe"    % elastic4sVersion,
    "com.sksamuel.elastic4s" %% "elastic4s-client-esjava" % elastic4sVersion
  )

  val cats = Seq("org.typelevel" %% "cats-core", "org.typelevel" %% "cats-kernel")
    .map(_ % catsVersion) ++ Seq("org.typelevel" %% "cats-testkit" % catsVersion % Test)
  val `cats-effect` = Seq("org.typelevel" %% "cats-effect" % catsEffectVersion)
}
