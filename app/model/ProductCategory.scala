package model

case class ProductCategory(id: CategoryId, name: String)

object ProductCategory {
  val byId = Ordering.by { it: ProductCategory => it.id }
  val byName = Ordering.by { it: ProductCategory => it.name }
}
