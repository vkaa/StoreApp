package services

import com.google.inject.ImplementedBy

import model.{CategoryId, ProductCategory, ProductId, ProductInfo}

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

@ImplementedBy(classOf[MockProductService])
trait ProductService {
  def categoryName(categoryId: CategoryId): Future[Option[String]]
  def categories: Future[Seq[ProductCategory]]
  def category(categoryId: CategoryId): Future[Option[Seq[ProductId]]]
  def productDetails(productId: ProductId): Future[Option[ProductInfo]]
}

class MockProductService extends ProductService {

  private val mockProductData = Map(
    "133" -> ProductInfo("133", "Whamo Super Disc", Map("weight" -> "16oz", "colour" -> "red")),
    "115" -> ProductInfo("115", "Acme Ankle Buster", Map("weight" -> "48oz", "colour" -> "blue")),
    "103" -> ProductInfo("103", "Tabby", Map("weight" -> "15 lbs", "colour" -> "tabby")),
    "112" -> ProductInfo("112", "Unlucky", Map("weight" -> "7 lbs", "colour" -> "black")),
    "109" -> ProductInfo("109", "Standard Witches Model", Map("weight" -> "18 lbs", "colour" -> "black")),
    "701" -> ProductInfo("701", "40 Creek", Map("ABV" -> "40%", "colour" -> "amber", "volume" -> "1L")),
    "553" -> ProductInfo("553", "Jameson's", Map("ABV" -> "40%", "colour" -> "gold", "volume" -> "1.44L"))
  )

  private val mockCategoriesData = Map(
    "frisbees" -> Seq("133", "115"),
    "cats" -> Seq("103", "112", "109"),
    "liquor" -> Seq("701", "553")
  )

  private val mockCategories: Map[CategoryId, String] = List("frisbees", "cats", "liquor", "no products")
    .map { it => it -> it.toLowerCase.capitalize }.toMap

  override def categoryName(categoryId: CategoryId) = Future {
    mockCategories get categoryId
  }

  override def categories = Future {
    implicit val byName = ProductCategory.byName
    mockCategories.map { case (k, v) => ProductCategory(k, v) }.toSeq.sorted
  }

  override def category(categoryId: CategoryId) = Future {
    mockCategoriesData get categoryId
  }

  override def productDetails(productId: ProductId) = ???
}
