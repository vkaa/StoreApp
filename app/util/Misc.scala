package util

import scala.concurrent.Future
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

object Misc {
  def unoption_2[A, B](x: (A, Option[B])): Option[(A, B)] =
    x._2 match {
      case Some(v) => Some(x._1 -> v)
      case None => None
    }

  trait ToEitherString[A] {
    def toEither: Either[String, A]
  }

  // convert Try[A] to Either[String, A]
  implicit def tryToEither[A](x: Try[A]): ToEitherString[A] = new ToEitherString[A] {
    def toEither = x.map(Right(_)).recover{ case e => Left(e.getMessage) }.get
  }

  trait ToFutureEitherString[A] {
    def toFutureEither: Future[Either[String, A]]
  }

  // convert a function producing Future[A] to Future[Either[String, A]]
  implicit def tryToFutureEither[A](x: => Future[A]): ToFutureEitherString[A] =
    new ToFutureEitherString[A] {
      // should return Left for failed future *** FAILED ***
      /*
      def toFutureEither = for {
        y <- x
        z = Try { y }
          .map(Right(_)).recover{ case e => Left(e.getMessage) }.get
      } yield z
      */

      // should return Left for failed future *** FAILED ***
      /* */
      def toFutureEither = for {
        y <- x
        z = try Right(y)
        catch { case e: Exception => Left(e.getMessage) }
      } yield z
      /* */

      /*
      def toFutureEither =
        x.flatMap( r =>
          Future.successful(
            try Right(r)
            catch { case e: Exception => Left(e.getMessage) }
          )
        )
      */

      /*
      def toFutureEither =
        Future.successful(
          try Right(x)
          catch { case e: Exception => Left(e.getMessage) }
        )
      */
    }
}
