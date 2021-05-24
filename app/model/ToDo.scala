/**
 *
 * to do sample project
 *
 */

package model

import lib.model.{ToDo, ToDoCategory}
import lib.model.ToDo.ToDoStatus
import lib.model.ToDoCategory.CategoryColor
import controllers.todo.ToDoFormData
import play.api.data.Form

// ToDoリスト画面のviewvalue
case class ViewValueToDoList(
  title:    String,
  cssSrc:   Seq[String],
  jsSrc:    Seq[String],
  toDoList: Seq[ToDoWithCategory],
) extends ViewValueCommon

// ToDo追加画面のviewvalue
case class ViewValueToDoAdd(
  title:      String,
  cssSrc:     Seq[String],
  jsSrc:      Seq[String],
  toDoForm:   Form[ToDoFormData],
  categorys:  Seq[(String, String)],
) extends ViewValueCommon

// ToDo編集画面のviewvalue
case class ViewValueToDoEdit(
  title:      String,
  cssSrc:     Seq[String],
  jsSrc:      Seq[String],
  id:         Long,
  toDoForm:   Form[ToDoFormData],
  categorys:  Seq[(String, String)],
) extends ViewValueCommon

//todo.listで使用するカテゴリーの情報を持ったcase class
case class ToDoWithCategory(
  id:             ToDo.Id,
  categoryId:     ToDoCategory.Id,
  title:          String,
  body:           String,
  status:         ToDoStatus,
  categoryName:   String,
  categoryColor:  CategoryColor,
)

