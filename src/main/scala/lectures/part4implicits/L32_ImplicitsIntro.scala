package lectures.part4implicits

import scala.language.implicitConversions

object L32_ImplicitsIntro extends App {

  // how it works??
  val pair = "Dikesh" -> "Netam"
  val intPair = 1 -> 2
  // '->' is a implicit method called ArrowAssoc

  case class Person(name: String) {
    def greet = s"Hi, My name is $name"
  }


  implicit def fromStringToPerson(str: String): Person = Person(str)

  // Compiler automatically search for implicit method, which will take string input
  // that uses greet function internally somewhere
  println("peter".greet)  // it will search for something which takes "peter", and
  // convert it to something which has greet method
  // compiler will rewrite it to println(fromStringToPerson("peter").greet())


/*  class A {
    def greet: Int = 2
  }

  // because of 'fromStringToA' println("peter".greet) it will show error because
  // compiler gets confused
  implicit def fromStringToA(str : String) : A = new A

  */


  // Call Implicit Parameters
  def increment(x : Int)(implicit amount : Int) = x+amount
  implicit val defaultAmount =  10 // implicit val is good practise to write

  println(increment(2)) // for second parameter compiler will fetch automatically
  // implicit variable

  // Not same as Default Arguments


}
