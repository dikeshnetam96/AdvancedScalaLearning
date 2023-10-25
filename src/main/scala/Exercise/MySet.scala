package Exercise

import lectures.part1as.AdvancedPatternMatching.Empty

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {

  /*
  Exercise - Implement a function set
   */

  def apply(elem: A): Boolean = {
    contains(elem)
  }

  def contains(elem: A): Boolean

  def +(elem: A): MySet[A]

  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B]

  def filter(predicate: A => Boolean): MySet[A]

  def foreach(f: A => Unit): Unit
}

class EmptySet[A] extends MySet[A] {
  override def contains(elem: A): Boolean = false

  override def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]

  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]

  override def filter(predicate: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = ()

}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  override def contains(elem: A): Boolean = elem == head || tail.contains(elem)

  override def +(elem: A): MySet[A] =
    if (this contains elem) this
    else new NonEmptySet[A](elem, this)

  /*
  [1,2,3] ++ [4,5] =
  [2,3] ++ [4,5] +1
  [3] ++ [4,5] + 1 + 2
  [] ++ [4,5] + 1 + 2 + 3
  final result will be now - [4,5,1,2,3] // we don't focus for sorted list here
   */
  override def ++(anotherSet: MySet[A]): MySet[A] = tail ++ anotherSet + head

  override def map[B](f: A => B): MySet[B] = tail.map(f) + f(head)

  // flatmap uses ++ because there can be multiple lists
  override def flatMap[B](f: A => MySet[B]): MySet[B] = tail.flatMap(f) ++ f(head)

  override def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail.filter(predicate)
    if (predicate(head)) filteredTail + head
    else filteredTail
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)

  }


}


object MySet {
  def apply[A](values : A*) : MySet[A] = {
    @tailrec
    def buildSet(seq: Seq[A], acc: MySet[A]): MySet[A] = {
    if(seq.isEmpty)
      acc
    else
      buildSet(seq.tail,acc + seq.head)
    }
    buildSet(values,new EmptySet[A])
  }
}

object test extends App {
  println(MySet(1, 2, 3, 4))
  println(MySet(1, 2, 3, 4,"dikesh"))
  val s = MySet(3,5,6,8)
  //  s+53 foreach(println)
  s+32 ++ MySet(-1,-2) + 32 map(x=>x*10) foreach println
  println("----------------------------------------------------")
  s+32 ++ MySet(-1,-2) + 32 flatMap(x=>MySet(x,10*x)) foreach println
  println("----------------------------------------------------")
  s+32 ++ MySet(-1,-2) + 32 flatMap(x=>MySet(x,10*x)) filter(_%2==0) foreach println
}