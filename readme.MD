# Testing... testing

A short demonstration of a challenges and strategies for testing complex systems.

Table of contents

- [Description of the Game](#description)
- [Simple tests](#simple-tests)
- [Generic tests](#generic-tests)
- [Simulated QA tests](#simulated-qa-tests)
- [Laws](#Laws)

# Description

The game we are looking at, consists of a simple 2d grid and a player that can navigate it using the following inputs:

```scala
  case object MoveUp    extends PlayerInput
  case object MoveDown  extends PlayerInput
  case object MoveRight extends PlayerInput
  case object MoveLeft  extends PlayerInput
```

The game-data consists of the following:

```scala
final case class GameData(grid: Grid, player: PlayerData)
```

and the game engine has the simple interface of:

```scala
trait GameEngine[F[_]] {
  def getGameData: F[GameData]
  def process(playerInput: PlayerInput): F[Unit]
}
```

# Simple tests

- The devs/product teams are tasked with defining these and it can be quite time-consuming to set these up. It's easy to miss a scenario.

- To run the unit-tests simply run:

    ```bash
    sbt "testOnly -- --include-tags=simple,edge"
    ```

    This will run:

  - [pre-defined simple scenarios](./src/test/scala/logic/GameEngineV1Spec.scala#L19-L83)
  - [pre-defined edge case scenarios](./src/test/scala/logic/GameEngineV1Spec.scala#L85-L150)

# Generic tests

- There is an initial investment, but they reduce the number of specific scenarios to think about.
For the purposes of this simple game, where the future state is determined purely by the last input, these are all we need.

- To run the [generic-tests](./src/test/scala/logic/GameEngineV1Spec.scala#L151-L223) simply run:

    ```bash
    sbt "testOnly -- --include-tags=generic"
    ```

# Simulated QA tests

- In general however, things are more complicated. Usually systems have multiple components:
  - possibly with an action at the distance, or simply seemingly unrelated components impacting each other
  - the history of how they got there, matters. 

  
- The first case is easy to imagine. Just google 3 body problem! :) As for the second case, think of our original game, except that there is an additional rule: You can not re-visit a coordinate that you have previously visited! This brings a whole set of scenarios that the generic tests don't cover since all of our tests so far, start from a blank slate and simply process a single input. As rules like this accumulate, it becomes harder and harder to think of all the scenarios to cover.

- The idea is to: 
  - Start from a blank slate
  - generate a random set of user inputs
  - iterate the game data, using game engine (just as done in production) based on the inputs (possibly save them somewhere and avoid doing this every time.)
  - take the resulting data and run our generic tests on them

- To run the [qa-tests](./src/test/scala/logic/GameEngineV1Spec.scala#L224-L308) simply run:

    ```bash
    sbt "testOnly -- --include-tags=qa"
    ```
  
# Laws

https://github.com/ShahOdin/testing-testing/pull/1
