import cats.Eq
import cats.effect.{IO, unsafe}
import models.{Coordinate, PlayerData, PlayerInput}
import enumeratum.Cats.eqForEnum

package object laws {

  implicit val playerInputEq: Eq[PlayerInput] = eqForEnum
  implicit val coordinateEq: Eq[Coordinate]   = Eq.or(Eq.by(_.x), Eq.by(_.y))
  implicit val playerDataEq: Eq[PlayerData]   = Eq.or(Eq.by(_.history), Eq.by(_.coordinate))

  implicit def ioEq[A: Eq](implicit runtime: unsafe.IORuntime): Eq[IO[A]] = Eq.by(_.unsafeRunSync())

}
