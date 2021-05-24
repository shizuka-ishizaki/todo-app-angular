/**
 * This is a sample of Todo Application.
 *
 */

package lib.model

import ixias.model._
import ixias.util.EnumStatus

import java.time.LocalDateTime

// ToDoCategoryを表すモデル
//~~~~~~~~~~~~~~~~~~~~
import ToDoCategory._
case class ToDoCategory(
  id:         Option[Id],
  name:       String,
  slug:       String,
  color:      CategoryColor,
  createdAt:  LocalDateTime = NOW,
  updatedAt:  LocalDateTime = NOW
) extends EntityModel[Id]

// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~
object ToDoCategory {

  val  Id = the[Identity[Id]]
  type Id = Long @@ ToDoCategory
  type WithNoId = Entity.WithNoId [Id, ToDoCategory]
  type EmbeddedId = Entity.EmbeddedId[Id, ToDoCategory]

  // ステータス定義
  //~~~~~~~~~~~~~~~~~
  sealed abstract class CategoryColor(val code: Short, val name: String) extends EnumStatus
  object CategoryColor extends EnumStatus.Of[CategoryColor] {
    case object NONE      extends CategoryColor(code = 0, name = "none")
    case object RED       extends CategoryColor(code = 1, name = "red")
    case object GREEN     extends CategoryColor(code = 2, name = "green")
    case object BLUE      extends CategoryColor(code = 3, name = "blue")
    case object YELLOW    extends CategoryColor(code = 4, name = "yellow")
  }

  // INSERT時のIDがAutoincrementのため,IDなしであることを示すオブジェクトに変換
  def apply(name: String, slug: String, color: CategoryColor): WithNoId = {
    new Entity.WithNoId(
      new ToDoCategory(
        id    = None,
        name  = name,
        slug  = slug,
        color = color
      )
    )
  }
}

