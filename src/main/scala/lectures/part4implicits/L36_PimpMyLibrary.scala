package lectures.part4implicits

object L36_PimpMyLibrary extends App {

  //  Pimp My Library - Enrich existing types with implicit
  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0

    def sqrt: Double = Math.sqrt(value)

    def times(function: () => Unit): Unit = {
      def timeAux(n: Int): Unit = {
        if (n <= 0) ()
        else {
          function()
          timeAux(n - 1)
        }
      }

      timeAux(value)
    }

    def *[T](list: List[T]): List[T] = {
      def concatenate(n: Int): List[T] = {
        if (n < 0) List()
        else concatenate(n - 1) ++ list
      }

      concatenate(value)
    }
  }

  implicit class RicherInt(richInt: RichInt) {
    def isOdd: Boolean = richInt.value % 2 != 0
  }

  new RichInt(42).sqrt

  42.isEven // new RichInt(42).isEven
  // type enrichment = pimping

  1 to 10

  import scala.concurrent.duration._

  3.seconds
  // Compiler does not do multiple implicit searches

  // 42.isOdd - it won't work because compiler will not check
  // twice once it failed. (fail in RichInt)

  /*
  Enrich the string class
    - asInt (parse String to Int)
    - Encrypt ( 2 char of ahead)

    Keep enriching the Int class

    - times(function)
    3.times(function)
    - *
    - 3 * List(1,2) => List(1,2,1,2,1,2)
   */

  implicit class RichString(string: String) {
    def asInt: Int = Integer.parseInt(string) // java.lang.Integer --> Int

    def encrypt(cypherDistance: Int): String = string.map(c => (c + cypherDistance).asInstanceOf[Char])
  }

  println("3".asInt + 4)
  println("John".encrypt(2))

  3.times(() => println("Scala Rocks!!!!"))
  println(4 * List(1, 2))

  // "3" /4
  implicit def stringToInt(string: String): Int = Integer.valueOf(string)
  // stringToInt(6) / 2
  println("6" / 2) // in case of '/' it will search for implicit class

  // Equivalent : implicit class RichAltInt(value : Int)
  class RichAltInt(value: Int)

  implicit def enrich(value: Int): RichAltInt = new RichAltInt(value)

  // danger zone
  implicit def intToBoolean(i: Int): Boolean = i == 1

  /*
  if(n) do something
  else do something else
   */

  val aConditionValue = if (3) "OK" else "Something Wrong"
  println(aConditionValue)
}
