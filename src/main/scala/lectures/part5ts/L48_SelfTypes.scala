package lectures.part5ts

import javax.swing.plaf.metal.MetalIconFactory.PaletteCloseIcon

object L48_SelfTypes extends App {

  // requiring a type to be mixed in
  trait Instrumentalist {
    def play(): Unit
  }

  // Whoever is extending Singer, he/she must extend Instrumentalist as well
  trait Singer {
    self: Instrumentalist => // whoever implements Singer to implements Instrumentalist
    def sing(): Unit // instead of self we can write something else
  }

  // Below is valid
  class LeadSinger extends Singer with Instrumentalist {
    override def sing(): Unit = ???

    override def play(): Unit = ???
  }

  // Below is not valid
  /*  class LeadSinger2 extends Singer {
      override def sing(): Unit = ???

    }*/

  val jamesHetfield = new Singer with Instrumentalist {
    override def sing(): Unit = ???

    override def play(): Unit = ???
  }

  class Guitarist extends Instrumentalist {
    override def play(): Unit = ???
  }

  val ericClapton = new Guitarist with Singer {
    override def play(): Unit = ???

    override def sing(): Unit = ???
  }

  // Self Types vs Inheritance

  class A

  class B extends A // 'B' IS AN 'A'

  trait T

  trait S {
    self: T => // 'S' REQUIRES 'T'
  }

  // self types are basically used for --> CAKE PATTERN == 'Dependency Injection like in java'

  // Dependency Injection
  // CAKE PATTERN
  trait ScalaComponent {
    // API
    def action(x : Int) : String
  }

  trait ScalaDependentComponent { self : ScalaComponent =>
    def dependentAction(x : Int) : String = action(x) + "this rocks!!!"
  }

  trait ScalaAppliation {self : ScalaDependentComponent =>}

  // layer 1 - Small Component
  trait Picture extends ScalaComponent
  trait Stats extends ScalaComponent

  // layer 2 - compose
  trait Profile extends ScalaDependentComponent with Picture
  trait Analytics extends ScalaDependentComponent with Stats

  // layer 3 - app
  trait AnlyticsApp extends ScalaAppliation with Analytics

  // Cyclic Dependencies

  // Not possible with Inheritance
  // class X extends Y
  // class Y extends X

  // Possible with Self Types
  trait X {self : Y => }
  trait Y {self : X => }
}
