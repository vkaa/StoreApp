package util

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}
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
      override def toFutureEither: Future[Either[String, A]] = {
        val p = Promise[Either[String, A]]()
        x.onComplete {
          case Success(r) => p complete Try(Right(r))
          case Failure(e) => p complete Try(Left(e.getMessage))
        }
        p.future
      }
//      override def toFutureEither: Future[Either[String, A]] = x.transform {
//        case Success(r) => Try(Right(r))
//        case Failure(e) => Try(Left(e.getMessage))
//      }
    }
  /*
    new ToFutureEitherString[A] {
      def toFutureEither = for {
        y <- x
        z = Try { y }
          .map(Right(_)).recover{ case e => Left(e.getMessage) }.get
      } yield z
    }
    */

//  trait ToTryEitherString[A] {
//    def toTryEither: Future[Either[String, A]]
//  }


  /*
  type SafeResult[A] = Either[String, A]

  trait ToSafeResult[A] {
    def toSafeResult: SafeResult[A]
  }
  */

  /*
  implicit def toFutureSafeResult[A](x: => Future[A]): Future[ToSafeResult[A]] = Future {
    new ToSafeResult[A] {
      override def toSafeResult: SafeResult[A] = {
        val p = Promise[SafeResult[A]]()
        x.onComplete {
          case Success(r) => p complete Try(Right(r))
          case Failure(e) => p complete Try(Left(e.getMessage))
        }
        p.future
      }
    }
  }
  */

  /*
  implicit def toFutureSafeResult[A](x: => Future[A]): Future[ToSafeResult[A]] =
    new ToSafeResult[A] {
      override def toSafeResult: SafeResult[A] = {
        val p = Promise[SafeResult[A]]()
        x.onComplete {
          case Success(r) => p complete Try(Right(r))
          case Failure(e) => p complete Try(Left(e.getMessage))
        }
        p.future
      }
    }
  */

  type SafeResult[A] = Either[String, A]

  trait ToFutureSafeResult[A] {
    def toFutureSafeResult: Future[SafeResult[A]]
  }

  /* TODO
  implicit def tryToFutureSafeResult[A](x: => Future[A]): Future[SafeResult[A]] =
    new ToFutureSafeResult[A] {
      override def toFutureSafeResult: Future[SafeResult[A]] = {
        val p = Promise[SafeResult[A]]()
        p.future
      }
    }
  */
}
