import model.{CategoryStatus, ProductCategory, ProductId}
import org.scalatestplus.play.PlaySpec
import services.{MockProductService, ProductService}

import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

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

  "Product category" should {
    val svc: ProductService = new MockProductService

    "find 'frisbees'" in {
      val catStatus: CategoryStatus = Await.result(svc.category("frisbees"), 1.seconds)
      catStatus.isRight mustBe true
    }

    "not find 'no category'" in {
      val catStatus: CategoryStatus = Await.result(svc.category("no category"), 1.seconds)
      catStatus.isLeft mustBe true
    }

    "find products for category 'frisbees'" in {
      val catStatus: CategoryStatus = Await.result(svc.category("frisbees"), 1.seconds)
      catStatus match {
        case Left(_) => fail("have NOT found category")
        case Right(optProducts) => optProducts match {
          case Some(products) => products.length mustBe 2
          case None => fail("have NOT found products")
        }
      }
    }

    "find no products for category 'no products'" in {
      val catStatus: CategoryStatus = Await.result(svc.category("no products"), 1.seconds)
      catStatus match {
        case Left(_) => fail("have NOT found category")
        case Right(optProducts) => optProducts match {
          case Some(products) => fail("NOT suppose to find products")
          case None => succeed
        }
      }
    }
  }

  "Product products" should {
    val svc: ProductService = new MockProductService

    "find products with names in category 'frisbees'" in {
      val products: Seq[(ProductId, Option[String])] = Await.result(
        for {
          catStatus <- svc.category("frisbees")
          products = svc.products(catStatus)
        } yield products,
        1.seconds)
      products.length mustBe 2
      val productMap = products.toMap
      productMap.get("133").flatten mustBe Some("Whamo Super Disc")
      productMap.get("115").flatten mustBe Some("Acme Ankle Buster")
    }
  }
}
