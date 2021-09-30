import cats.effect.{IO, Sync, unsafe}
import cats.effect.kernel.Resource
import models.{Coordinate, GameData, Grid, PlayerData, PlayerInput}
import org.scalacheck.{Arbitrary, Gen}
import enumeratum.scalacheck._
import logic.GameEngine
import cats.syntax.all._
import cats.instances.list._
import io.circe.parser._
import codecs._

import scala.io.Source

package object generators {

  private def startingCoordGen(grid: Grid): Gen[Coordinate] =
    for {
      x <- Gen.chooseNum(grid.minX, grid.maxX)
      y <- Gen.chooseNum(grid.minY, grid.maxY)
    } yield Coordinate(x = x, y = y)

  private val gridGen: Gen[Grid] = for {
    minX <- Gen.posNum[Int]
    minY <- Gen.posNum[Int]

    xBand <- Gen.posNum[Int]
    yBand <- Gen.posNum[Int]
  } yield Grid(minX, minX + xBand, minY, minY + yBand)

  private def blankPlayerDataGen(grid: Grid): Gen[PlayerData] =
    for {
      coord <- startingCoordGen(grid)
    } yield PlayerData(coord, history = Nil)

  val gameDataWithBlankPlayerGen: Gen[GameData] = for {
    grid       <- gridGen
    playerData <- blankPlayerDataGen(grid)
  } yield GameData(grid, playerData)

  private def v1GameEngineResourceGen[F[_]: Sync]: Gen[Resource[F, GameEngine.V1[F]]] =
    for {
      blankGameData <- gameDataWithBlankPlayerGen
      playerInputs  <- Gen.nonEmptyListOf(implicitly[Arbitrary[PlayerInput]].arbitrary)
      gameEngine = GameEngine.V1[F](blankGameData)

      iteratedGameEngine = gameEngine.evalMap(engine =>
        playerInputs
          .traverse_(engine.process)
          .as(engine)
      )

    } yield iteratedGameEngine

  def v1GameEngineGameDataResourceGen[F[_]: Sync]: Gen[Resource[F, GameData]] =
    for {
      gameEngine <- v1GameEngineResourceGen
      gameDataResource = gameEngine.evalMap(_.getGameData)
    } yield gameDataResource

  def v1GameEngineGameDataFromFileResourceGen(implicit runtime: unsafe.IORuntime): Gen[Resource[IO, GameData]] =
    Gen.oneOf(
      Resource
        .fromAutoCloseable(Source.fromFile("gamedata.txt").pure[IO])
        .map(_.getLines().map(parse).toList.traverse(_.flatMap(_.as[GameData])))
        .map(IO.fromEither)
        .flatMap(Resource.eval)
        .use(IO.pure)
        .unsafeRunSync()
        .map(Resource.pure[IO, GameData])
    )
}
