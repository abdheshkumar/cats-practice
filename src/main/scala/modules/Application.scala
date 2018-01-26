package modules

import algebra.{Interaction, Validation}
import freestyle.free.module

@module trait Application {
  val validation: Validation
  val interaction: Interaction
}