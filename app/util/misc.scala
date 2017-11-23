package util

object Misc {
  def unoption_2[A, B](x: (A, Option[B])): Option[(A, B)] =
    x._2 match {
      case Some(v) => Some(x._1 -> v)
      case None => None
    }
}
