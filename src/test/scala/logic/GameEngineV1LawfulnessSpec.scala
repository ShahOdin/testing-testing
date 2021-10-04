package logic

import arbitrary._
import cats.effect.IO
import cats.effect.kernel.Resource
import laws.GameEngineV1AlegraTests
import laws._
import org.scalacheck.Arbitrary
import util.Scenarios

class GameEngineV1LawfulnessSpec extends munit.CatsEffectSuite with munit.DisciplineSuite {
  {
    implicit val arbitrary: Arbitrary[Resource[IO, GameEngine.V1[IO]]] = v1GameEngineResourceArbitrary[IO]
    checkAll(
      "GameEngine.V1 has lawful impl for all dynamically generated game data.".tag(Scenarios.Laws),
      GameEngineV1AlegraTests[IO].algebra
    )
  }

  {
    implicit val arbitrary: Arbitrary[Resource[IO, GameEngine.V1[IO]]] = v1GameEngineResourceFromFileArbitrary
    checkAll(
      "GameEngine.V1 has lawful impl for all previously generated game data.".tag(Scenarios.Laws),
      GameEngineV1AlegraTests[IO].algebra
    )
  }
}
