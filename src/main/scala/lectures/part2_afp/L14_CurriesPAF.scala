package lectures.part2_afp

object L14_CurriesPAF extends App {

  // curried functions
  val superAdder: Int => Int => Int = x => y => x + y

  val adder3 = superAdder(3)
  println(adder3(5))
  println(superAdder(3)(5))

  // METHOD !  -  It is deaf, you need to specify the output type in next call of y explicitly
  def curriedAdder(x: Int)(y: Int): Int = x + y

  val add4: Int => Int = curriedAdder(4)
  // lifting -> ETA-EXPANSION // It is a simple technique for wrapping function into extra layer well preserving
  // identical personality and this is perfomed by compiler to create function out of methods

  // functions!=methods ( JVM Limitations)

  def inc(x: Int) = x + 1

  List(1, 2, 3).map(inc) // Jvm convert it as lambda expression as List(1,2,3).map(x=>inc(x)) // called ETA Expansion

  // making compiler to do ETA Expansion when we want --> Partial Fuction Application
  // Partial Function Application
  val add5 = curriedAdder(5) _ // _ will tell compiler to write Int=> Int

  println(add4(5))

  // EXERCISE
  val simpleFunction = (x: Int, y: Int) => x + y

  def simpleAddMethod(x: Int, y: Int) = x + y

  def curriedAddMethod(x: Int)(y: Int) = x + y

  // add7 Int=>Int = y => 7 + y
  // as many different implementations of add7 using the above
  // be creative!

  val add7 = (x: Int) => simpleFunction(7, x) // simplest
  /*val add7_2 = simpleAddMethod.curried(6) // This also worked in lecture but not in this version of scala*/
  val add7_6 = simpleFunction(7, _: Int) // works as well and make compiler to create a new ETA-Expansion

  val add7_3 = curriedAddMethod(7) _ // Partially Applied Function
  val add7_4 = curriedAddMethod(7)(_) // Partial Applied Function - alternative syntax

  val add7_5 = simpleAddMethod(7, _: Int) // _:Int will force compiler to write ETA-Expansion
  // alternative syntax for turning methods into function values
  // y  =  simpleAddMethod(7,y)

  // underscores are powerful
  def concatenator(a: String, b: String, c: String) = a + b + c

  val insertName = concatenator("Hello I'm ", _: String, " how are you!") // making compiler to write an ETA Funtcions
  println(insertName("xyz")) // x : String => concatenator("hello",x,"how are you!")

  val insertTwoValues = concatenator("Hello I'm ", _: String, _: String)
  println(insertTwoValues(" xyz ", " and you are my friend.."))
  val test = insertTwoValues("dikesh", _: String)
  println(test(" and you are my friends"))

  /*
  Exercise :
  1. Process a list of numbers and return their string representation with different formates
  use th e%4.2f , %8.6f , %14.12f  with curried formatter function

  sample code for formatting : "4.2f".formate(Math.PI)
   */

  def curriedFormatter = (str: String) => (num: Double) => str.format(num).toString

  val list = List(23232.34, 3312.34234, 5.535345345, 6434.5545)
  val firstStep = curriedFormatter("%4.6f")
  list.map(x => firstStep(x)).foreach(println)

  /*
  difference between
    - function vs methods
    - parameters : By-name vs 0-lambda
   */

  def byName(n: Int) = n + 1

  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42

  def parenMethod(): Int = 42

  /*
  calling by name and byFunction
  - int
  - method
  - parenMethod
  - lambda
  - PAF (Partial Applied Function)
   */

  byName(45) // ok
  byName(method) // ok
  byName(parenMethod()) // ok
  byName(parenMethod) // ok but be aware ==> byName(parenMethod())
  //  byName(parenMethod _) // not ok
  byName((() => 42)()) // ok
  //  byName(parenMethod _) // not ok

  //  byFunction(45) // not ok
  //  byFunction(method) // not ok - doesn't do ETA-Expansion
  byFunction(parenMethod) // compiler does ETA
  byFunction(() => 46) // works
  byFunction(parenMethod _) // also works, but warning - unnecessary

}
