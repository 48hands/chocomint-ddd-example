package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json.Json

import com.mint.choco.servicies.ChocomintService


@Singleton
class ChocomintController @Inject()(cc: ControllerComponents, service: ChocomintService) extends AbstractController(cc) {
  def index = Action { implicit request =>
    val user = service.execute()
    Ok(Json.obj("result" -> user.toString))
  }
}
