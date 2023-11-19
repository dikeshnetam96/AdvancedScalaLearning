package lectures.part5ts

object L46_TypeMembers extends App {

  class Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection {
    type AnimalType // abstract type member
    type BoundedAnimal <: Animal  // must extend Animal
    type SuperBoundedAnimal >: Dog <: Animal // must extended by the Animal and SuperBoundedAnimal
    type AnimalC = Cat
  }

  val ac = new AnimalCollection
  val dog : ac.AnimalType = ???

//  val cat : ac.BoundedAnimal = new Cat

  val pup : ac.SuperBoundedAnimal = new Dog
  val cat : ac.AnimalC = new Cat

  type CatAlias = Cat
  val anotherCat : CatAlias = new Cat
  // type aliases uses when there is so much confusion in packages

  // alternative to generics
  trait MyList {
    type T
    def add(element : T) : MyList
  }

  class NonEmptyList(value : Int) extends MyList {
    override type T = Int
    def add(element : Int) : MyList = ???
  }

  // .type
  type CatsType = cat.type
  val newCat : CatsType = cat
//  new CatsType // class type required but lectures.part5ts.L46_TypeMembers.cat.type found new CatsType

  /*
    Exercise - enforce a type to be applicable to SOME TYPES only
   */

  // Locked
  trait MList {
    type A
    def head : A
    def tail : MList
  }

  trait ApplicationToNumbers {
    type A <: Number
  }
  /*  Below code is worked with Scala 2.0
/*
  class CustomList(hd : String, tl : CustomList) extends MList {
    type A = String
    def head : String = hd
    def tail : tl
  }
*/
   class IntList(hd : Int, tl : CustomList) extends MList {
    type A = Int
    def head = hd
    def tail : CustomList
  }
  */

  // Number
  // type member and type member constraints (bounds)
}
