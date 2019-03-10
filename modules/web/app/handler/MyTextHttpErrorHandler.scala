package handler

import com.sun.org.apache.xml.internal.utils.DefaultErrorHandler
import javax.inject.{Inject, Singleton}
import play.api.http.{DefaultHttpErrorHandler, HttpErrorHandler, JsonHttpErrorHandler, PreferredMediaTypeHttpErrorHandler}
import play.api.mvc.{RequestHeader, Result}
import play.api.mvc.Results._

import scala.concurrent.Future

/*
  自分でErrorHandlerを定義する

  application.confの設定は以下の通り
  play.http.errorHandler = "handler.MyTextHttpErrorHandler"
 */
@Singleton
class MyTextHttpErrorHandler extends HttpErrorHandler {
  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future.successful(
      Status(statusCode)("A client error occurred: " + message)
    )
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    Future.successful(
      InternalServerError("A server error occurred: " + exception.getMessage)
    )
  }
}

/**
  * Content-Typeに応じてエラーハンドラを使い分けるカスタムハンドラ
  */
class MyHttpErrorHandler @Inject()(
                                    jsonHandler: JsonHttpErrorHandler,
                                    htmlHandler: DefaultHttpErrorHandler,
                                    textHandler: MyTextHttpErrorHandler
                                  ) extends PreferredMediaTypeHttpErrorHandler(
  "application/json" -> jsonHandler,
  "text/html" -> htmlHandler,
  "text/plain" -> textHandler
)