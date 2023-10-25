package lectures.part1as

import com.sun.nio.sctp.AbstractNotificationHandler

object AdvancedPatternMatching extends App {

  val number = List(1)
  val description = number match {
    // It below condition used to check if List is having a single element or not.
    case head :: Nil => println(s"the only element is $head")
    case _ =>
  }

  /*
    -constants
    - wildcards (_)
    - case classes
    - tuples
    - some special logic like above
   */

  // sometimes in api we can't create case class in that case how we can
  // make class compatible pattern matching

  class Person(val name: String, val age: Int)

  // we define a companion for Person class
  object Person {
    // special function
    def unapply(person: Person): Option[(String, Int)] =
      if (person.age < 21)
        None
      else
        Some(person.name, person.age)

    def unapply(age: Int): Option[(String)] =
      Some(if (age < 21) "minor" else "major")
  }

  val bob = new Person("bob", 20)
  val greeting = bob match {
    // (n,a) pattern output will we searched.
    case Person(n, a) => s"Hi i'm $n and i'm $a years old"
    // here runtime says, the pattern called Person with name and age look an method
    // unapply in object called person and which return tuples of two things what
    // we found it Option[(String,Int)]
    case _ => None
  }
  println(greeting)
  val legalStatus = bob.age match {
    case Person(status) => s"My legal status is $status"
  }
  println(legalStatus)

  /*
    Exercise :
   */

  object even {
    def unapply(arg: Int): Option[Boolean] =
      if (arg % 2 == 0) Some(true)
      else None
  }

  object singleDigit {
    def unapply(arg: Int): Option[Boolean] =
      if (arg > -10 && arg < 10)
        Some(true)
      else
        None
  }

  /*
  Pros : good for multi use
  Cons : code will messed up for multiple condition
   */
  object even2 {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  object singleDigit2 {
    def unapply(arg: Int): Boolean = (arg > -10 && arg < 10)
  }


  val n: Int = 45
  val mathProperty = n match {
    case x if x < 10 => "single digit"
    case x if x % 2 == 0 => " an even number"
    case _ => "No Property"
  }

  val e: Int = 44
  val modifiedMathProperty = e match {
    case singleDigit(e) => "single digit"
    case even(e) => "enter no is even number"
    case _ => "No Property"
  }

  println(modifiedMathProperty)


  val e2: Int = 44
  val modifiedMathProperty2 = e2 match {
    case singleDigit2() => "single digit"
    case even2() => "enter no is even number"
    case _ => "No Property"
  }

  println(modifiedMathProperty2)

  // infix patterns
  case class Or[A, B](a: A, b: B)

  val either = Or(2, "two")
  val humanDescription = either match {
    // below expression is exact same as Or(number,string)
    case number Or string => s"$number is written as $string"

  }

  println(humanDescription)

  // decomposition sequences
  val vararg = number match {
    // vararg = varaible argument
    case List(1, _*) => "starting with 1"
  }

  abstract class MyList[+A] {
    def head: A = ???

    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]

  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  val decomposed = myList match {
    // here we are sending a List, it will search for unapply or unapplySeq method which takes input of MyList
    // and return output as Sequence.
    case MyList(1, 2, _*) => "starts with 1 and 2 ..."
    case _ => "something else"
  }
  println(decomposed)

  // custom return type for unapply
  // isEmtpy : Boolean, get : something

  abstract class Wrapper[T] {
    def isEmpty: Boolean

    def get: T
  }

  // unappy methods gives output as wrapper class.
  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      override def isEmpty: Boolean = false

      override def get: String = person.name
    }
  }

  println(bob match {
    // person class object 'bob' , which will go to unapply method and then values are getting
    // initialized and wrapper return name string value
    case PersonWrapper(n) => s"the name is $n"
    case _ => "something else"
  })
}

