import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
trait SharableSpec extends Matchers {
  self: AnyFlatSpec =>

  def nonEmpty(string: String) = {
    string should "be non-empty" in {
      string should not be empty
    }
  }
}

class SharingTests extends AnyFlatSpec with SharableSpec {

  it should behave like nonEmpty("hello")
  it should behave like nonEmpty("hello test")
}
