package controllers

import javax.inject.{Inject, Singleton}

import model.ProductId
import play.api.mvc._
import play.twirl.api.HtmlFormat

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductController @Inject() ()(implicit executionContext:ExecutionContext) extends Controller {

  def product(productId: ProductId) = Action.async {
    Future.successful(Ok(HtmlFormat.raw("<h1>ProductController.product stub</h1>")))
  }
}
