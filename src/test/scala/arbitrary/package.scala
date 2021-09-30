import cats.effect.kernel.{Resource, Sync}
import models.{GameData, PlayerInput}
import org.scalacheck.Arbitrary
import generators._
import enumeratum.scalacheck.arbEnumEntry
import logic.GameEngine

package object arbitrary {
  val gameDataWithBlankPlayerArbitrary: Arbitrary[GameData] = Arbitrary(gameDataWithBlankPlayerGen)
  def v1gameDataResourceArbitrary[F[_]: Sync]: Arbitrary[Resource[F, GameData]] =
    Arbitrary(v1GameEngineGameDataResource)

  implicit val playerInputArbitrary: Arbitrary[PlayerInput] = arbEnumEntry
  implicit def v1GameEngineResourceArbitrary[F[_]: Sync]: Arbitrary[Resource[F, GameEngine.V1[F]]] =
    Arbitrary(v1GameEngineResourceGen)
}
