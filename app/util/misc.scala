package util

import scala.util.Try

object Misc {
  def unoption_2[A, B](x: (A, Option[B])): Option[(A, B)] =
    x._2 match {
      case Some(v) => Some(x._1 -> v)
      case None => None
    }

  trait ToEitherString[A] {
    def toEither: Either[String, A]
  }

  implicit def tryToEither[A](x: Try[A]): ToEitherString[A] = new ToEitherString[A] {
    def toEither = x.map(Right(_)).recover{ case e => Left(e.getMessage) }.get
  }
}
