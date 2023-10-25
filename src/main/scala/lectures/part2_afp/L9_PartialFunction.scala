package lectures.part2_afp

object L9_PartialFunction extends App {

  val aFunction = (x: Int) => x + 1 // Function1[Int,Int]=== Int=>Int

  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicationExeption

  class FunctionNotApplicationExeption extends RuntimeException

  // In below method (with pattern matching) is same as above mentioned method
  val aNicerFussyFunction = (x: Int) => x match { // this is a total function
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }
  //println(aNicerFussyFunction(44)) // it will give a match error here

  // Partial Functions : It uses pattern matching internally
  val aPartialFunction: PartialFunction[Int, Int] = { // this is a partial function
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  println(aPartialFunction(2))
  //println(aPartialFunction(32322)) // it will also produce match error because it is based on pattern match

  // Partial Function Utilities
  println(aPartialFunction.isDefinedAt(67)) // check 67 is defined there or not and return boolean value

  // Lift
  // Partial function can be lifted to total function returning option
  val lifted = aPartialFunction.lift // Int => Option[Int]
  println(lifted(555))
  println(lifted(5))

  // Partial Functon chaining
  val pfchain = aPartialFunction.orElse[Int, Int] {
    case 45 => 66
  }
  println(pfchain(2))
  println(pfchain(45))

  // partial function extends normal functions

  // Partial Function is sub type of total function
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  /*
  Side effect to that is :
  // HOFs also accept Partial Order Function
   */


  val aMappedList = List(1, 2, 3).map {
    case 1 => 44
    case 2 => 34
    case 3 => 4567
  }
  // it will return a list of output // it also cause pattern match error if any one element is not found.
  println(aMappedList)

  /*
  Note : Partial Function can have only one parameter type
  [it is obvious if it is multiple parameter then how it will work with pattern matching]
   */

  /*
  Exercises :
  1. construct a PF instance yourself (anonymous class)
  2. dumb chatbot as a Partial functions
   */

  /*
    // runtime input in scala
    scala.io.Source.stdin.getLines().foreach(line => println("you said : "+ line))
  */

  // Exercise No 1
  val aManualFussyFunction = new PartialFunction[Int,Int] {
    override def apply(v1: Int): Int = v1 match {

      case 1 => 34
      case 2 => 344
      case 3 => 555
    }

    override def isDefinedAt(x: Int): Boolean = {
      x==1 || x==2 || x==3
    }
  }

  // Exercise Number 2
  val chatbot : PartialFunction[String,String] = {
    case "hello" => "Hi my name is HAL900"
    case "goodbye" => "Once you start talking to me, there is no way you can escape"
    case "call mom" => "Unable to find your phone without your credit card"
  }

  scala.io.Source.stdin.getLines().foreach(line=>println("chatbot said : "+chatbot(line)))
}
