package services

import com.google.inject.ImplementedBy

import scala.concurrent.Future
import scala.util.{Random, Success, Try}
import model.{Price, PriceStatus, ProductId}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import util.Misc.tryToFutureEither

@ImplementedBy(classOf[FlakyMockPriceService])
trait PriceService {
  def price(productId: ProductId): Future[Option[Price]]
  def prices(xs: Seq[ProductId]): Future[Seq[(ProductId, Option[Price])]]
  def priceStatus1(productId: ProductId): Future[PriceStatus] =
    for {
      x <- price(productId)
      y = Try {
        x
      }.map(Right(_)).recover({case e: Exception => Left(e.getMessage)}).get
    } yield y

  def priceStatus(productId: ProductId): Future[PriceStatus] =
    price(productId).toFutureEither

  def f1(productId: ProductId): Either[String, Future[Option[Price]]] =
    Try { price(productId) }
    .map(Right(_)).recover({case e: Exception => Left(e.getMessage)}).get

//    Try {
//      for {
//        pr <- price(productId)
//      } yield pr
//    }.map()
//    Try(price(productId)).map(Right(_)).recover{case e: Exception => Left(e.getMessage)}
//    for {
//    pr <- Try(price(productId)).toEither
//    pr <- Try(price(productId))
//      .map(Right(_)).recover{case e: Exception => Left(e.getMessage)}
//  } yield pr.map(Right(_)).recover{case e: Exception => Left(e.getMessage)}
}

object MockPriceService extends PriceService {

  private val mockPrice: Map[ProductId, Price] = Map(
    "133" -> 133.33,
    "115" -> 115.15,
    "103" -> 103.03,
    "112" -> 112.12,
    "109" -> 109.09,
    "701" -> 701.01,
    "553" -> 553.53
  )

  def price(productId: ProductId): Future[Option[Price]] = Future {
    mockPrice get productId
  }

  def pricesAsFutures(xs: Seq[ProductId])(implicit price: ProductId => Future[Option[Price]]): Seq[Future[(ProductId, Option[Price])]] =
    xs.map { it: ProductId =>
//      for { // fails ProductPrice should handle Flaky Price Service test
//        pr: Option[Price] <- price(it)
//      } yield (it, pr)
      Try { // passes ProductPrice should handle Flaky Price Service test
        // but gives execution exception "Someone snipped the network cable!"
        // TODO: address execution exception "Someone snipped the network cable!"
        for {
          pr: Option[Price] <- price(it)
        } yield (it, pr)
      } match {
        case Success(futureResult) => futureResult
        case _ => Future.successful(it -> None)
      }
    }

  def prices(xs: Seq[ProductId]): Future[Seq[(ProductId, Option[Price])]] = {
    implicit val pr: ProductId => Future[Option[Price]] = MockPriceService.price
    Future.sequence { pricesAsFutures(xs) }
  }
}

class FlakyMockPriceService extends PriceService {

  def price(productId: ProductId) = {
    val isFailure = Random.nextInt(4) == 0
    if (isFailure) Future.failed(new Exception("Someone snipped the network cable!"))
    else MockPriceService.price(productId)
  }

  def prices(xs: Seq[ProductId]): Future[Seq[(ProductId, Option[Price])]] = {
//    implicit val pr: ProductId => Future[Option[Price]] = price
    implicit val pr: ProductId => Future[Option[Price]] = MockPriceService.price
    Future.sequence { MockPriceService.pricesAsFutures(xs) }
  }
}