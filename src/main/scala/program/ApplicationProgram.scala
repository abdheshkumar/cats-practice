package program

import freestyle.free._
import freestyle.free.implicits._
import cats.implicits._
import freestyle.free.FreeS

object ApplicationProgram {
  def program(a: String)(
                      implicit app: modules.Application[modules.Application.Op]
  ): FreeS[modules.Application.Op, Unit] = {
    import app._
    for {
      userInput <- interaction.ask(
        s"Give me something with at least 3 chars and a number on it ${a}"
      )
      valid <- (
        validation.minSize(userInput, 3),
        validation.hasNumber(userInput)
      ).mapN(_ && _).freeS
      _ <- if (valid)
        interaction.tell("awesomesauce!")
      else
        interaction.tell(s"$userInput is not valid")
    } yield ()
  }
}
