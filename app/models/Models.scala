package models

import play.api.libs.json.{Format, Json}

case class Account(id: Option[Long], partner: String, accountId: String)

case class Device(id: Option[Long], deviceId: String, accountID: Long)

object Device {
  implicit val format: Format[Device] = Json.format[Device]
}
