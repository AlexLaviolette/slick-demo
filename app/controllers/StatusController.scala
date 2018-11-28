package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

@Singleton
class StatusController @Inject()(cc: ControllerComponents)(implicit executionContext: ExecutionContext) extends AbstractController(cc) {

  def get = Action {
    Ok("ok")
  }
}
