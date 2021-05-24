/**
  * This is a sample of Todo Application.
  * 
  */

package lib.persistence

import scala.concurrent.Future
import ixias.persistence.SlickRepository
import lib.model.{ToDo, ToDoCategory}
import slick.jdbc.JdbcProfile

// ToDoRepository: ToDoTableへのクエリ発行を行うRepository層の定義
//~~~~~~~~~~~~~~~~~~~~~~
case class ToDoRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[ToDo.Id, ToDo, P]
  with db.SlickResourceProvider[P] {

  import api._

  /**
   * GetAll ToDo Data
   */
  def all(): Future[Seq[EntityEmbeddedId]] =
    RunDBAction(ToDoTable, "slave") { _
      .result
    }

  /**
    * Get ToDo Data
    */
  def get(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(ToDoTable, "slave") { _
      .filter(_.id === id)
      .result.headOption
    }

  /**
    * Add ToDo Data
   */
  def add(entity: EntityWithNoId): Future[Id] =
    RunDBAction(ToDoTable) { slick =>
      slick returning slick.map(_.id) += entity.v
    }

  /**
   * Update ToDo Data
   */
  def update(entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
    RunDBAction(ToDoTable) { slick =>
      val row = slick.filter(_.id === entity.id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.update(entity.v)
        }
      } yield old
    }

  /**
   * Delete ToDo Data
   */
  def remove(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(ToDoTable) { slick =>
      val row = slick.filter(_.id === id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.delete
        }
      } yield old
    }

  /**
   * Delete ToDo By Category Data <br>
   * Categoryが削除された時に、該当しているTodoを削除する
   */
  def removeByCategoryId(categoryId: ToDoCategory.Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(ToDoTable) { slick =>
      val row = slick.filter(_.categoryId === categoryId)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.delete
        }
      } yield old
    }

  /**
   * Update ToDo By Category Data <br>
   * ”どのカテゴリにも紐づいていない” というデータになるようTodoを更新する <br>
   * 暫定的に値を0に更新している <br>
   * ※ToDoCategory.Idはオートインクリメントで付与される、かつ、Idは1から付与されているため0は存在しないデータという認識
   */
  def updateByCategoryId(categoryId: ToDoCategory.Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(ToDoTable) { slick =>
      val row = slick.filter(_.categoryId === categoryId)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.map(_.categoryId).update(ToDoCategory.Id(0))
        }
      } yield old
    }
}