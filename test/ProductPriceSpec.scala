import model.{Price, ProductId}
import org.scalatestplus.play.PlaySpec
import services.{FlakyMockPriceService, MockPriceService, MockProductService}

import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class ProductPriceSpec extends PlaySpec {

  "ProductPrice" should {

    "having CategoryId 'frisbees' get ProductIds '133' and '115' and corresponding prices 133.33 and 115.15" in {
      val prodSvc = new MockProductService
//      val priceSvc = new FlakyMockPriceService
      val priceSvc = MockPriceService
      val prices: Seq[(ProductId, Option[Price])] = Await.result(
            for {
              products <- prodSvc.category("frisbees")
              optOfSeq: Option[Seq[(ProductId, Option[String])]] = products match {
                case Left(_) => None
                case Right(x) => x
              }
              seqOfPairs: Seq[(ProductId, Option[String])] = optOfSeq.getOrElse(List())
              (seqOfIds: Seq[ProductId], _) = seqOfPairs.unzip
              prices <- priceSvc.prices(seqOfIds)
            } yield prices,
          1.seconds
        )
      prices.length mustBe 2
      val priceMap = prices.toMap
      priceMap.get("133").flatten mustBe Some(133.33)
      priceMap.get("115").flatten mustBe Some(115.15)
    }
  }
}
