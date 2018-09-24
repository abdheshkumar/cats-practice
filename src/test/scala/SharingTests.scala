import org.scalatest.{FlatSpec, Matchers}
trait SharableSpec extends Matchers {
  self: FlatSpec =>

  def nonEmpty(string: String) = {
    string should "be non-empty" in {
      string should not be empty
    }
  }
}

class SharingTests extends FlatSpec with SharableSpec {

  it should behave like nonEmpty("hello")
  it should behave like nonEmpty("hello test")
}
