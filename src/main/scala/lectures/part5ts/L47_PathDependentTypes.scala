package lectures.part5ts

object L47_PathDependentTypes extends App {

  class Outer {
    class Inner

    object InnerObject

    type InnerType

    def print(i: Inner): Unit = println(i)

    def printGeneric(i: Outer#Inner) = println(i)

  }

  def aMethod: Int = {
    class HyperClass
    type HelperType = String
    2
  }

  // per-instance
  val o = new Outer
  val inner = new o.Inner

  val oo = new Outer
  //    val otherInner : oo.Inner = new o.Inner
  o.print(inner)
  //  oo.print(inner) // won't work because it depends upon path... (created by "o" not by "oo")

  // This above conditions called path-dependent types

  // All Inner types having comman super types that is Outer#Inner
  o.printGeneric(inner)
  oo.printGeneric(inner)

  trait ItemLike {
    type key
  }
  trait Item[k] extends ItemLike{
    type key = k
  }
  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]
  def get[ItemType <: ItemLike](key: ItemType#key): ItemType = ???

  get[IntItem](42)// ok
  get[StringItem]("home") // Not ok
  //  get[IntItem]("Scala" ) // Not Ok

}
