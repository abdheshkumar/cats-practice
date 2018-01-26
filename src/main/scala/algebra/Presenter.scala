package algebra

import freestyle.free.free
@free trait Presenter {
  def show(id: Int): FS[Int]
}

@free trait Validation {
  def minSize(s: String, n: Int): FS[Boolean]
  def hasNumber(s: String): FS[Boolean]
}

@free trait Interaction {
  def tell(msg: String): FS[Unit]
  def ask(prompt: String): FS[String]
}
