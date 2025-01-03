import sbt._

object Dependencies {
  lazy val circeVersion         = "0.14.10"
  val http4sVersion             = "0.23.30"
  private val catsVersion       = "2.12.0"
  private val catsEffectVersion = "3.6-623178c"

  lazy val circeLibrary = Seq(
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "io.circe" %% "circe-generic-extras" % "0.14.4",
    "io.circe" %% "circe-literal" % circeVersion,
    "io.circe" %% "circe-jawn" % circeVersion
  )


  lazy val scalaTest = Seq(
    "org.scalatest"     %% "scalatest"       % "3.2.19"   % Test,
    "org.scalacheck"    %% "scalacheck"      % "1.18.1"  % Test,
    "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0" % Test
  )

  val kantanCsv =
    Seq("com.nrinaudo" %% "kantan.csv" % "0.7.0", "com.nrinaudo" %% "kantan.csv-generic" % "0.7.0")

  val shapeless = Seq(
    "com.chuusai" %% "shapeless" % "2.3.12"
  )
  val randomData = Seq(
    "com.danielasfregola" %% "random-data-generator" % "2.9"
  )

  val http4s = Seq(
    "org.http4s" %% "http4s-dsl"          % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % "0.23.17",
    "org.http4s" %% "http4s-blaze-client" % "0.23.17",
    "org.http4s" %% "http4s-circe"        % http4sVersion
  )

  val pureconfig = Seq(
    "com.github.pureconfig" %% "pureconfig" % "0.17.8"
  )

  val refined = Seq(
    "eu.timepit" %% "refined" % "0.11.3"
  )

  val cats = Seq("org.typelevel" %% "cats-core" % catsVersion, "org.typelevel" %% "cats-kernel" % catsVersion) ++ Seq("org.typelevel" %% "cats-testkit" % catsVersion % Test)
  val `cats-effect` = Seq("org.typelevel" %% "cats-effect" % catsEffectVersion)
}
