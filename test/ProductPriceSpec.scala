import org.scalatestplus.play.PlaySpec
import services.{FlakyMockPriceService, MockProductService}

class ProductPriceSpec extends PlaySpec {

  "ProductPrice" should {

    "having CategoryId 'frisbees' get ProductIds '133' and '115' and corresponding prices 133.33 and 115.15" in {
      val prodSvc = new MockProductService
      val priceSvc = new FlakyMockPriceService
      for {
        products <- prodSvc.category("frisbees")
        prices <- priceSvc.prices(products.right.map(it => it.map(seq => seq.map(_._1))))
      }
    }
  }
}
