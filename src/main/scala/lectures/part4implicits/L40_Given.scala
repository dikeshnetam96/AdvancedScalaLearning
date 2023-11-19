package lectures.part4implicits

object L40_Given extends App {


  // implicit val, def and class having different concepts, scala 3 separated those , in
  // separated structures

  // implicit val is here Given
  // Scala 3 style
 /*


  Below code will work on Scala 3 or above




  */
 private val list = List(1, 4, 2, 6)
 // implicit val desendingOrdering : Ordering[Int] = Ordering.fromLessThan(_>_)
 val anOrderedList = list.sorted
 /*  object Givens {
     given descendingOrder: Ordering[Int] = Ordering.fromLessThan(_ > _)
   }*/

 // Scala 2 Style
 object Implicits {
   implicit val descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
 }

 // it doesn't matter which formate we have written it in.
 //given desendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
 // given <=> implicits vals

 // Scala 3 Style
  /*
 object Givens {
   given desendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
   // given <=> implicits vals
 }

 // instantiating an anonymous class
 object GivenAnonymousClassNaive {
   given desendingOrdering_v2: Ordering[Int] = new Ordering[Int]:
     override def compare(x: Int, y: Int): Int = y - x
 }

 // 'with' keyword below is just for shorting the code
 object GivenWith {
   given descendinOrdering_v3: Ordering[Int] with {
     override def compare(x: Int, y: Int): Int = y - x
   }
 }

 // if we want to import the given, we have to write

 import GivenWith._ // in scala 3, it import does NOT imports given as well
 // for big code bases it's very hard to find out where it came form

 // In Scala 3,
 import GivenWith.given // it will import all the given
 import GivenWith.descendinOrdering_v3 // will import a particular mentioned given


 // implicit arguments <=> using clauses

 def extremes[A](list: List[A])(implicit ordering: Ordering[A]): (A, A) = {
   val sortedList = list.sorted
   (sortedList.head, sortedList.last)
 }

 def extremes_v2[A](list: List[A])(using ordering: Ordering[A]): (A, A) = {
   val sortedList = list.sorted
   (sortedList.head, sortedList.last)
 }

 //  implicit def (synthesize new implicit value)
 trait Combinator[A] {
   def combine(x: A, y: A): A
 }

 implicit def listOrdering[A](implicit simpleOrdering: Ordering[A], combinator: Combinator[A]): Ordering[List[A]] =
   new Ordering[List[A]] {
     override def compare(x: List[A], y: List[A]): Int = {
       val sumX = x.reduce(combinator.combine)
       val sumY = y.reduce(combinator.combine)
       simpleOrdering.compare(sumX, sumY)
     }
   }

 // equivalence in scala 3 with givens
 given listOrdering_v2[A] (using simpleOrdering: Ordering[A], combinator: Combinator[A]): Ordering[List[A]] with {
   override def compare(x: List[A], y: List[A]): Int =
     val sumX = x.reduce(combinator.combine)
     val sumY = y.reduce(combinator.combine)
     simpleOrdering.compare(sumX, sumY)
 }

 // Implicit in scala 2 can be used for conversion (abused in scala 2)
 case class Person(name : String) {
   def greet() : String = s"Hi, my name is ${name}"
 }

 implicit def string2Person(string : String) : Person = Person(string)
 val danialsGreet = "Danial".greet()

 // in scala 3
 import scala.language.implicitConversions  // required in scala 3
 given string2PersonConversion : Conversion[String, Person] with {
   override def apply(x: String): Person = Person(x)
 }




 println(anOrderedList)


}

  */
}
