package object model {
  type ProductId = String
  type CategoryId = String
  type Price = BigDecimal
  type CategoryStatus = Either[ // many things can go wrong
    String, // categoryId doesn't exist
    Option[ // categoryId exists but there are no products
      Seq[  // collection of
        (ProductId, // productId
          Option[String] // productName
          )
        ]
  ]]
  type PriceStatus = Either[ // things can go wrong
    String, // Error getting price
    Option[Price] // Possibility of not-priced product
  ]
}
