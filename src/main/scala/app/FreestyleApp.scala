package app

import freestyle.free.{FreeSInstances, Interpreters}
import program.ApplicationProgram

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object FreestyleApp extends App {

  import freestyle.free._
  import Implicits._
  import handlers.Handlers._

  ApplicationProgram.program("Test").interpret[Future]
}

object Implicits
    extends Interpreters
    with FreeSInstances
    with cats.syntax.AllSyntax
    with cats.instances.AllInstances
