import cats.Eq
import cats.effect.{IO, unsafe}
import models.PlayerInput
import enumeratum.Cats.eqForEnum

package object laws {

  implicit val playerInputEq: Eq[PlayerInput] = eqForEnum

  implicit def ioEq[A: Eq](implicit runtime: unsafe.IORuntime): Eq[IO[A]] = Eq.by(_.unsafeRunSync())

}
