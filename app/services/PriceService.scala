package services

import com.google.inject.ImplementedBy

import scala.concurrent.Future
import scala.util.Random
import model.{Price, ProductId}

@ImplementedBy(classOf[FlakyMockPriceService])
trait PriceService {
  def price(productId: ProductId): Future[Option[Price]]
}

object MockPriceService extends PriceService {
  def price(productId: ProductId): Future[Option[BigDecimal]] = ???
}

class FlakyMockPriceService extends PriceService {
  def price(productId: ProductId) = {
    val isFailure = Random.nextInt(4) == 0
    if (isFailure) Future.failed(new Exception("Someone snipped the network cable!"))
    else MockPriceService.price(productId)
  }
}