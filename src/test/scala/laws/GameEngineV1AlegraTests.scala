package laws

import cats.Eq
import cats.effect.MonadCancel
import cats.effect.kernel.Resource
import logic.GameEngine
import models.{PlayerData, PlayerInput}
import org.scalacheck.{Arbitrary, Prop}
import org.typelevel.discipline.Laws
import cats.kernel.laws.discipline._

trait GameEngineV1AlegraTests[F[_]] extends Laws {
  def laws: GameEngineV1AlgebraLaws[F]

  def algebra(
      implicit
      arbInput: Arbitrary[PlayerInput],
      eqFSetInput: Eq[F[Set[PlayerInput]]],
      eqFPlayerData: Eq[F[PlayerData]],
      gameEngine: Arbitrary[Resource[F, GameEngine.V1[F]]]
  ): SimpleRuleSet = new SimpleRuleSet(
    name = "GameEngine.V1",
    "process() keeps a copy of the processed input." -> Prop.forAll {
      (input: PlayerInput, gameEngineResource: Resource[F, GameEngine.V1[F]]) =>
        laws.processSavesUserInput(gameEngineResource)(input)
    },
    "process ignores MoveUp at maxY." -> Prop.forAll { gameEngineResource: Resource[F, GameEngine.V1[F]] =>
      laws.moveUpIsIgnoredAtMaxY(gameEngineResource)
    },
    "process ignores MoveDown at minY." -> Prop.forAll { gameEngineResource: Resource[F, GameEngine.V1[F]] =>
      laws.moveDownIsIgnoredAtMinY(gameEngineResource)
    },
    "process ignores MoveRight at maxX." -> Prop.forAll { gameEngineResource: Resource[F, GameEngine.V1[F]] =>
      laws.moveRightIsIgnoredAtMaxX(gameEngineResource)
    },
    "process ignores MoveLeft at minX." -> Prop.forAll { gameEngineResource: Resource[F, GameEngine.V1[F]] =>
      laws.moveLeftIsIgnoredAtMinX(gameEngineResource)
    }
  )
}

object GameEngineV1AlegraTests {
  def apply[F[_]: MonadCancel[*[_], Throwable]]: GameEngineV1AlegraTests[F] =
    new GameEngineV1AlegraTests[F] {
      override val laws: GameEngineV1AlgebraLaws[F] = GameEngineV1AlgebraLaws[F]
    }
}
