package controllers

import java.net.URI

import com.typesafe.config.Config
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.{ConfigLoader, Configuration, Logging}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class MyController @Inject()(config: Configuration, cc: ControllerComponents) extends AbstractController(cc) {
  def getFoo = Action {
    println(config.getAndValidate("foo", Set("bar", "baz")))

    /*
      custom config
     */
    val customConf = config.get[AppConfig]("app.config")
    println(customConf.baseUri)
    println(customConf.title)

    Ok(config.get[String]("foo"))
  }

  def echo = Action { implicit request =>
    Ok("Got request [" + request + "]")
  }

  def action1 = Action { implicit request =>
    anotherMethod("chocomint")
    Ok("Got request [" + request + "]")
  }

  // The last way of creating an Action value is to specify an additional BodyParser argument:
  def action2 = Action(parse.json) { implicit request =>
    Ok("Got request [" + request + "]")
  }

  def actionVariation = Action { implicit request =>
    val ok = Ok("Hello world!")
    val notFound = NotFound
    val pageNotFound = NotFound(<h1>Page not found</h1>)
    //val badRequest = BadRequest(views.html.form(formWithErrors))
    val oops = InternalServerError("Oops")
    val anyStatus = Status(488)("Strange response type")
    Ok
  }

  def redirectSample1 = Action {
    Redirect("/echo1", MOVED_PERMANENTLY)
  }

  def redirectSample2 = Action {
    Redirect(routes.HomeController.hello("Bob"))
  }

  def manipulatingHttpHeaders = Action {
    Ok("Hello World").withHeaders(
      CACHE_CONTROL -> "max-age=3600",
      ETAG -> "x",
    )
  }

  def discardingCookies = Action {
    // HTTPレスポンスにCookie追加する
    val result = Ok("Discarding Cookes")
      .withCookies(Cookie("theme", "blue"), Cookie("skin", "white"))
      .bakeCookies()

    // 以前Webブラウザに保存されていたCookieを破棄する
    val result2 = result.discardingCookies(DiscardingCookie("theme"))
    result2
  }

  def customCharset = Action {
    implicit val myCustomCharset = Codec.javaSupported("iso-8859-1")
    Ok(<h1>Hello World!</h1>).as(HTML)
  }

  // Contetn-Type: application/jsonの場合は、charset=utf-8をセットしてもPlayで消してしまうので注意。
  def customCharsetUtf8 = Action {
    implicit val myCustomCharset = Codec.javaSupported("utf-8")
    Ok(Json.obj("result" -> "ok"))
  }

  def storingInSession = Action {
    // The default name for the cookie is PLAY_SESSION.
    // This can be changed by configuring the key play.http.session.cookieName in application.conf.
    Ok("Welcome").withSession(
      "connected" -> "chocomint@example.com"
    )
  }

  def addStoringSession(name: String) = Action { implicit request =>
    Ok("Welcome").withSession(
      request.session + (s"$name" -> s"Hyper $name")
    )
  }

  def discardingSession = Action {
    Ok("Bye").withNewSession
  }

  def saveJson = Action { implicit request =>
    val body = request.body
    val jsonBody = body.asJson
    jsonBody.map { json =>
      Ok("Got: " + (json \ "name").as[String])
    }.getOrElse(BadRequest("Expecting application/json request body"))
  }

  // Optionでラップされていないので使いやすい。
  def saveJsonWithBodyParser = Action(parse.json) { implicit request =>
    Ok("Got: " + (request.body \ "name").as[String])
  }

  //Content-Typeを無視し、本文をjsonとして解析する。
  def saveJsonWithTolerant = Action(parse.tolerantJson) { implicit request =>
    Ok("Got: " + (request.body \ "name").as[String])
  }


  private def anotherMethod(str: String)(implicit requset: Request[_]) = {
    println(s"$str: ${requset.body}")
  }
}


case class AppConfig(title: String, baseUri: URI)

object AppConfig {

  implicit val configLoader: ConfigLoader[AppConfig] = new ConfigLoader[AppConfig] {
    def load(rootConfig: Config, path: String): AppConfig = {
      val config = rootConfig.getConfig(path)
      AppConfig(
        title = config.getString("title"),
        baseUri = new URI(config.getString("baseUri"))
      )
    }
  }
}


/*
 * Custom action builders Sample
 * https://www.playframework.com/documentation/2.7.x/ScalaActionsComposition
 */

import play.api.mvc._

class LoggingAction @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) with Logging {
  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    logger.warn("Calling custom action")
    block(request)
  }
}


class MyCustomActionController @Inject()(loggingAction: LoggingAction, cc: ControllerComponents)
  extends AbstractController(cc) {

  def loggingIndex = loggingAction {
    Ok("Hello World")
  }

  def submit = loggingAction(parse.text) { request =>
    Ok("Got a body " + request.body.length + " bytes long")
  }
}