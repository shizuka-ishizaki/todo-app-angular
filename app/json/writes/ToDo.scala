package json.writes

import play.api.libs.json._
import lib.model._

case class JsValueWritesToDoWithCategory(
  id:             String,
  categoryId:     String,
  title:          String,
  body:           String,
  status:         String,
  categoryName:   String,
  categoryColor:  String,
  editUrl:        String,
)

object  JsValueWritesToDoWithCategory {
  implicit val toDoWithCategoryWrites = Json.writes[JsValueWritesToDoWithCategory]
}

case class JsValueWritesCategoryList(
  id:   String,
  name: String,
)

object JsValueWritesCategoryList {
  implicit val apiResultWrites = Json.writes[JsValueWritesCategoryList]
}


case class JsValueWritesApiResult(
  isSuccess: Boolean,
)

object JsValueWritesApiResult {
  implicit val apiResultWrites = Json.writes[JsValueWritesApiResult]
}