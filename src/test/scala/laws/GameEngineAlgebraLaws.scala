package laws

import cats.effect.kernel.{MonadCancel, Resource}
import cats.laws
import logic.GameEngine
import models.PlayerInput
import cats.syntax.all._
import cats.laws._

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
}
