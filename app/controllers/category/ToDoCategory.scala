/**
 *
 * to do sample project
 *
 */

package controllers.category

import javax.inject._
import play.api.mvc._
import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import lib.persistence.default.{ToDoRepository, ToDoCategoryRepository}
import model.{ViewValueToDoCategoryList, Category, ViewValueError, ViewValueToDoCategoryAdd, ViewValueToDoCategoryEdit}

import lib.model.ToDoCategory
import lib.model.ToDoCategory.CategoryColor

/**
 * 追加・更新用Form
 */
case class ToDoCategoryFormData (
  name:     String,
  slug:     String,
  color:    Short,
)

@Singleton
class ToDoCategoryController @Inject()(val controllerComponents: ControllerComponents)
    extends BaseController with I18nSupport {

  /**
   * ToDo登録用のFormオブジェクト
   */
  val categoryForm = Form(
    mapping(
      "name"    -> nonEmptyText,
      "slug"    -> nonEmptyText.verifying(
                     constraint = _.matches("""^[0-9a-zA-Z]+$"""),
                     error="半角英数だけで入力してください。"
                   ),
      "color"   -> shortNumber(min = 0, max = 4),
    )(ToDoCategoryFormData.apply)(ToDoCategoryFormData.unapply)
  )

  /**
   * ToDoカテゴリー一覧画面の表示用
   */
  def list() = Action async {implicit request: Request[AnyContent] =>
    for {
      categorySeq <- ToDoCategoryRepository.all()
    } yield {
      val categoryList = categorySeq.map { category =>
        Category(
          category.id,
          category.v.name,
          category.v.slug,
          category.v.color,
        )
      }
      val vv = ViewValueToDoCategoryList(
        title           = "ToDoカテゴリーリスト",
        cssSrc          = Seq("main.css", "category/list.css"),
        jsSrc           = Seq("main.js",  "category/list.js"),
        categoryList    = categoryList
      )
      Ok(views.html.category.List(vv))
    }
  }


  /**
   * 登録画面の表示用
   */
  def register() = Action async { implicit request: Request[AnyContent] =>
    for {
      categorySeq <- ToDoCategoryRepository.all()
    } yield {
      val vv = ViewValueToDoCategoryAdd(
        title         = "ToDoカテゴリー登録",
        cssSrc        = Seq("main.css", "category/add.css"),
        jsSrc         = Seq("main.js"),
        categoryForm  = categoryForm,
      )
      Ok(views.html.category.Add(vv))
    }
  }

  /**
   * 登録処理
   */
  def add() = Action async { implicit request: Request[AnyContent] =>
    // foldでデータ受け取りの成功、失敗を分岐しつつ処理が行える
    categoryForm
      .bindFromRequest()
      .fold(
        // 処理が失敗した場合に呼び出される関数
        // 処理失敗の例: バリデーションエラー
        (formWithErrors: Form[ToDoCategoryFormData]) => {
          for {
            categorySeq <- ToDoCategoryRepository.all()
          } yield {
            val vv = ViewValueToDoCategoryAdd(
              title         = "ToDoカテゴリー登録",
              cssSrc        = Seq("main.css", "category/add.css"),
              jsSrc         = Seq("main.js"),
              categoryForm  = formWithErrors,
            )
            BadRequest(views.html.category.Add(vv))
          }
        },

        // 処理が成功した場合に呼び出される関数
        (categoryForm: ToDoCategoryFormData) => {
          // 登録が完了したら一覧画面へリダイレクトする
          val categoryWithNoId = new ToDoCategory(
            id       = None,
            name     = categoryForm.name,
            slug     = categoryForm.slug,
            color    = CategoryColor.apply(categoryForm.color),
          ).toWithNoId
          for {
            _ <- ToDoCategoryRepository.add(categoryWithNoId)
          } yield {
            Redirect(routes.ToDoCategoryController.list())
                .flashing("success" -> "ToDoカテゴリーを追加しました!")
          }
        }
      )
  }

  /**
   * 編集画面の表示用
   */
  def edit(id: Long) = Action async { implicit request: Request[AnyContent] =>
    val toDoCategoryId = ToDoCategory.Id(id)
    for {
      category   <- ToDoCategoryRepository.get(toDoCategoryId)
    } yield {
      category match {
        case Some(category) =>
          val vv = ViewValueToDoCategoryEdit(
            title         = "ToDoカテゴリー登録",
            cssSrc        = Seq("main.css", "category/edit.css"),
            jsSrc         = Seq("main.js"),
            id            = toDoCategoryId,
            categoryForm  = categoryForm.fill(
              ToDoCategoryFormData(
                category.v.name,
                category.v.slug,
                category.v.color.code,
              )
            ),
          )
          Ok(views.html.category.Edit(vv))
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
   * 編集処理
   */
  def update(id: Long) = Action async { implicit request: Request[AnyContent] =>
    // foldでデータ受け取りの成功、失敗を分岐しつつ処理が行える
    categoryForm
        .bindFromRequest()
        .fold(
          // 処理が失敗した場合に呼び出される関数
          // 処理失敗の例: バリデーションエラー
          (formWithErrors: Form[ToDoCategoryFormData]) => {
            for {
              categorySeq <- ToDoCategoryRepository.all()
            } yield {
              val vv = ViewValueToDoCategoryEdit(
                title         = "ToDoカテゴリー登録",
                cssSrc        = Seq("main.css", "category/edit.css"),
                jsSrc         = Seq("main.js"),
                id            = id,
                categoryForm  = formWithErrors,
              )
              BadRequest(views.html.category.Edit(vv))
            }
          },

          // 処理が成功した場合に呼び出される関数
          (categoryForm: ToDoCategoryFormData) => {
            // 更新が完了したら一覧画面へリダイレクトする
            val categoryEmbeddedId = new ToDoCategory(
              id       = Some(ToDoCategory.Id(id)),
              name     = categoryForm.name,
              slug     = categoryForm.slug,
              color    = CategoryColor.apply(categoryForm.color),
            ).toEmbeddedId
            for {
              result <- ToDoCategoryRepository.update(categoryEmbeddedId)
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
                  Redirect(routes.ToDoCategoryController.list())
                      .flashing("success" -> "ToDoカテゴリーを更新しました!")
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
    val toDoCategoryid = (ToDoCategory.Id(id))
    for {
      // 該当しているTodoを削除する場合
      resultToDo      <- ToDoRepository.removeByCategoryId(toDoCategoryid)
      // ”どのカテゴリにも紐づいていない” というデータになるようTodoを更新する場合
      // resultToDo      <- ToDoRepository.updateByCategoryId(toDoCategoryid)
      resultCategory  <- ToDoCategoryRepository.remove(toDoCategoryid)
    } yield {
      (resultToDo, resultCategory) match {
        case (Some(_), Some(_))  =>
          Redirect(routes.ToDoCategoryController.list())
              .flashing("success" -> "ToDoカテゴリーと紐づくToDoを削除しました!")
        case (_, Some(_))  =>
          Redirect(routes.ToDoCategoryController.list())
              .flashing("success" -> "ToDoカテゴリーを削除しました!")
        case _        =>
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
}
