import org.scalatestplus.play.PlaySpec
import services.{FlakyMockPriceService, MockPriceService, PriceService}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
//import play.api.libs.concurrent.Execution.Implicits.defaultContext
import util.control.Breaks._

class PriceServiceSpec extends PlaySpec {

  "PriceService" should {

    "have price for product id '133'" in {
      val svc: PriceService = MockPriceService
      val pr = Await.result(svc.price("133"), 1.milliseconds)
      pr mustBe Some(133.33)
    }

    "not have price for product id '777'" in {
      val svc: PriceService = MockPriceService
      val pr = Await.result(svc.price("777"), 1.milliseconds)
      pr mustBe None
    }

    "handle failure of getting price" in {
      val svc: PriceService = new FlakyMockPriceService
      breakable {
        for (i <- 1 to 10) {
          //          println(s"svc.price attempt: $i")
          Try {
            Await.result(svc.price("133"), 1.milliseconds)
          } match {
            case Success(price) => {
              //              println(s"svc.price success: $price")
              price mustBe Some(133.33)
            }
            case Failure(e) => {
              //              println(s"svc.price failure: $e")
              e.getMessage mustBe "Someone snipped the network cable!"
              break
            }
          }
        }
      }
    }
  }
}
