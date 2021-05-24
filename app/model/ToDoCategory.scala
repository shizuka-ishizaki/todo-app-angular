/**
 *
 * to do sample project
 *
 */

package model

import play.api.data.Form
import controllers.category.ToDoCategoryFormData
import lib.model.ToDoCategory
import lib.model.ToDoCategory.CategoryColor

// ToDoカテゴリーリスト画面のviewvalue
case class ViewValueToDoCategoryList(
  title:        String,
  cssSrc:       Seq[String],
  jsSrc:        Seq[String],
  categoryList: Seq[Category],
) extends ViewValueCommon

// ToDoカテゴリー追加画面のviewvalue
case class ViewValueToDoCategoryAdd(
  title:        String,
  cssSrc:       Seq[String],
  jsSrc:        Seq[String],
  categoryForm: Form[ToDoCategoryFormData],
) extends ViewValueCommon

// ToDoカテゴリー編集画面のviewvalue
case class ViewValueToDoCategoryEdit(
  title:        String,
  cssSrc:       Seq[String],
  jsSrc:        Seq[String],
  id:           Long,
  categoryForm: Form[ToDoCategoryFormData],
) extends ViewValueCommon

// category.listで使用するカテゴリーの情報を持ったcase class
case class Category(
  id:     ToDoCategory.Id,
  name:   String,
  slug:   String,
  color:  CategoryColor,
)

