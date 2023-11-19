package lectures.part4implicits

object L41_ExtensionMethod extends App {
  /*

    case class Person(name : String) {
      def greet() : String = s"Hi, i am $name, how can i help?"
    }

    extension (string : String) { // extraction method
      def greetingMethod() : String = Person(string).greet()
    }

    val danielsGreeting = "Danial".greetingMethod()
    println(danielsGreeting)

    // extension method <=> implicit classes

    object Scala3ExtensionMethods {
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
      }
    }

    val is3Even = 3.isEven // new RichInt(3).isEven

  /*
    extension (value : Int)  {
      // define all methods
    }
  */
  */

}
