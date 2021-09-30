package laws

import cats.effect.IO
import arbitrary._

class GameEngineV1LawfulnessSpec extends munit.CatsEffectSuite with munit.DisciplineSuite {
  checkAll(
    "GameEngine.V1",
    GameEngineV1AlegraTests[IO].algebra
  )
}
