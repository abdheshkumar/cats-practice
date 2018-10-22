import org.scalatest.{FlatSpec, Matchers}
import eu.timepit.refined.W
import eu.timepit.refined.api._
import eu.timepit.refined.auto._
import eu.timepit.refined.boolean._
import eu.timepit.refined.char._
import eu.timepit.refined.collection._
import eu.timepit.refined.generic._
import eu.timepit.refined.string._
import shapeless.Witness

class RefinedCirce extends FlatSpec with Matchers {
  "literal-based singleton types" should "works" in {
    val x: W.`"abc"`.T = "abc"
    type Name = Refined[String, NonEmpty]
    type TwitterHandle = String Refined StartsWith[W.`"@"`.T]
    final case class Developer(name: String Refined NonEmpty, twitterHandle: TwitterHandle)

   def test = Developer("sas","@my")

  }
}
