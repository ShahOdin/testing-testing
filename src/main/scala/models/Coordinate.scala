package models

final case class Coordinate(x: Int, y: Int)

object Coordinate {
  val empty: Coordinate = Coordinate(0, 0)
}
