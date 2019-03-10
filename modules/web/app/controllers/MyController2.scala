package controllers

import java.io.File

import akka.stream.scaladsl.{FileIO, Source}
import javax.inject.Inject
import play.api.http.HttpEntity
import play.api.mvc.{AbstractController, ControllerComponents, ResponseHeader, Result}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class MyController2 @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /*
    Sending large amounts of data.
   */
  def streamed = Action { implicit request =>
    val file = new File("/tmp/large_file.zip")
    val path = file.toPath
    val source = FileIO.fromPath(path)

    // Content-Lengthを計算して指定する。
    // 指定しない場合、Playは計算するためにメモリにすべて読み込んでセットすることになる。
    val contentLength = Option(file.length)

    Result(
      header = ResponseHeader(200, Map.empty),
      body = HttpEntity.Streamed(source, contentLength, Some("application/zip"))
    )
  }

  /*
    Serving files
   */

  //Play provides easy-to-use helpers for common task of serving a local file:
  def file = Action { implicit request =>
    // このヘルパーはファイル名からContent-Typeヘッダーも計算し、Content-Dispositionヘッダーを追加してWebブラウザがこの応答を処理する方法を指定します。
    Ok.sendFile(new File("/tmp/large_file.zip"))
  }

  def fileWithName = Action {
    Ok.sendFile(
      // ダウンロードファイル名を任意のものに指定する場合
      content = new File("/tmp/large_file.zip"),
      fileName = _ => "termOfService.zip"
    )
  }

  /*
    Chunk responses
   */

  def chunkedFromSource = Action {
    val source = Source(List("foo", "bar", "baz"))
    Ok.chunked(source)
  }

}
