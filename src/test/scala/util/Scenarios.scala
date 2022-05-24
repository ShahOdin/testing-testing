package util

import munit.Tag

sealed abstract class Scenarios(name: String) extends Tag(name)

object Scenarios {

  //happy paths
  object Simple extends Scenarios("simple")
  //edge cases
  object EdgeCase extends Scenarios("edge")
  //generic simple case
  object Generic extends Scenarios("generic")
  //fully generic case
  object SimulatedQA extends Scenarios("qa")
  //formal laws
  object Laws extends Scenarios("laws")

}
