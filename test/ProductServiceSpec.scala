import model.ProductCategory
import org.scalatestplus.play.PlaySpec
import services.{MockProductService, ProductService}

import scala.concurrent.Await
import scala.concurrent.duration._

class ProductServiceSpec extends PlaySpec {

  "ProductCategory collection" should {
    val svc: ProductService = new MockProductService
    val cats = Await.result(svc.categories, 1.milliseconds)

    "have 4 elements" in {
      cats.length mustBe 4
    }

    implicit val byName = ProductCategory.byName

    "be sorted by name" in {
      cats mustBe sorted
    }

    "have id 'frisbees'" in {
      cats.filter { _.id == "frisbees" }.length mustBe 1
    }

    "have id 'frisbees' with name 'Frisbees'" in {
      cats.filter { _.id == "frisbees" }(0).name mustBe "Frisbees"
    }
  }
}
