package services

import com.google.inject.ImplementedBy

import scala.concurrent.Future
import scala.util.{Random, Success, Try}
import model.{Price, ProductId}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

@ImplementedBy(classOf[FlakyMockPriceService])
trait PriceService {
  def price(productId: ProductId): Future[Option[Price]]
//  def prices(xs: Seq[ProductId]): Future[Map[ProductId, BigDecimal]]
  def prices(xs: Seq[ProductId]): Future[Map[ProductId, Option[Price]]] = Future {
    xs.map { it: ProductId =>
      Try{
        for {
          pr: Option[Price] <- price(it)
        } yield pr
      } match {
        case Success(res: Option[Price]) => (it, res)
        case _ => (it, None)
      }
    }.toMap
  }
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

  /*
  def prices(xs: Seq[ProductId]): Future[Map[ProductId, Option[BigDecimal]]] = Future {
    xs.map { it: ProductId =>
      Try{
        for {
          pr: Option[BigDecimal] <- price(it)
        } yield pr
      } match {
        case Success(res: Option[BigDecimal]) => (it, res)
        case _ => (it, None)
      }
    }.toMap
  }
  */
}

class FlakyMockPriceService extends PriceService {
  def price(productId: ProductId) = {
//    Future.failed(new Exception("Someone snipped the network cable!"))
    val isFailure = Random.nextInt(4) == 0
    if (isFailure) Future.failed(new Exception("Someone snipped the network cable!"))
    else MockPriceService.price(productId)
  }
//  def prices(xs: Seq[ProductId]): Future[Map[ProductId, Option[BigDecimal]]] =
//    MockPriceService.prices(xs)
}