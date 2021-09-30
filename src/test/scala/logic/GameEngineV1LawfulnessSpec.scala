package logic

import arbitrary._
import cats.effect.IO
import laws.GameEngineV1AlegraTests
import laws._
import util.Scenarios

class GameEngineV1LawfulnessSpec extends munit.CatsEffectSuite with munit.DisciplineSuite {
  checkAll(
    "GameEngine.V1".tag(Scenarios.Laws),
    GameEngineV1AlegraTests[IO].algebra
  )
}
