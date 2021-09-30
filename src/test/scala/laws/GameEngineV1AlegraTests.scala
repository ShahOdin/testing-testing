package laws

import cats.Eq
import cats.effect.MonadCancel
import cats.effect.kernel.Resource
import logic.GameEngine
import models.PlayerInput
import org.scalacheck.{Arbitrary, Prop}
import org.typelevel.discipline.Laws
import cats.kernel.laws.discipline._

trait GameEngineV1AlegraTests[F[_]] extends Laws {
  def laws: GameEngineV1AlgebraLaws[F]

  def algebra(
      implicit
      arbInput: Arbitrary[PlayerInput],
      eqFSetInput: Eq[F[Set[PlayerInput]]],
      gameEngine: Arbitrary[Resource[F, GameEngine.V1[F]]]
  ): SimpleRuleSet = new SimpleRuleSet(
    name = "GameEngine.V1",
    "process() keeps a copy of the processed input." -> Prop.forAll {
      (input: PlayerInput, gameEngineResource: Resource[F, GameEngine.V1[F]]) =>
        laws.processSavesUserInput(gameEngineResource)(input)
    }
  )
}

object GameEngineV1AlegraTests {
  def apply[F[_]: MonadCancel[*[_], Throwable]]: GameEngineV1AlegraTests[F] =
    new GameEngineV1AlegraTests[F] {
      override val laws: GameEngineV1AlgebraLaws[F] = GameEngineV1AlgebraLaws[F]
    }
}
