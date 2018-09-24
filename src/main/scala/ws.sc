val str =
  """
    |a{
    |0:[{
    |b: "hello"
    |}]
    |}
  """.stripMargin
import com.typesafe.config.{ConfigFactory, ConfigList, ConfigValue, ConfigValueFactory}

import scala.collection.JavaConverters._
import scala.collection.mutable
val result: mutable.Map[String, ConfigValue] = ConfigFactory
  .parseString(str)
  .getObject("a")
  .asScala

case class A(b: String)
import pureconfig._
result.map {
  case (k, v) =>
    v match {
      case aa: ConfigList =>
       val configObject = ConfigValueFactory.fromMap(Map(k->aa).asJava)
        println(configObject)
/*loadConfig[(Int,List[A])](configObject.toConfig)*/
      case a =>
        println(a)
    }
}
