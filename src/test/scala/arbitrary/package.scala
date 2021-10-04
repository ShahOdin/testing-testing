import cats.effect.{IO, unsafe}
import cats.effect.kernel.{Resource, Sync}
import models.{GameData, PlayerInput}
import org.scalacheck.Arbitrary
import generators._
import enumeratum.scalacheck.arbEnumEntry
import logic.GameEngine

package object arbitrary {
  val gameDataWithBlankPlayerArbitrary: Arbitrary[GameData] = Arbitrary(gameDataWithBlankPlayerGen)
  def v1gameDataResourceArbitrary[F[_]: Sync]: Arbitrary[Resource[F, GameData]] =
    Arbitrary(v1GameEngineGameDataResourceGen)

  def v1gameDataResourceFromFileArbitrary(implicit runtime: unsafe.IORuntime): Arbitrary[Resource[IO, GameData]] =
    Arbitrary(v1GameEngineGameDataFromFileResourceGen)

  implicit val playerInputArbitrary: Arbitrary[PlayerInput] = arbEnumEntry
  def v1GameEngineResourceArbitrary[F[_]: Sync]: Arbitrary[Resource[F, GameEngine.V1[F]]] =
    Arbitrary(
      v1GameEngineGameDataResourceGen.map(
        _.flatMap(GameEngine.V1.apply[F])
      )
    )

  def v1GameEngineResourceFromFileArbitrary(
      implicit runtime: unsafe.IORuntime
  ): Arbitrary[Resource[IO, GameEngine.V1[IO]]] = Arbitrary(
    v1GameEngineGameDataFromFileResourceGen.map(
      _.flatMap(GameEngine.V1.apply[IO])
    )
  )
}
