package json.reads

import play.api.libs.json._
import lib.model._

case class JsValueReadsToDoId(
  id: String
)

object  JsValueReadsToDoId {
  implicit val todoIdReads = Json.reads[JsValueReadsToDoId]
}


case class JsValueReadsToDoAdd(
  title:    String,
  body:     String,
  category: String
)

object  JsValueReadsToDoAdd {
  implicit val todoAddReads = Json.reads[JsValueReadsToDoAdd]
}