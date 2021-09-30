package logic

import arbitrary._
import cats.effect.IO
import models.{Coordinate, GameData, Grid, PlayerData, PlayerInput}
import org.scalacheck.effect.PropF
import util.Scenarios
import monocle.syntax.all._
import munit.ScalaCheckEffectSuite

class GameEngineV1Spec extends munit.CatsEffectSuite with ScalaCheckEffectSuite {

  private val grid: Grid = Grid(0, 5, 0, 5)
  private def gameDataWithBlankPlayer(startingAt: Coordinate) = GameData(
    grid = grid,
    player = PlayerData(startingAt, history = Nil)
  )

  // Generic movements ( 4 inputs => 4 tests)

  test("game Engine should process simple MoveUp successfully".tag(Scenarios.Simple)) {
    GameEngine
      .v1[IO](
        gameDataWithBlankPlayer(startingAt = Coordinate(x = 2, y = 3))
      )
      .use { engine =>
        for {
          _          <- engine.process(PlayerInput.MoveUp)
          playerData <- engine.getGameData.map(_.player)
        } yield assertEquals(
          playerData,
          PlayerData(coordinate = Coordinate(x = 2, y = 4), history = List(PlayerInput.MoveUp))
        )
      }
  }

  test("game Engine should process simple MoveDown successfully".tag(Scenarios.Simple)) {
    GameEngine
      .v1[IO](
        gameDataWithBlankPlayer(startingAt = Coordinate(x = 2, y = 3))
      )
      .use { engine =>
        for {
          _          <- engine.process(PlayerInput.MoveDown)
          playerData <- engine.getGameData.map(_.player)
        } yield assertEquals(
          playerData,
          PlayerData(coordinate = Coordinate(x = 2, y = 2), history = List(PlayerInput.MoveDown))
        )
      }
  }

  test("game Engine should process simple MoveRight successfully".tag(Scenarios.Simple)) {
    GameEngine
      .v1[IO](
        gameDataWithBlankPlayer(startingAt = Coordinate(x = 2, y = 3))
      )
      .use { engine =>
        for {
          _          <- engine.process(PlayerInput.MoveRight)
          playerData <- engine.getGameData.map(_.player)
        } yield assertEquals(
          playerData,
          PlayerData(coordinate = Coordinate(x = 3, y = 3), history = List(PlayerInput.MoveRight))
        )
      }
  }

  test("game Engine should process simple MoveLeft successfully".tag(Scenarios.Simple)) {
    GameEngine
      .v1[IO](
        gameDataWithBlankPlayer(startingAt = Coordinate(x = 2, y = 3))
      )
      .use { engine =>
        for {
          _          <- engine.process(PlayerInput.MoveLeft)
          playerData <- engine.getGameData.map(_.player)
        } yield assertEquals(
          playerData,
          PlayerData(coordinate = Coordinate(x = 1, y = 3), history = List(PlayerInput.MoveLeft))
        )
      }
  }

  // Inputs special case ( 4 inputs => 4 tests)

  test("game Engine should ignore MoveUp if already at max Y".tag(Scenarios.EdgeCase)) {
    GameEngine
      .v1[IO](
        gameDataWithBlankPlayer(startingAt = Coordinate(x = 2, y = 5))
      )
      .use { engine =>
        for {
          _          <- engine.process(PlayerInput.MoveUp)
          playerData <- engine.getGameData.map(_.player)
        } yield assertEquals(
          playerData,
          PlayerData(coordinate = Coordinate(x = 2, y = 5), history = List(PlayerInput.MoveUp))
        )
      }
  }

  test("game Engine should ignore MoveDown if already at min Y".tag(Scenarios.EdgeCase)) {
    GameEngine
      .v1[IO](
        gameDataWithBlankPlayer(startingAt = Coordinate(x = 2, y = 0))
      )
      .use { engine =>
        for {
          _          <- engine.process(PlayerInput.MoveDown)
          playerData <- engine.getGameData.map(_.player)
        } yield assertEquals(
          playerData,
          PlayerData(coordinate = Coordinate(x = 2, y = 0), history = List(PlayerInput.MoveDown))
        )
      }
  }

  test("game Engine should ignore MoveRight if already at max X".tag(Scenarios.EdgeCase)) {
    GameEngine
      .v1[IO](
        gameDataWithBlankPlayer(startingAt = Coordinate(x = 5, y = 3))
      )
      .use { engine =>
        for {
          _          <- engine.process(PlayerInput.MoveRight)
          playerData <- engine.getGameData.map(_.player)
        } yield assertEquals(
          playerData,
          PlayerData(coordinate = Coordinate(x = 5, y = 3), history = List(PlayerInput.MoveRight))
        )
      }
  }

  test("game Engine should ignore MoveLeft if already at min X".tag(Scenarios.EdgeCase)) {
    GameEngine
      .v1[IO](
        gameDataWithBlankPlayer(startingAt = Coordinate(x = 0, y = 3))
      )
      .use { engine =>
        for {
          _          <- engine.process(PlayerInput.MoveLeft)
          playerData <- engine.getGameData.map(_.player)
        } yield assertEquals(
          playerData,
          PlayerData(coordinate = Coordinate(x = 0, y = 3), history = List(PlayerInput.MoveLeft))
        )
      }
  }

  // generic test (4 inputs => 4 tests)
  test("game engine should process a generic MoveUp input successfully".tag(Scenarios.Generic)) {
    PropF.forAllF(gameDataWithBlankPlayerArbitrary.arbitrary) { gameData =>
      GameEngine.v1[IO](gameData).use { engine =>
        for {
          gameData <- engine.getGameData
          isAtMaxY     = gameData.playerIsAtMaxY
          coordsBefore = gameData.player.coordinate

          input = PlayerInput.MoveUp
          _ <- engine.process(input)

          playerData <- engine.getGameData.map(_.player)
          coordsAfter = if (isAtMaxY) coordsBefore else coordsBefore.focus(_.y).modify(_ + 1)
        } yield assertEquals(playerData, PlayerData(coordinate = coordsAfter, history = List(input)))
      }
    }
  }

  test("game engine should process a generic MoveDown input successfully".tag(Scenarios.Generic)) {
    PropF.forAllF(gameDataWithBlankPlayerArbitrary.arbitrary) { gameData =>
      GameEngine.v1[IO](gameData).use { engine =>
        for {
          gameData <- engine.getGameData
          isAtMinY     = gameData.playerIsAtMinY
          coordsBefore = gameData.player.coordinate

          input = PlayerInput.MoveDown
          _ <- engine.process(input)

          playerData <- engine.getGameData.map(_.player)
          coordsAfter = if (isAtMinY) coordsBefore else coordsBefore.focus(_.y).modify(_ - 1)
        } yield assertEquals(playerData, PlayerData(coordinate = coordsAfter, history = List(input)))
      }
    }
  }

  test("game engine should process a generic MoveRight input successfully".tag(Scenarios.Generic)) {
    PropF.forAllF(gameDataWithBlankPlayerArbitrary.arbitrary) { gameData =>
      GameEngine.v1[IO](gameData).use { engine =>
        for {
          gameData <- engine.getGameData
          isAtMaxX     = gameData.playerIsAtMaxX
          coordsBefore = gameData.player.coordinate

          input = PlayerInput.MoveRight
          _ <- engine.process(input)

          playerData <- engine.getGameData.map(_.player)
          coordsAfter = if (isAtMaxX) coordsBefore else coordsBefore.focus(_.x).modify(_ + 1)
        } yield assertEquals(playerData, PlayerData(coordinate = coordsAfter, history = List(input)))
      }
    }
  }

  test("game engine should process a generic MoveLeft input successfully".tag(Scenarios.Generic)) {
    PropF.forAllF(gameDataWithBlankPlayerArbitrary.arbitrary) { gameData =>
      GameEngine.v1[IO](gameData).use { engine =>
        for {
          gameData <- engine.getGameData
          isAtMinX     = gameData.playerIsAtMinX
          coordsBefore = gameData.player.coordinate

          input = PlayerInput.MoveLeft
          _ <- engine.process(input)

          playerData <- engine.getGameData.map(_.player)
          coordsAfter = if (isAtMinX) coordsBefore else coordsBefore.focus(_.x).modify(_ - 1)
        } yield assertEquals(playerData, PlayerData(coordinate = coordsAfter, history = List(input)))
      }
    }
  }

  // simulated QA test (4 inputs => 4 tests)
  test(
    "game engine should process a generic MoveUp input successfully, for a simulated user.".tag(Scenarios.SimulatedQA)
  ) {
    PropF.forAllF(gameDataResourceArbitrary[IO].arbitrary) { gameData =>
      gameData.flatMap(GameEngine.v1[IO]).use { engine =>
        for {
          gameData <- engine.getGameData
          isAtMaxY      = gameData.playerIsAtMaxY
          coordsBefore  = gameData.player.coordinate
          historyBefore = gameData.player.history

          input = PlayerInput.MoveUp
          _ <- engine.process(input)

          playerData <- engine.getGameData.map(_.player)
          coordsAfter = if (isAtMaxY) coordsBefore else coordsBefore.focus(_.y).modify(_ + 1)
        } yield assertEquals(playerData, PlayerData(coordinate = coordsAfter, history = input :: historyBefore))
      }
    }
  }

  test(
    "game engine should process a generic MoveDown input successfully, for a simulated user.".tag(Scenarios.SimulatedQA)
  ) {
    PropF.forAllF(gameDataResourceArbitrary[IO].arbitrary) { gameData =>
      gameData.flatMap(GameEngine.v1[IO]).use { engine =>
        for {
          gameData <- engine.getGameData
          isAtMinY      = gameData.playerIsAtMinY
          coordsBefore  = gameData.player.coordinate
          historyBefore = gameData.player.history

          input = PlayerInput.MoveDown
          _ <- engine.process(input)

          playerData <- engine.getGameData.map(_.player)
          coordsAfter = if (isAtMinY) coordsBefore else coordsBefore.focus(_.y).modify(_ - 1)
        } yield assertEquals(playerData, PlayerData(coordinate = coordsAfter, history = input :: historyBefore))
      }
    }
  }

  test(
    "game engine should process a generic MoveRight input successfully, for a simulated user."
      .tag(Scenarios.SimulatedQA)
  ) {
    PropF.forAllF(gameDataResourceArbitrary[IO].arbitrary) { gameData =>
      gameData.flatMap(GameEngine.v1[IO]).use { engine =>
        for {
          gameData <- engine.getGameData
          isAtMaxX      = gameData.playerIsAtMaxX
          coordsBefore  = gameData.player.coordinate
          historyBefore = gameData.player.history

          input = PlayerInput.MoveRight
          _ <- engine.process(input)

          playerData <- engine.getGameData.map(_.player)
          coordsAfter = if (isAtMaxX) coordsBefore else coordsBefore.focus(_.x).modify(_ + 1)
        } yield assertEquals(playerData, PlayerData(coordinate = coordsAfter, history = input :: historyBefore))
      }
    }
  }

  test(
    "game engine should process a generic MoveLeft input successfully, for a simulated user.".tag(Scenarios.SimulatedQA)
  ) {
    PropF.forAllF(gameDataResourceArbitrary[IO].arbitrary) { gameData =>
      gameData.flatMap(GameEngine.v1[IO]).use { engine =>
        for {
          gameData <- engine.getGameData
          isAtMinX      = gameData.playerIsAtMinX
          coordsBefore  = gameData.player.coordinate
          historyBefore = gameData.player.history

          input = PlayerInput.MoveLeft
          _ <- engine.process(input)

          playerData <- engine.getGameData.map(_.player)
          coordsAfter = if (isAtMinX) coordsBefore else coordsBefore.focus(_.x).modify(_ - 1)
        } yield assertEquals(playerData, PlayerData(coordinate = coordsAfter, history = input :: historyBefore))
      }
    }
  }

}
