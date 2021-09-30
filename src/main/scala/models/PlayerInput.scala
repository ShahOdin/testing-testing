package models

import enumeratum.EnumEntry
import enumeratum._

sealed trait PlayerInput extends EnumEntry

object PlayerInput extends Enum[PlayerInput] {

  case object MoveUp    extends PlayerInput
  case object MoveDown  extends PlayerInput
  case object MoveRight extends PlayerInput
  case object MoveLeft  extends PlayerInput

  override val values: IndexedSeq[PlayerInput] = findValues
}
