package controllers

import javax.inject._

import play.api.mvc._
import play.twirl.api.HtmlFormat
import model.CategoryId
import services.{PriceService, ProductService}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (productService: ProductService, priceService: PriceService)(implicit executionContext:ExecutionContext) extends Controller {

  /**
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index: Action[AnyContent] = Action.async {
    Try {
      for {
        categories <- productService.categories
      } yield Ok(views.html.index(categories))
    } match {
      case Success(futureResult) => futureResult
      case _ => Future.successful(Ok(HtmlFormat.raw("<h1>Good Luck!</h1><p>Don't forget about error handling</p>")))
    }
  }

  def category(categoryId: CategoryId) = Action.async {
    Try {
      for {
        name <- productService.categoryName(categoryId)
        categoryStatus <- productService.category(categoryId)
        products = productService.products(categoryStatus)
        prices <- priceService.prices(products.unzip._1)

        (categoryName, categoryProducts, productsPrices) = categoryStatus match {
          case Left(s) => (Left(s), None, None)
          case Right(_) => (Right(name), Some(products), prices)
        }
      } yield Ok(views.html.products(categoryId, categoryName, categoryProducts))
    } match {
      case Success(result) => result
      case _ => Future.successful(Ok(HtmlFormat.raw("<h1>Failed get products for category</h1>")))
    }
  }
}
