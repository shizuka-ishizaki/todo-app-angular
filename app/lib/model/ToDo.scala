/**
 * This is a sample of Todo Application.
 *
 */

package lib.model

import ixias.model._
import ixias.util.EnumStatus

import java.time.LocalDateTime

// ToDoを表すモデル
//~~~~~~~~~~~~~~~~~~~~
import ToDo._
case class ToDo(
  id:         Option[Id],
  categoryId: ToDoCategory.Id,
  title:      String,
  body:       String,
  status:     ToDoStatus,
  createdAt:  LocalDateTime = NOW,
  updatedAt:  LocalDateTime = NOW
) extends EntityModel[Id]

// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~
object ToDo {

  val  Id = the[Identity[Id]]
  type Id = Long @@ ToDo
  type WithNoId = Entity.WithNoId [Id, ToDo]
  type EmbeddedId = Entity.EmbeddedId[Id, ToDo]

  // ステータス定義
  //~~~~~~~~~~~~~~~~~
  sealed abstract class ToDoStatus(val code: Short, val name: String) extends EnumStatus
  object ToDoStatus extends EnumStatus.Of[ToDoStatus] {
    case object TODO   extends ToDoStatus(code = 0, name = "着手前")
    case object DOING  extends ToDoStatus(code = 1, name = "進行中")
    case object DONE   extends ToDoStatus(code = 2, name = "完了")
  }

  // INSERT時のIDがAutoincrementのため,IDなしであることを示すオブジェクトに変換
  def apply(categoryId: ToDoCategory.Id, title: String, body: String, status: ToDoStatus): WithNoId = {
    new Entity.WithNoId(
      new ToDo(
        id          = None,
        categoryId  = categoryId,
        title       = title,
        body        = body,
        status      = status
      )
    )
  }
}

