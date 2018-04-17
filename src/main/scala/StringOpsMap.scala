object StringOpsMap extends App {
  import scala.reflect.runtime.{universe => ru}
  ru.show{ ru.reify{ "abc".map(_.toUpper) }.tree }
  ru.show{ ru.reify{ "123".map(_.toInt) }.tree }
  Predef.StringCanBuildFrom
  Predef.fallbackStringCanBuildFrom[Int]
}
