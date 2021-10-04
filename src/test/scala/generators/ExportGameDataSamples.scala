package generators

import cats.effect.{IO, IOApp}
import fs2.{Stream, text}
import io.circe.syntax._
import codecs._

import fs2.io.file.{Files, Path}

object ExportGameDataSamples extends IOApp.Simple {
  override def run: IO[Unit] = {
    val stream: Stream[IO, String] = for {
      maybegameDataResource <- Stream.repeatEval(IO(v1GameEngineGameDataResourceGen[IO].sample))
      resource              <- Stream.fromOption(maybegameDataResource)
      gameData              <- Stream.resource(resource)
      json = gameData.asJson.noSpaces
    } yield json

    stream.repeat
      .take(100)
      .intersperse("\n")
      .through(text.utf8.encode)
      .through(Files[IO].writeAll(Path("gamedata.txt")))
      .compile
      .drain
  }

}
