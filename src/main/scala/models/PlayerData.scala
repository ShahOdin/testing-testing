package models

final case class PlayerData(coordinate: Coordinate, history: List[PlayerInput])

object PlayerData {
  val empty: PlayerData = PlayerData(coordinate = Coordinate.empty, history = Nil)
}
