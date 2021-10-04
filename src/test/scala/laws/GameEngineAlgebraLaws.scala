package laws

import cats.effect.kernel.{MonadCancel, Resource}
import cats.laws
import logic.GameEngine
import models.{Coordinate, GameData, PlayerData, PlayerInput}
import cats.syntax.all._
import cats.laws._
import monocle.syntax.all._

trait GameEngineAlgebraLaws[F[_]] {
  implicit def M: MonadCancel[F, Throwable]

  def processSavesUserInput(
      gameEngineResource: Resource[F, GameEngine[F]]
  )(input: PlayerInput): laws.IsEq[F[Set[PlayerInput]]] = {

    val inputProcessed = gameEngineResource.use { engine =>
      for {
        _        <- engine.process(input)
        gameData <- engine.getGameData
        history = gameData.player.history
      } yield history.toSet
    }

    val historyAddedDirectly = gameEngineResource.use(engine =>
      for {
        gameData <- engine.getGameData
        history = gameData.player.history
      } yield (input :: history).toSet
    )

    inputProcessed <-> historyAddedDirectly

  }

  def moveUpIsIgnoredAtMaxY(gameEngineResource: Resource[F, GameEngine[F]]): IsEq[F[PlayerData]] =
    playerInputIsIgnoredAtEdges(gameEngineResource)(
      MovementScenario(
        PlayerInput.MoveUp,
        _.playerIsAtMaxY,
        _.focus(_.y).modify(_ + 1)
      )
    )
  def moveDownIsIgnoredAtMinY(gameEngineResource: Resource[F, GameEngine[F]]): IsEq[F[PlayerData]] =
    playerInputIsIgnoredAtEdges(gameEngineResource)(
      MovementScenario(
        PlayerInput.MoveDown,
        _.playerIsAtMinY,
        _.focus(_.y).modify(_ - 1)
      )
    )
  def moveRightIsIgnoredAtMaxX(gameEngineResource: Resource[F, GameEngine[F]]): IsEq[F[PlayerData]] =
    playerInputIsIgnoredAtEdges(gameEngineResource)(
      MovementScenario(
        PlayerInput.MoveRight,
        _.playerIsAtMaxX,
        _.focus(_.x).modify(_ + 1)
      )
    )
  def moveLeftIsIgnoredAtMinX(gameEngineResource: Resource[F, GameEngine[F]]): IsEq[F[PlayerData]] =
    playerInputIsIgnoredAtEdges(gameEngineResource)(
      MovementScenario(
        PlayerInput.MoveLeft,
        _.playerIsAtMinX,
        _.focus(_.x).modify(_ - 1)
      )
    )

  private case class MovementScenario(
      playerInput: PlayerInput,
      predicate: GameData => Boolean,
      coordsChange: Coordinate => Coordinate
  )

  private def playerInputIsIgnoredAtEdges(
      gameEngineResource: Resource[F, GameEngine[F]]
  )(movementScenario: MovementScenario): IsEq[F[PlayerData]] = {
    val expectedResult = gameEngineResource.use { engine =>
      for {
        gameData <- engine.getGameData
        coordsBefore  = gameData.player.coordinate
        historyBefore = gameData.player.history
        coordsAfter = if (movementScenario.predicate(gameData)) coordsBefore
        else movementScenario.coordsChange(coordsBefore)
        historyAfter = movementScenario.playerInput :: historyBefore
      } yield PlayerData(
        coordinate = coordsAfter,
        history = historyAfter
      )
    }
    val result = gameEngineResource.use { engine =>
      for {
        _          <- engine.process(movementScenario.playerInput)
        playerData <- engine.getGameData.map(_.player)
      } yield playerData
    }

    expectedResult <-> result
  }
}
