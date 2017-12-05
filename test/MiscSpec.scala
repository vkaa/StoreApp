import org.scalatestplus.play.PlaySpec

import scala.concurrent.{Await, Future}
import util.Misc.tryToFutureEither

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.util.control.NonFatal

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

    "test Exception on NonFatal" in {
      println(s"Exception NonFatal: ${NonFatal(new Exception("Someone snipped the network cable!"))}")
    }

    "test Exception on NonFatal with Try of Success" in {
      try Success(throw new Exception("Someone snipped the network cable!"))
      catch {
         case NonFatal(e) => println(s"NonFatal '${e.getMessage}'")
         case _: Throwable => println("FATAL")
      }
    }

    "test Exception on NonFatal with Try" in {
      try { throw new Exception("Someone snipped the network cable!") }
      catch {
        case NonFatal(e) => println(s"NonFatal '${e.getMessage}'")
        case e: Throwable => println(s"Throwable '${e.getMessage}'")
      }
    }

    def futFailed(v: BigDecimal): Future[BigDecimal] =
      Future.failed(new Exception("Someone snipped the network cable!"))

    "test try Await.result" in {
      try {
        val ret = Await.result(futFailed(7), 1 .seconds)
        println(s"Await.result(futFailed(7), 1 .seconds): ${ret}")
      }
      catch {
        case NonFatal(e) => println(s"NonFatal '${e.getMessage}'")
        case e: Throwable => println(s"Throwable '${e.getMessage}'")
        case _: Throwable => println(s"case _")
      }
    }

    "test Try Await.result match" in {
      Try { Await.result(futFailed(7), 1 .seconds) } match {
        case Success(r) => println(s"Success '${r}'")
        case Failure(r) => println(s"Failure '${r}'")
      }
    }

    "test failed applied to future" in {
      val applyFailed = futFailed(7).failed
      val v = Await.result(applyFailed, 1 .seconds)
      println(s"applyFailed: ${v.getClass}")
      println(s"applyFailed value: ${v.getMessage}")
    }

    /*
    "test transform on failed future" in {
      val transformed = futFailed(7).transform(
        res => Right(res),
        res => Left(res)
      )
    }
    */

    /*
    "test Await.result value" in {
      val v = Await.result(futFailed(7), 1 .seconds)
      println(s"Await.result: ${v.getClass}")
    }
    */

    /*
    "return Left for failed future" in {
//      val futEither = futFailed(7).toFutureEither
      val futEither: Future[Either[String, BigDecimal]] = for {
        x <- futFailed(7)
//        x <- futFailed(7).recoverWith({case e: Exception => Left(e.getMessage)})
        y = Try {
          x
        }.map(Right(_)).recover({case e: Exception => Left(e.getMessage)}).get
//        }.map(Right(_)).recoverWith({case e: Exception => Left(e.getMessage)}).get
//        }.recover({case e: Exception => Left(e.getMessage)}).get
//        }.recoverWith({case e: Exception => Left(e.getMessage)}).get
        /*
        y <- Try {
          futFailed(7)
        }.map(Right(_)).recover({case e: Exception => Left(e.getMessage)}).get
        */
      } yield y

      val futValue = Await.result(futEither, 1.seconds)
      futValue.isLeft mustBe true
      futValue.left.get mustBe "Someone snipped the network cable!"
    }
    */
  }
}
