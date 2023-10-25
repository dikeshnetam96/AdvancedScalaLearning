package lectures.part2_afp

object L15_LazyEvaluation extends App {

  // this line is obviously throw an exception
  //  val x : Int = throw new RuntimeException

  // lazy - will make computation once(it won't compute again [more or less like static]), only when it is needed
  lazy val x: Int = throw new RuntimeException

  // Example :
  lazy val test: Int = {
    println("hello")
    42
  }

  println(test)

  println("printing already computed values : " + test)

  // 1. Example of implications
  // Side-Effect No. 1
  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }

  def simpleCondition: Boolean = false

  lazy val lazycondition = sideEffectCondition

  // compiler check simpleCondition values is false, so it smarty didn't compute lazyCondition because it is not at
  // all required...
  println(if (simpleCondition && lazycondition) "yes" else "no")

  // 2.  In-conjection with call by name
  // it compute 3 times because we have used "n" e times  -> solution to that is use lazy vals
  def byNameMethod(n: => Int): Int = {
    // CALL BY NEED
    lazy val t = n
//    n + n + n + 1
    t + t + t + 1
  }

  def retriveMagicValue = {
    // side effect or a long computation
    println("waiting")
    Thread.sleep(1000)
    42
  }

  println(byNameMethod(retriveMagicValue))
  // use lazy vals -> to overcome such problem with call by name

  // 3. filtering with lazy vals
  def lessthan30(i : Int) : Boolean = {
    println(s"$i is less than 30")
    i<30
  }

  def greaterThan20(i : Int) : Boolean = {
    println(s"$i is greater than 20")
    i>20
  }

  // Normal way
  val number = List(1,25,40,5,23)
  println("----------------------------")
  val lt30 = number.filter(lessthan30)
  val gt20 = number.filter(greaterThan20)
  println(gt20)
  println("----------------------------")
  println
  // Lazy way
  val lt30Lazy = number.withFilter(lessthan30) // lazy vals under the hood
  val get20Lazy = lt30Lazy.withFilter(greaterThan20)
  // it is just printing a wrapper method -> an the main this it is not evaluated yet
  println(get20Lazy)
  // In this it is computing combinely for each digit
  get20Lazy.foreach(println)

  // for - comprehensions use withFilter with guards
  for {
    a <- List(1,2,3) if a %2 ==0
  } yield a+1
  List(1,2,3).withFilter(_%2==0).map(_ + 1)  // List[Int]
  // withFilter will wrap the list values and then map will provide us the List[Int]

  /*
  Exercise :

  implement a lazily evaluated, singly linked stream of elements.

  naturals = MyStream.from(1)(x=>x+1) = stream of natural numbers (potentially infinite!)
  natural.take(100).foreach(println) // lazily evaluated stream of the first 100 naturals (finite stream)
  natural.foreach(println) // will crash - infinite!
  natural.map(_*2) // stream of all even numbers (potentially infinite)
   */

  abstract class MyStream[+A] {
    def isEmpty : Boolean
    def head : A
    def tail : MyStream[A]

    def #::[B>:A](element : B) : MyStream[B] //prepend operator
    def ++[B>:A](anotherStream : MyStream[B]): MyStream[B] // concate two streams

    def foreach(f : A=>Unit) = Unit
    def map[B](f: A=>B) : MyStream[B]
    def flatmap[B](f:A=>MyStream[B]) : MyStream[B]
    def filter(predicate : A=>Boolean) : MyStream[A]

    def take(n:Int): MyStream[A]
    def takeAsList(n : Int) : List[A]

  }

  object MyStream{
    def from[A](start:A)(genrator : A=>A): MyStream[A] = ???
  }

}
