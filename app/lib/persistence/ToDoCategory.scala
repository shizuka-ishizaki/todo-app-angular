/**
  * This is a sample of Todo Application.
  * 
  */

package lib.persistence

import scala.concurrent.Future
import ixias.persistence.SlickRepository
import lib.model.ToDoCategory
import slick.jdbc.JdbcProfile

// ToDoCategoryRepository: ToDoCategoryTableへのクエリ発行を行うRepository層の定義
//~~~~~~~~~~~~~~~~~~~~~~
case class ToDoCategoryRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[ToDoCategory.Id, ToDoCategory, P]
  with db.SlickResourceProvider[P] {

  import api._

  /**
   * GetAll ToDo Data
   */
  def all(): Future[Seq[EntityEmbeddedId]] =
    RunDBAction(ToDoCategoryTable, "slave") { _
        .result
    }

  /**
    * Get ToDoCategory Data
    */
  def get(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(ToDoCategoryTable, "slave") { _
      .filter(_.id === id)
      .result.headOption
  }

  /**
    * Add ToDoCategory Data
   */
  def add(entity: EntityWithNoId): Future[Id] =
    RunDBAction(ToDoCategoryTable) { slick =>
      slick returning slick.map(_.id) += entity.v
    }

  /**
   * Update ToDoCategory Data
   */
  def update(entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
    RunDBAction(ToDoCategoryTable) { slick =>
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
   * Delete ToDoCategory Data
   */
  def remove(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(ToDoCategoryTable) { slick =>
      val row = slick.filter(_.id === id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.delete
        }
      } yield old
    }
}