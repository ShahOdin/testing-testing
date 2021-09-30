import io.circe.Codec
import models.{Coordinate, GameData, Grid, PlayerData, PlayerInput}
import io.circe.generic.semiauto.deriveCodec

package object codecs {

  implicit val gridCodec: Codec[Grid] = deriveCodec

  implicit val coordinateCodec: Codec[Coordinate] = deriveCodec
  implicit val playerInputEncoder: Codec[PlayerInput] = Codec.from(
    enumeratum.Circe.decoder(PlayerInput),
    enumeratum.Circe.encoder(PlayerInput)
  )

  implicit val playerDataCodec: Codec[PlayerData] = deriveCodec
  implicit val gameDataCodec: Codec[GameData]     = deriveCodec
}
