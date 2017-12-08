import org.scalatestplus.play.PlaySpec

import scala.concurrent.{Await, Future}
import util.Misc.tryToFutureEither

import scala.concurrent.duration._
import scala.util.Try

import play.api.libs.concurrent.Execution.Implicits.defaultContext

class MiscSpec extends PlaySpec{

  "tryToFutureEither" should {

    def futSuccessful(v: BigDecimal): Future[BigDecimal] =
      Future.successful(v)

    "return Right for successful future" in {
      val futEither = futSuccessful(7).toFutureEither
      val futValue = Await.result(futEither, 1.seconds)
      futValue.isRight mustBe true
      futValue.right.get mustBe 7
    }

    def futFailed(v: BigDecimal): Future[BigDecimal] =
      Future.failed(new Exception("Someone snipped the network cable!"))

    "return Left for failed future" in {
      val futEither = futFailed(7).toFutureEither
      /*
      val futEither: Future[Either[String, BigDecimal]] = for {
        x <- futFailed(7)
        y = Try {
          x
        }.map(Right(_)).recover({case e: Exception => Left(e.getMessage)}).get
        /*
        y <- Try {
          futFailed(7)
        }.map(Right(_)).recover({case e: Exception => Left(e.getMessage)}).get
        */
      } yield y
      */

      val futValue = Await.result(futEither, 1.seconds)
      futValue.isLeft mustBe true
      futValue.left.get mustBe "Someone snipped the network cable!"
    }
  }
}
