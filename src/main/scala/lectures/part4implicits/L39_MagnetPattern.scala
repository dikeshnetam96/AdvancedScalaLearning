package lectures.part4implicits

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
object L39_MagnetPattern extends App {

  // Magnet pattern is a use case of type classes which aims at solving some of the problems created by method overloading

  class P2PRequest
  class P2PResponse
  class Serializer[T]

  trait Actor {
    def receive(statusCode: Int): Int

    def receive(request: P2PRequest): Int

    def receive(response: P2PResponse): Int

    //    def receive[T](message : T)(implicit serializer: Serializer[T])  // can be reduced to context bond written below
    def receive[T: Serializer](message: T): Int

    def receive[T: Serializer](message: T, statusCode: Int): Int

    def receive(future: Future[P2PRequest]): Int
    // def receive(future: Future[P2PResponse]) : Int  // Not possible
    // lots of overloads
    // generic type are erased at compile time,so after the type eraser receive methods would simply receive a future and that would be it.

    /*
    1 - type erasure
    2 - lifting doesn't work for all overloads

        val receiveFV = receive _ // for '_', compiler will confuse it is either statusCode, request or response.

    3 - code duplication
    4 - default arguments and type inferrence

      actor.receive(?!)  // what default args to be fetch. compiler will get confused
     */
  }
    trait MessageMagnet[Result] {
      def apply() : Result
    }

    def receive[R](magnet : MessageMagnet[R]) : R = magnet()

    implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int] {
      override def apply(): Int = {
        // logic for handling a P2PRequest
        println("handing P2P request....")
        42
      }
    }

    implicit class FromP2PResponse(request: P2PResponse) extends MessageMagnet[Int] {
      override def apply(): Int = {
        // logic for handling a P2PRequest
        println("handing P2P response....")
        24
      }
    }
  println("started....")
    receive(new P2PRequest)
    receive(new P2PResponse)
  println("End....")


  // 1 - no more type erasure problems !
  implicit class FromResponseFuture(future: Future[P2PRequest]) extends MessageMagnet[Int] {
    override def apply(): Int = 2
  }

  implicit class FromRequestFuture(future: Future[P2PResponse]) extends MessageMagnet[Int] {
    override def apply(): Int = 3
  }

  // def receive(future: Future[P2PRequest]): Int
  // def receive(future: Future[P2PResponse]) : Int  // Not possible
  println(receive(Future(new P2PRequest)))
  println(receive(Future(new P2PResponse)))

  // 2 - lifting works
  trait MathLib {
    def add1(x : Int) : Int = x + 1
    def add1(s : String) : Int = s.toInt + 1
  }

  // magnetize
  trait AddMagnet {
    def apply() : Int
  }

  def add1(magnet : AddMagnet) : Int = magnet()

  implicit class AddInt(x : Int) extends AddMagnet {
    override def apply(): Int = x+1
  }

  implicit class AddString(s : String) extends AddMagnet {
    override def apply(): Int = s.toInt + 2
  }

  val addFV = add1 _ // The underscore _ is used to convert the
  // add1 function into a function value, which can be used later.

  println(addFV(1))
  println(addFV("3"))

/*
  val receiveFV = receive _
  receiveFV(new P2PResponse)
*/
  /*
  Drawbacks :
  1. verbose
  2. harder to read
  3. you can't name or place default arguments // we can't write receive()
  4. call by name doesn't work correctly
   */

  trait HandleMagnet{
    def apply() : Unit
  }

  def handle(magnet : HandleMagnet) = magnet()

  implicit class StringHandle( s : => String) extends HandleMagnet {
    override def apply(): Unit = {
      println(s)
      println(s)
    }
  }
  def sideEffectMethod() : String = {
    println("Hellow, Scala")
    "hahahah"
  }

  // handle(sideEffectMethod())

  handle{
    println("Hello, World")
    new StringHandle("magnet")
  }
}
