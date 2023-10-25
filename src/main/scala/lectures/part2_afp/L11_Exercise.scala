package lectures.part2_afp


import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {



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

/*
  L11_Exercise_1:

    1.Implement a remove element method
    2.Intersection with another set
    3.difference with another set
*/

  // removing element
  def -(elem : A) : MySet[A]
  // difference between two list
  def --(anotherSet : MySet[A]) : MySet[A]
  // Intersection between two list
  def &(anotherSet : MySet[A]) : MySet[A]

  /*
   L11_Exercise_2 : implement a negation of set
   set[1,2,3] => [everything except 1,2,3]
  * */
  def unary_! : MySet[A] = new PropertyBasedSet[A](_=>true)
}

/*
class AllInclusiveSet[A] extends MySet[A] {

}
*/

// Property based sets are flexible in defining properties for potentially infinite functional sets
// All elements of type A which satisfies a property
// {x in A | property(x)}
class PropertyBasedSet[A](proprty : A=>Boolean) extends MySet[A]{

  override def contains(elem: A): Boolean = proprty(elem)

  override def +(elem: A): MySet[A] =
   new PropertyBasedSet[A](x=>proprty(elem) || x==elem)
  // {x in A | property(x) } + element = { x in A | Property(x) || x==element}

 // {x in A | property(x) } ++ set => { x in A | property(x) || set contains x}
  override def ++(anotherSet: MySet[A]): MySet[A] =
    new PropertyBasedSet[A](x=>proprty(x) || anotherSet(x))

  // all integers => (_%3) => [0,1,2] // then we don't know whether it is finite or not
  override def map[B](f: A => B): MySet[B] = ???

  override def flatMap[B](f: A => MySet[B]): MySet[B] = ???

  override def filter(predicate: A => Boolean): MySet[A] = new PropertyBasedSet[A](x=>proprty(x) && predicate(x)) // Property-Based- Set

  override def foreach(f: A => Unit): Unit = ()

  override def -(elem: A): MySet[A] = filter(x=>x!=elem)

  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x=> !proprty(x))

  def politelyFail = throw new IllegalArgumentException("really deep rabbit hole!")
}

class EmptySet[A] extends MySet[A] {
  override def contains(elem: A): Boolean = false

  override def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]

  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]

  override def filter(predicate: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = ()

  def -(elem: A): MySet[A] = this

  def --(anotherSet: MySet[A]): MySet[A] = this

  def &(anotherSet: MySet[A]): MySet[A] = this

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

  def -(elem: A): MySet[A] = {
    if(head==elem) this.tail
    else tail - elem + head
  }

  def --(anotherSet: MySet[A]): MySet[A] = filter(x=> !anotherSet.contains(x))

  // intersection is basically a filtering
  //def &(anotherSet: MySet[A]): MySet[A] = this.filter(x=>anotherSet.contains(x)) // we can reduce it to
  def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet.contains)

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))

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


object L11_Exercise extends App {

  val s = MySet(1,2,3,4)
  //s foreach println
  val ss = s - 1
  ss foreach println
  println("----------------------------------")
  val intersection = s & MySet(3,4,5,6)
  intersection foreach println
  println("----------------------------------")
  val difference = s -- MySet(3, 4, 5, 6)
  difference foreach println

  val negative = !s
  println(negative(2))
  println(negative(5))

  val negativeEven = negative.filter(_%2==0)
  println(negativeEven(5))

  val negativeEven5 = negativeEven + 5 // all the number greater than 4 + 5
  println(negativeEven5(5))

}
