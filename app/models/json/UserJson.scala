package models.json

import play.api.libs.json._

case class AuthJson(partner: String, accountId: String, deviceId: String)

object AuthJson {
  implicit val format: Format[AuthJson] = Json.format[AuthJson]

}
