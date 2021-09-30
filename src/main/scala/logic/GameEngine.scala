package logic

import cats.data.State
import cats.effect.{Ref, Resource, Sync}
import models._
import cats.syntax.all._
import monocle.syntax.all._

trait GameEngine[F[_]] {
  def getGameData: F[GameData]
  def process(playerInput: PlayerInput): F[Unit]
}


object GameEngine {

  private val saveInput: PlayerInput => State[GameData, Unit] = input =>
    State.modify[GameData](_.focus(_.player.history).modify(input :: _))

  //simple game:
  // rules:
  // - ignore movement inputs if at the edge of the grid.
  def v1[F[_]: Sync](gameData: GameData): Resource[F, GameEngine[F]] = Resource.eval(
    Ref.of[F, GameData](gameData).map{ ref =>
      new GameEngine[F] {
        val movePlayer: PlayerInput => State[GameData, Unit] = {
          case PlayerInput.MoveUp => State.modify{
            case gameData if gameData.playerIsAtMaxY => gameData
            case gameData => gameData.focus(_.player.coordinate.y).modify(_ + 1)
          }

          case PlayerInput.MoveDown => State.modify{
            case gameData if gameData.playerIsAtMinY => gameData
            case gameData => gameData.focus(_.player.coordinate.y).modify(_ - 1)
          }

          case PlayerInput.MoveRight => State.modify{
            case gameData if gameData.playerIsAtMaxX => gameData
            case gameData => gameData.focus(_.player.coordinate.x).modify(_ + 1)
          }

          case PlayerInput.MoveLeft => State.modify{
            case gameData if gameData.playerIsAtMinX => gameData
            case gameData => gameData.focus(_.player.coordinate.x).modify(_ - 1)
          }
        }

        val gameData: Ref[F, GameData] = ref

        override def getGameData: F[GameData] = gameData.get

        override def process(playerInput: PlayerInput): F[Unit] = gameData.modifyState(
          for {
            _ <- saveInput(playerInput)
            _ <- movePlayer(playerInput)
          } yield ()
        )
      }
    }
  )

}