import org.scalatestplus.play.PlaySpec
import services.{FlakyMockPriceService, MockPriceService, PriceService}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import scala.util.control.Breaks._

class PriceServiceSpec extends PlaySpec {

  "PriceService" should {

    "have price for product id '133'" in {
      val svc: PriceService = MockPriceService
      val pr = Await.result(svc.price("133"), 1.seconds)
      pr mustBe Some(133.33)
    }

    "not have price for product id '777'" in {
      val svc: PriceService = MockPriceService
      val pr = Await.result(svc.price("777"), 1.seconds)
      pr mustBe None
    }

    "handle failure of getting price" in {
      val svc: PriceService = new FlakyMockPriceService
      breakable {
        for (i <- 1 to 10) {
          //          println(s"svc.price attempt: $i")
          Try {
            Await.result(svc.price("133"), 1.seconds)
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

    "have price for product list containing only product id '133'" in {
      val svc: PriceService = MockPriceService
      val pr = Await.result(svc.prices(List("133")), 1.seconds)
      pr.toMap mustBe Map( "133" -> Some(133.33) )
    }

    "have price for product list with product id '133' and no price for product id '777'" in {
      val svc: PriceService = MockPriceService
      val pr = Await.result(svc.prices(List("133", "177")), 1.seconds)
      pr.toMap get "133" flatMap identity mustBe Some(133.33)
      pr.toMap get "777" flatMap identity mustBe None
    }

    "handle failure of getting prices" in {
      val svc: PriceService = new FlakyMockPriceService
      breakable {
        for (i <- 1 to 100) {
//          println(s"svc.price attempt: $i")
          Try {
            Await.result(svc.prices(List("133", "177")), 1.seconds)
          } match {
            case Success(prices) => {
//              println(s"svc.prices success: $prices")
              prices.toMap get "133" flatMap identity mustBe Some(133.33)
              prices.toMap get "777" flatMap identity mustBe None
            }
            case Failure(e) => {
//              println(s"svc.prices failure: $e")
              e.getMessage mustBe "Someone snipped the network cable!"
              break
            }
          }
        }
      }
    }
  }
}
