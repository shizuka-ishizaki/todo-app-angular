/**
 *
 * to do sample project
 *
 */

package controllers.todo

import json.writes.{JsValueWritesToDoWithCategory, JsValueWritesApiResult, JsValueWritesCategoryList}
import json.reads.{JsValueReadsToDoId, JsValueReadsToDoAdd}
import lib.model.{ToDo, ToDoCategory}
import lib.model.ToDo.ToDoStatus
import lib.model.ToDoCategory.CategoryColor
import lib.persistence.default.{ToDoCategoryRepository, ToDoRepository}
import model._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.libs.json._

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * 追加・更新用Form
 */
case class ToDoFormData (
  title:      String,
  body:       String,
  status:     Short,
  categoryId: Long,
)

@Singleton
class ToDoController @Inject()(val controllerComponents: ControllerComponents)
    extends BaseController with I18nSupport {

  /**
   * ToDo登録用のFormオブジェクト
   */
  val toDoForm = Form(
    mapping(
      "title"       -> nonEmptyText,
      "body"        -> nonEmptyText,
      "status"      -> shortNumber(min = 0, max = 2),
      "categoryId"  -> longNumber
    )(ToDoFormData.apply)(ToDoFormData.unapply)
  )

  /**
   * ToDo一覧画面の表示用
   */
  def list() = Action async {implicit request: Request[AnyContent] =>
    for {
      todoSeq <- ToDoRepository.all()
      categorySeq <- ToDoCategoryRepository.all()
    } yield {

      val todoList = todoSeq.map { todo =>
        val category = categorySeq.find(c => c.id == todo.v.categoryId)
        ToDoWithCategory(
          todo.id,
          todo.v.categoryId,
          todo.v.title,
          todo.v.body,
          todo.v.status,
          categorySeq.collectFirst{case i if i.id == todo.v.categoryId => i.v.name}.getOrElse(""),
          categorySeq.collectFirst{case i if i.id == todo.v.categoryId => i.v.color}.getOrElse(CategoryColor.NONE),
        )
      }
      val vv = ViewValueToDoList(
        title    = "ToDoリスト",
        cssSrc   = Seq("main.css"),
        jsSrc    = Seq("main.js",  "todo/list.js"),
        toDoList = todoList
      )
      Ok(views.html.todo.List(vv))
    }
  }

  /**
   * 登録画面の表示用
   */
  def register() = Action async { implicit request: Request[AnyContent] =>
    for {
      categorySeq <- ToDoCategoryRepository.all()
    } yield {
      val vv = ViewValueToDoAdd(
        title     = "ToDo登録",
        cssSrc    = Seq("main.css", "todo/add.css"),
        jsSrc     = Seq("main.js"),
        toDoForm  = toDoForm,
        categorys = categorySeq.map( category =>
          (category.id.toString -> category.v.name)
        )
      )
      Ok(views.html.todo.Add(vv))
    }
  }

  /**
   * 登録処理
   */
  def add() = Action async { implicit request: Request[AnyContent] =>
    // foldでデータ受け取りの成功、失敗を分岐しつつ処理が行える
    toDoForm
      .bindFromRequest()
      .fold(
        // 処理が失敗した場合に呼び出される関数
        // 処理失敗の例: バリデーションエラー
        (formWithErrors: Form[ToDoFormData]) => {
          for {
            categorySeq <- ToDoCategoryRepository.all()
          } yield {
            val vv = ViewValueToDoAdd(
              title     = "ToDo登録",
              cssSrc    = Seq("main.css", "todo/add.css"),
              jsSrc     = Seq("main.js"),
              toDoForm  = formWithErrors,
              categorys = categorySeq.map( category =>
                (category.id.toString -> category.v.name)
              )
            )
            BadRequest(views.html.todo.Add(vv))
          }
      },

      // 処理が成功した場合に呼び出される関数
      (toDoForm: ToDoFormData) => {
        // 登録が完了したら一覧画面へリダイレクトする
        val todoWithNoId = new ToDo(
          id          = None,
          categoryId  = ToDoCategory.Id(toDoForm.categoryId),
          title       = toDoForm.title,
          body        = toDoForm.body,
          status      = ToDoStatus.apply(toDoForm.status),
        ).toWithNoId
        for {
          _ <- ToDoRepository.add(todoWithNoId)
        } yield {
          Redirect(routes.ToDoController.list())
            .flashing("success" -> "ToDoを追加しました!")
        }
      }
    )
  }

  /**
   * 編集画面を開く
   */
  def edit(id: Long) = Action async { implicit request: Request[AnyContent] =>
    val toDoId = ToDo.Id(id)
    for {
      todo        <-  ToDoRepository.get(toDoId)
      categorySeq <-  ToDoCategoryRepository.all()
    } yield {
      todo match {
        case Some(todo) =>
          val vv = ViewValueToDoEdit(
            title     = "ToDo編集",
            cssSrc    = Seq("main.css", "todo/edit.css"),
            jsSrc     = Seq("main.js"),
            id        = toDoId,
            toDoForm  = toDoForm.fill(
              ToDoFormData(
                todo.v.title,
                todo.v.body,
                todo.v.status.code,
                todo.v.categoryId,
              )
            ),
            categorys = categorySeq.map( category =>
              (category.id.toString -> category.v.name)
            )
          )
          Ok(views.html.todo.Edit(vv))
        case None       =>
          val vv = ViewValueError(
            title     = "404",
            cssSrc    = Seq("main.css"),
            jsSrc     = Seq("main.js"),
            message   = "ページが見つかりません。"
          )
          NotFound(views.html.error.Page404(vv))
      }
    }
  }

  /**
   * 更新処理
   */
  def update(id: Long) = Action async { implicit request: Request[AnyContent] =>
    // foldでデータ受け取りの成功、失敗を分岐しつつ処理が行える
    toDoForm
        .bindFromRequest()
        .fold(
          // 処理が失敗した場合に呼び出される関数
          // 処理失敗の例: バリデーションエラー
          (formWithErrors: Form[ToDoFormData]) => {
            for {
              categorySeq <- ToDoCategoryRepository.all()
            } yield {
              val vv = ViewValueToDoEdit(
                title     = "ToDo編集",
                cssSrc    = Seq("main.css", "todo/edit.css"),
                jsSrc     = Seq("main.js"),
                id        = id,
                toDoForm  = formWithErrors,
                categorys = categorySeq.map( category =>
                  (category.id.toString -> category.v.name)
                )
              )
              BadRequest(views.html.todo.Edit(vv))
            }
          },

          // 処理が成功した場合に呼び出される関数
          (toDoForm: ToDoFormData) => {
            val todoWithNoId = new ToDo(
              id          = Some(ToDo.Id(id)),
              categoryId  = ToDoCategory.Id(toDoForm.categoryId),
              title       = toDoForm.title,
              body        = toDoForm.body,
              status      = ToDoStatus.apply(toDoForm.status),
            ).toEmbeddedId
            for {
              result <- ToDoRepository.update(todoWithNoId)
            } yield {
              result match {
                case None        =>
                  val vv = ViewValueError(
                    title     = "404",
                    cssSrc    = Seq("main.css"),
                    jsSrc     = Seq("main.js"),
                    message   = "ページが見つかりません。"
                  )
                  NotFound(views.html.error.Page404(vv))
                case Some(_)  =>
                  Redirect(routes.ToDoController.list())
                    .flashing("success" -> "ToDoを更新しました!")
              }
            }
          }
        )
  }

  /**
   * 削除処理
   */
  def delete(id: Long) = Action async { implicit request: Request[AnyContent] =>
    // requestから直接値を取得するサンプル
    // val idOpt = request.body.asFormUrlEncoded.get("id").headOption
    for {
      result <- ToDoRepository.remove(ToDo.Id(id))
    } yield {
      result match {
        case None        =>
          val vv = ViewValueError(
            title     = "404",
            cssSrc    = Seq("main.css"),
            jsSrc     = Seq("main.js"),
            message   = "ページが見つかりません。"
          )
          NotFound(views.html.error.Page404(vv))
        case Some(_)  =>
          Redirect(routes.ToDoController.list())
            .flashing("success" -> "ToDoを削除しました!")
      }
    }
  }

  // API
  /**
   * ToDo一覧画面の表示用
   */
  def listApi() = Action async {implicit request: Request[AnyContent] =>
    for {
      todoSeq <- ToDoRepository.all()
      categorySeq <- ToDoCategoryRepository.all()
    } yield {
      val todoList = todoSeq.map { todo =>
        JsValueWritesToDoWithCategory(
          todo.id.toString,
          todo.v.categoryId.toString,
          todo.v.title,
          todo.v.body,
          todo.v.status.name,
          categorySeq.collectFirst{case i if i.id == todo.v.categoryId => i.v.name}.getOrElse(""),
          categorySeq.collectFirst{case i if i.id == todo.v.categoryId => i.v.color.name}.getOrElse(CategoryColor.NONE.name),
          "/todo/" + todo.id.toString + "/edit"
        )
      }
      Ok(Json.toJson(todoList))
    }
  }

  /**
   * 削除処理
   */
  def deleteApi() = Action async {implicit request =>
   val jsonId = request.body.asJson.get.validate[JsValueReadsToDoId].get

    for {
      result <- ToDoRepository.remove(ToDo.Id(jsonId.id.toLong))
    } yield {
      result match {
        case None     =>
          Ok(Json.toJson(JsValueWritesApiResult(isSuccess = false)))
        case Some(_)  =>
          Ok(Json.toJson(JsValueWritesApiResult(isSuccess = true)))
      }
    }
  }

  /**
   * カテゴリ一覧取得
   */
  def categoryList() = Action async {implicit request: Request[AnyContent] =>
    for {
      categorySeq <- ToDoCategoryRepository.all()
    } yield {
      val list = categorySeq.map { category =>
        JsValueWritesCategoryList(
          category.id.toString,
          category.v.name,
        )
      }
      Ok(Json.toJson(list))
    }
  }

  /**
   * 作成処理
   */
  def addApi() = Action async {implicit request =>
    val jsonToDo = request.body.asJson.get.validate[JsValueReadsToDoAdd].get
    
    // 登録が完了したら一覧画面へリダイレクトする
    val todoWithNoId = new ToDo(
      id          = None,
      categoryId  = ToDoCategory.Id(jsonToDo.category.toLong),
      title       = jsonToDo.title,
      body        = jsonToDo.body,
      status      = ToDoStatus.TODO,
    ).toWithNoId
    for {
      _ <- ToDoRepository.add(todoWithNoId)
    } yield {
      Ok(Json.toJson(JsValueWritesApiResult(isSuccess = true)))
    }
  }
}
