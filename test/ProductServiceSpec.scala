import model.ProductCategory
import org.scalatestplus.play.PlaySpec
import services.{MockProductService, ProductService}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class ProductServiceSpec extends PlaySpec {

  "getCategories" should {

    "return empty collection for empty CategoriesData" in {
      MockProductService.getCategories(Map()) mustBe empty
    }

    "return Product Categories 'cat1', 'cat2', 'cat3'" in {
      val categories = MockProductService.getCategories(Map(
        "cat2" -> Seq(),
        "cat3" -> Seq(),
        "cat1" -> Seq()
      ))
      categories.length mustBe 3
      categories contains "cat1"
      categories contains "cat2"
      categories contains "cat3"
    }
  }

  "categories" should {
    val svc: ProductService = new MockProductService
    "return Product Categories sorted by name" in {
      val catsFuture = svc.categories
      catsFuture onSuccess {
        case cats: Seq[ProductCategory] => cats.sortBy(_.name) mustBe cats
        case _ => assert(false, "Wrong result from ProductService.categories")
      }
      catsFuture onFailure {
        case _ => assert(false, "ProductService.categories should not resolve to failure")
      }
    }
  }
}
