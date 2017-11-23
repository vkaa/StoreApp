import model.{Price, ProductId}
import org.scalatestplus.play.PlaySpec
import services.{FlakyMockPriceService, MockPriceService, MockProductService}

import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import util.Misc.unoption_2

class ProductPriceSpec extends PlaySpec {

  "ProductPrice" should {

    "having CategoryId 'frisbees' get ProductIds '133' and '115' and corresponding prices 133.33 and 115.15" in {
      val prodSvc = new MockProductService
      val priceSvc = MockPriceService
      val optPrices: Seq[(ProductId, Option[Price])] = Await.result(
        for {
          catStatus <- prodSvc.category("frisbees")
          products = prodSvc.products(catStatus)
          (seqOfIds: Seq[ProductId], _) = products.unzip
          prices <- priceSvc.prices(seqOfIds)
        } yield prices,
        1.seconds
      )

      optPrices.length mustBe 2

      val prices: Seq[(ProductId, Price)] = optPrices.flatMap(unoption_2[ProductId, Price])
      val priceMap: Map[ProductId, Price] = prices.toMap
      priceMap.get("133") mustBe Some(133.33)
      priceMap.get("115") mustBe Some(115.15)
    }

    "having CategoryId 'liquor' get ProductIds '701', '553', '777' and corresponding prices 701.01, 553.53 and no price" in {
      val prodSvc = new MockProductService
      val priceSvc = MockPriceService
      val optPrices: Seq[(ProductId, Option[Price])] = Await.result(
        for {
          catStatus <- prodSvc.category("liquor")
          products = prodSvc.products(catStatus)
          (seqOfIds: Seq[ProductId], _) = products.unzip
          prices <- priceSvc.prices(seqOfIds)
        } yield prices,
        1.seconds
      )

      optPrices.length mustBe 3

      val prices: Seq[(ProductId, Price)] = optPrices.flatMap(unoption_2[ProductId, Price])
      val priceMap: Map[ProductId, Price] = prices.toMap
      priceMap.get("701") mustBe Some(701.01)
      priceMap.get("553") mustBe Some(553.53)
      priceMap.get("777") mustBe None
    }

    "handle Flaky Price Service having CategoryId 'liquor' get ProductIds '701', '553', '777' and corresponding prices 701.01, 553.53 and no price" in {
      val prodSvc = new MockProductService
      val priceSvc = new FlakyMockPriceService
      val optPrices: Seq[(ProductId, Option[Price])] = Await.result(
        for {
          catStatus <- prodSvc.category("liquor")
          products = prodSvc.products(catStatus)
          (seqOfIds: Seq[ProductId], _) = products.unzip
          prices <- priceSvc.prices(seqOfIds)
        } yield prices,
        1.seconds
      )

      optPrices.length mustBe 3

      val prices: Seq[(ProductId, Price)] = optPrices.flatMap(unoption_2[ProductId, Price])
      val priceMap: Map[ProductId, Price] = prices.toMap
      priceMap.get("701") mustBe Some(701.01)
      priceMap.get("553") mustBe Some(553.53)
      priceMap.get("777") mustBe None
    }
  }
}
