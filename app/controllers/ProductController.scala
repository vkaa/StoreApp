package controllers

import javax.inject.Singleton

import model.ProductId
import play.api.mvc._

@Singleton
class ProductController extends Controller {

  def product(productId: ProductId) = ???
}
