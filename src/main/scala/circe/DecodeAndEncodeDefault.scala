package circe

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.auto._
//import io.circe.generic.auto._
import io.circe._, io.circe.parser._
import io.circe.syntax._
import cats.implicits._

object DecodeAndEncodeDefault extends App {

  implicit val withDefaultsConfig: Configuration = Configuration.default.withDefaults

  val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

  case class Test(key: Option[String])

  val rawJson = """{"key": null}"""

  val json: Json = parse(rawJson).getOrElse(Json.Null)

  println(json.as[Test])
  println(Test(key = None).asJson.printWith(printer))

  case class Students(name: String, classId: String)

  val students = List(
    Students("james1", "1"),
    Students("james2", "1"),
    Students("micky1", "1"),
    Students("micky2", "2")
  )

  val noOfStudentsEachClassStudents = students.groupBy(student => student.classId).map {
    case (id, stdnts) => (id, (stdnts.size, stdnts))
  }

  val r = students.foldMap(st => Map(st.classId -> (1, List(st))))
}
