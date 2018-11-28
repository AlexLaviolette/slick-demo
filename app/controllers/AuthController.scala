package controllers

import com.google.inject.Singleton
import dao.{AccountDao, DeviceDao}
import javax.inject.Inject
import models.json.AuthJson
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthController @Inject()(
    implicit accountDao: AccountDao,
    deviceDao: DeviceDao,
    cc : ControllerComponents,
    executionContext: ExecutionContext)
  extends AbstractController(cc) {


  def auth = Action.async(parse.json) { implicit request =>
    request.body.validate[AuthJson] match {
      case ar: JsSuccess[AuthJson] => {
        val partner = ar.get.partner.toLowerCase

        val res = for {
          _ <- accountDao.createWithDeviceIfNotExists(partner, ar.get.accountId, ar.get.deviceId)
        } yield {
          Ok
        }

        res.recover {
          case _: NoSuchElementException => Unauthorized("Bad token failed authentication")
          case e: Exception => {
            Logger.warn(s"[AuthController][auth] Something went wrong while attempting to verify the user: ${e.getMessage}")
            BadRequest(e.getMessage)
          }
        }

      }
      case e: JsError => Future.successful(BadRequest)
    }
  }

}
