package dao

import javax.inject.Inject
import models.{Account, Device}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait AccountComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import profile.api._

  class AccountTable(tag: Tag) extends Table[Account](tag, "accounts") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def partner = column[String]("partner")
    def account_id = column[String]("accountId")

    def * = (id.?, partner, account_id) <> ((Account.apply _).tupled, Account.unapply)
  }

  def _findAccountByAccountId(partner: String, accountId: String): DBIO[Option[Account]] = {
    TableQuery[AccountTable].filter(_.account_id === accountId).filter(_.partner === partner).result.headOption
  }
}


class AccountDao @Inject()(
    @NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider, devices: DeviceDao)
    (implicit executionContext: ExecutionContext)
  extends AccountComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val accounts = TableQuery[AccountTable]

  def _insertIfNotExists(partner: String, accountId: String): DBIO[Account] = {
    accounts
      .filter(_.account_id === accountId)
      .filter(_.partner === partner)
      .result.headOption.flatMap {
      case Some(account) => DBIO.successful(account) // no-op
      case _ => {
        accounts.returning(accounts.map(_.id)).into((account, id) => account.copy(id = Some(id))) += Account(None, partner, accountId)
      }
    }
  }

  def createWithDeviceIfNotExists(partner: String, accountId: String, deviceId: String): Future[Device] = {
    val query = for {
      account <- _insertIfNotExists(partner, accountId)
      device <- devices._insertIfNotExists(deviceId, account.id.get)
    } yield device

    db.run(query.transactionally)
  }
}
