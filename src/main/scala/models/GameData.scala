package models

final case class GameData(grid: Grid, player: PlayerData) {
  def playerIsAtMaxX: Boolean = grid.maxX == player.coordinate.x
  def playerIsAtMinX: Boolean = grid.minX == player.coordinate.x
  def playerIsAtMaxY: Boolean = grid.maxY == player.coordinate.y
  def playerIsAtMinY: Boolean = grid.minY == player.coordinate.y
}
