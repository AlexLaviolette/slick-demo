package dao

import javax.inject.Inject
import models.Device
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class DeviceDao @Inject()(@NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] with AccountComponent {


  import profile.api._

  private val devices = TableQuery[DeviceTable]
  private val accounts = TableQuery[AccountTable]

  private class DeviceTable(tag: Tag) extends Table[Device](tag, "devices") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def device_id = column[String]("deviceId")
    def accountID = column[Long]("accountID")

    def * = (id.?, device_id, accountID) <> ((Device.apply _).tupled, Device.unapply)

    def account = foreignKey("account_fk", accountID, accounts)(_.id, onDelete = ForeignKeyAction.Restrict)

  }

  def _insertIfNotExists(deviceId: String, accountId: Long): DBIO[Device] = {
    devices
      .filter(_.device_id === deviceId)
      .filter(_.accountID === accountId)
      .result.headOption.flatMap {
      case Some(device) => DBIO.successful(device)
      case _ => {
        devices.returning(devices.map(_.id)).into((device, id) => device.copy(id = Some(id))) += Device(None, deviceId, accountId)
      }
    }
  }
}
