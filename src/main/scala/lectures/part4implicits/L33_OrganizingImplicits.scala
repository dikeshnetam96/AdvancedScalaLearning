package lectures.part4implicits

import scala.Predef

object L33_OrganizingImplicits extends App {

  // it will sort the List. In behind it will
  // use sorted function which takes a implicit input

  /*
    sorted function will search for implicit value , if it fount
    that it will apply that when sorted is calling
   */

  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  // implicit val reverseOrdering : Ordering[Int] = Ordering.fromLessThan(_>_) // also valid

  // If i have added normalOrdering, now compiler will get confused because it will
  // find two implicit methods....
  //  implicit val normalOrdering : Ordering[Int] = Ordering.fromLessThan(_<_)
  println(List(1, 4, 3, 2, 6, 5).sorted) // default sorted will work only for Int and String

  /*
    Implicits :
      - val/var
      - object
      - accessor methods  = def with no parameters
   */

  // Exercise
  case class Person(name: String, age: Int)

  val person: List[Person] = List(Person("Steve", 30), Person("Amy", 22), Person("John", 66))
  implicit val alphabeticOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  println(person.sorted)

  /*
  Implicit Scope :
    - Normal Scope
    - Imported Scope
    - compare of all types involved in the method signature
      - List
      - Ordering
      - all the types involved = A or any supertype
   */
  // def Sorted[B > : A](implicit ord : Ordering[B]) : List[B]

  /*
  Exercise
  - TotalPrice = most used (50%)
  - by unit count = 25%
  - by unit price = 25%
   */

  case class Purchase(nUnit : Int, unitPrice : Double)

  val list : List[Purchase] = List(Purchase(5,2.5),Purchase(10,5.5),Purchase(9,6.6),Purchase(3,4.4))

  object Purchase {
    implicit val totalPricingOrdering : Ordering[Purchase] = Ordering.fromLessThan((a,b)=> a.unitPrice*a.nUnit < b.unitPrice*b.nUnit)
  }
  object UnitCountOrdering {
    implicit val unitCountOrdering: Ordering[Purchase] = Ordering.fromLessThan((a, b) => a.nUnit < b.nUnit)
  }

  object UnitPriceOrdering {
    implicit val unitPriceOrdering : Ordering[Purchase] = Ordering.fromLessThan((a,b) => a.unitPrice<b.unitPrice)
  }

  // call the specific implicit classes
  import UnitPriceOrdering._
  println(list.sorted)
  

}