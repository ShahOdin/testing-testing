package laws

import cats.effect.kernel.MonadCancel

sealed trait GameEngineV1AlgebraLaws[F[_]] extends GameEngineAlgebraLaws[F]
object GameEngineV1AlgebraLaws {
  def apply[F[_]](implicit ev: MonadCancel[F, Throwable]): GameEngineV1AlgebraLaws[F] = new GameEngineV1AlgebraLaws[F] {
    override implicit val M: MonadCancel[F, Throwable] = ev
  }
}
