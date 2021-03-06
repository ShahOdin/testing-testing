import cats.effect.kernel.{Resource, Sync}
import models.GameData
import org.scalacheck.Arbitrary
import generators._

package object arbitrary {
  val gameDataWithBlankPlayerArbitrary: Arbitrary[GameData]                     = Arbitrary(gameDataWithBlankPlayerGen)
  def v1gameDataResourceArbitrary[F[_]: Sync]: Arbitrary[Resource[F, GameData]] = Arbitrary(v1gameDataResourceGen)
}
