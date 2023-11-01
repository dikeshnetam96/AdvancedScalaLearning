package Exercise

import scala.annotation.tailrec

/*
Exercise :

implement a lazily evaluated, singly linked stream of elements.

naturals = MyStream.from(1)(x=>x+1) = stream of natural numbers (potentially infinite!)
natural.take(100).foreach(println) // lazily evaluated stream of the first 100 naturals (finite stream)
natural.foreach(println) // will crash - infinite!
natural.map(_*2) // stream of all even numbers (potentially infinite)
 */

abstract class MyStream[+A] {
  def isEmpty: Boolean

  def head: A

  def tail: MyStream[A]

  def #::[B >: A](element: B): MyStream[B] //prepend operator

//  def ++[B >: A](anotherStream: MyStream[B]): MyStream[B] // concate two streams
  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] // concate two streams

  def foreach(f: A => Unit) : Unit

  def map[B](f: A => B): MyStream[B]

  def flatmap[B](f: A => MyStream[B]): MyStream[B]

  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A]

  def takeAsList(n: Int): List[A] = take(n).toList()

  /*
  [1,2,3].toList([]) =
  [2 3].toList([1]) =
  [3].toList([2 1]) =
  [].toList([3 2 1])=
  [1 2 3]
   */

  @tailrec
  final def toList[B >: A](acc : List[B] = Nil) : List[B] =
    if(isEmpty) acc.reverse
    else tail.toList(head :: acc)
}

object EmptyStream extends MyStream[Nothing] {

  def isEmpty: Boolean = true

  def head: Nothing = throw new NoSuchElementException

  def tail: MyStream[Nothing] = throw new NoSuchElementException

  def #::[B >: Nothing](element: B): MyStream[B] = new Cons[B](element, this) //prepend operator

  def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream // concate two streams

  def foreach(f: Nothing => Unit) = ()

  // there is nothing to no use of map and flatmap, so it will return current object
  def map[B](f: Nothing => B): MyStream[B] = this

  def flatmap[B](f: Nothing => MyStream[B]): MyStream[B] = this

  def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  def take(n: Int): MyStream[Nothing] = this

}
// here we made tail call by name because, if we call for natural number it will directly crash because
// there is no end
class Cons[+A](hd : A, tl : => MyStream[A]) extends MyStream[A] {

  def isEmpty: Boolean = false

  override val head: A = hd

  override lazy val tail: MyStream[A] = tl  // call by need

  def #::[B >: A](element: B): MyStream[B] = new Cons(element, this) //prepend operator
  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new Cons[B](head, tail ++ anotherStream) // concate two streams
  def foreach(f: A => Unit) = {
    f(head)
    tail.foreach(f)
  }
  /*

  s = new Cons(1,?)
  mapped = s.map(_+1) =  new Cons(2, s.tail.map(_+1)) // this tail section won't be evaluated
  // until mapped.tail is called somewhere
   */
  def map[B](f: A => B): MyStream[B] = new Cons[B](f(head),tail.map(f)) // preserve lazy evaluation
  def flatmap[B](f:A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatmap(f) // it will also preserve lazy evaluation
  def filter(predicate: A => Boolean): MyStream[A] =
    if(predicate(head)) new Cons(head, tail.filter(predicate)) // tail is lazy variable so
    // it will preserve lazy evaluation
    else tail.filter(predicate)

  def take(n: Int): MyStream[A] = {
    if(n<=0) EmptyStream
    else if(n==1) new Cons(head,EmptyStream)
    else new Cons(head, tail.take(n-1)) // new cons's tail section is by name function and tail is
    // lazy variable so it will preserve lazy evaluation
  }

}

object MyStream {
  def from[A](start: A)(genrator: A => A): MyStream[A] =
    new Cons(start, MyStream.from(genrator(start))(genrator))
}

object L16_StreamsPlayground extends App {

  val naturals = MyStream.from(1)(_+1)
  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)
  println("----------------------------------")
  val startFrom0 = 0 #:: naturals
  println(startFrom0.head)
  startFrom0.take(10000).foreach(println)
  println("----------------------------------")
  // map,flatmap
  println(startFrom0.map(_*2).take(100).toList())
  println("flatmap-------------------------------------------")
  println(startFrom0.flatmap(x =>new Cons(x,new Cons(x+1,EmptyStream))).take(10).toList())

  // below line throws stack overflow exception lets figure it out in next lecture
  // println(startFrom0.flatmap(x=>new Cons(x,new Cons(x+1,EmptyStream))).take(10).toList())
  // it occure becaue in ++ function it is not call by name or lazy defined, because of that
  // it computed again and again which cause stack overflow error .
  // The solution to this problem is making ++ parameters to be call by name which now
  // i have implemented you can see commented code is previous code.

  println(startFrom0.filter(_<10).take(10).take(20).toList())

  // Exercise on Streams

  // 1. Stream of Fibonaaci numbers
  // 2. Stream of prime number's with Eratosthenes' Sieve

  println("-----------------------------------")
  def fibonnaci(first : Int, second : Int) : MyStream[Int] =
   new Cons(first,fibonnaci(second,first+second))

  println(fibonnaci(1,1).take(10).toList())

  println("---------------------------------------")
  // eratosthenes sieve
  def eratosthenes(inputList : MyStream[Int]) : MyStream[Int] =
    if(inputList.isEmpty) inputList
    else new Cons[Int](inputList.head, eratosthenes(inputList.filter(_%inputList.head!=0)))

  val testList = MyStream.from(2)(_+1)
  println(eratosthenes(testList).take(20).toList())
}
