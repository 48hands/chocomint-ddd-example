package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json._
import play.api.mvc._

class MyJsonController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  val json1: JsValue = Json.parse(
    """
  {
    "name" : "Watership Down",
    "location" : {
      "lat" : 51.235685,
      "long" : -1.309197
    },
    "residents" : [ {
      "name" : "Fiver",
      "age" : 4,
      "role" : null
    }, {
      "name" : "Bigwig",
      "age" : 6,
      "role" : "Owsla"
    } ]
  }
  """)

  def getJson1 = Action { implicit request =>
    val json1: JsValue = Json.parse(
      """
  {
    "name" : "Watership Down",
    "location" : {
      "lat" : 51.235685,
      "long" : -1.309197
    },
    "residents" : [ {
      "name" : "Fiver",
      "age" : 4,
      "role" : null
    }, {
      "name" : "Bigwig",
      "age" : 6,
      "role" : "Owsla"
    } ]
  }
  """)

    Ok(json1)
  }


  /*
    Using Write Conversion
   */
  def getJson2 = Action { request =>
    val jsonString = Json.toJson("Fiver")
    val jsonNumber = Json.toJson(4)
    val jsonBoolean = Json.toJson(false)
    val jsonArray = Json.toJson(Seq(1, 2, 3, 4, 5))
    val jsonArrayOfString = Json.toJson(Seq("Fiver", "Bigwig"))


    Ok(Json.obj(
      "jsonString" -> jsonString,
      "jsonNumber" -> jsonNumber,
      "jsonBoolean" -> jsonBoolean,
      "jsonArray" -> jsonArray,
      "jsonArrayOfString" -> jsonArrayOfString
    ))
  }


  case class Location(lat: Double, long: Double)

  case class Resident(name: String, age: Int, role: Option[String])

  case class Place(name: String, location: Location, residents: Seq[Resident])

  implicit object locationWrites extends Writes[Location] {
    override def writes(o: Location): JsValue = Json.obj(
      "lat" -> o.lat,
      "long" -> o.long
    )
  }

  implicit object residentWrites extends Writes[Resident] {
    override def writes(o: Resident): JsValue = Json.obj(
      "name" -> o.name,
      "age" -> o.age,
      "role" -> o.role
    )
  }

  implicit object placeWrites extends Writes[Place] {
    override def writes(o: Place): JsValue = Json.obj(
      "name" -> o.name,
      "location" -> o.location,
      "redirents" -> o.residents
    )
  }

  def getJson3 = Action { _ =>

    val place = Place(
      "Watership Down",
      Location(51.235685, -1.309197),
      Seq(
        Resident("Fiver", 4, None),
        Resident("Bigwig", 6, Some("Owsla"))
      )
    )

    Ok(Json.obj("place" -> place))
  }


  import play.api.libs.functional.syntax._

  implicit val locationReads: Reads[Location] = (
    (JsPath \ "lat").read[Double] and
      (JsPath \ "long").read[Double]
    ) (Location.apply _)

  implicit val residentReads: Reads[Resident] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "age").read[Int] and
      (JsPath \ "role").readNullable[String]
    ) (Resident.apply _)

  implicit val placeReads: Reads[Place] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "location").read[Location] and
      (JsPath \ "residents").read[Seq[Resident]]
    ) (Place.apply _)

  def getJson4 = Action { _ =>
    val json: JsValue = Json.parse(
      """
  {
    "name" : "Watership Down",
    "location" : {
      "lat" : 51.235685,
      "long" : -1.309197
    },
    "residents" : [ {
      "name" : "Fiver",
      "age" : 4,
      "role" : null
    }, {
      "name" : "Bigwig",
      "age" : 6,
      "role" : "Owsla"
    } ]
  }
  """)

    val residentsResult = (json \ "residents").validate[Seq[Resident]]
    residentsResult match {
      case JsSuccess(values, _) => Ok(values.toString)
      case e: JsError => Ok("error")
    }
  }

  def savePlace = Action(parse.json) { request =>
    val placeResult = request.body.validate[Place]
    placeResult.fold(
      errors => {
        BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors)))
      }
      ,
      place => {
          Ok(Json.obj("success" -> place))
      }
    )
  }


}
