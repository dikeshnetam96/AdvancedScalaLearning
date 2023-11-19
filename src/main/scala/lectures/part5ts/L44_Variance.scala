package lectures.part5ts

object L44_Variance extends App {

  trait Animal

  class Dog extends Animal

  class Cat extends Animal

  class Crocodile extends Animal

  // what is variance ?
  // inheritance - type substitution of generics

  class Cage[T]

  // yes - covariance
  class CCage[+T]

  val ccage: CCage[Animal] = new CCage[Cat]

  // no - invariance
  class ICage[T]
  /*  val icage : CCage[Animal] = new ICage[Cat]
    val x : Int = "Hello World!!!"*/

  // hell no. - opposite = contravariance
  class XCage[-T]

  val xcage: XCage[Cat] = new XCage[Animal]

  class InvariantCage[T](animal: T) // invariant

  // covariant positions
  class CovariantCage[+T](val animal: T) // COVARIANT POSITION
  // (field class field decoration : Type)

  // We can't write below statement because the further below explained reasons
  //  class ContravariantCage[-T] (val animal : T)
  /*
  val catCage : XCage[Cat] = new XCage[Animal](new Crocodile)
   */

  // class CovariantVariableCage[+T] (var animal : T) // types of vars are in CONTRAVARIANT POSITION
  /*
    val ccage : CCage[Animal] = new CCage[Cat](new Cat)
    ccage.animal = new Crocodile
   */

  // class ContravariantVariableCage[-T] (var animal : T) // also in COVARIANT POSITION
  /*
    val catCage : XCage[Cat] = new XCage[Animal](new Crocodile)
   */


  class InvariantVariableCage[T](var animal: T) // Ok
  // COVARIANT AND CONTRAVARIANT are having some compiler restriction


  // Below code won't work the reason is explain in just below comment...
  /*  trait AnotherCovariantCage[+T] {
      def addAnimal(animal: T) //  CONTRAVARIANT POSITION
    }*/

  /*
  // here we created CCage[Dog] type but inside that we are taking another cat animal which is wrong this why this type of definitions are not valid
  val ccage : CCage[Animal] = new CCage[Dog]
  ccage.add(new Cat)
   */

  class AnotherContravariantCage[-T] {
    def addAnimal(animal: T) = true
  }

  val acc: AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
  acc.addAnimal(new Cat)

  // acc.addAnimal(new Dog) // ERROR Because of CONTRAVARIANT Properties, it is of Cat type, so it will take either CAT or other class which extends CAT.
  class Kitty extends Cat

  acc.addAnimal(new Kitty)
  /*
  The assignment val acc: AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal] is acceptable because you are assigning
  a more general (contravariant) type to a more specific (covariant) type, which is allowed due to the contravariance.

  In summary, the contravariant nature of AnotherContravariantCage allows you to assign an instance of AnotherContravariantCage[Animal]
  to a variable of type AnotherContravariantCage[Cat] because it reverses the subtyping relationship between the type parameters.
   */

  /*
    // already defined above,added just for reference

    class AnotherContravariantCage[-T] {
      def addAnimal(animal : T) = true
    }*/

  class MyList[+A] {
    def add[B >: A](element: B): MyList[B] = new MyList[B] // widening the type
  }

  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)
  val moreAnimals = animals.add(new Cat)
  val evenMoreAnimals = moreAnimals.add(new Dog)
  val evenMoreAnimals2 = moreAnimals.add(new Crocodile)

  // METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITION

  // Return Types
  class PetShop[-T] {
    // WHY NOT VALID ??
    // def get(isItaPuppy : Boolean) : T  // METHOD RETURN TYPE ARE IN COVARIANT POSITION
    /*
    val catShop = new PetShop[Animal] {
      def get(isItaPuppy : Boolean) : Animal = new Cat
    }

    val dogShop : PetShop[Dog] = catShop
    dogShop.get(true) // EVIL CAT!
     */

    def get[S <: T] (isItaPuppy : Boolean, defaultAnimal : S) : S = defaultAnimal
   }

  val shop : PetShop[Dog] = new PetShop[Animal]
  //  val evilCat = shop.get(true, new Cat)

  class TerraNova extends Dog
  val bigFurry = shop.get(true, new TerraNova)

  /*
  Big Rule -
    - method arguments are in CONTRAVARIANT position
    - return types are in COVARIANT position
   */

  /*
  In summary:

  Contravariance in method arguments allows you to accept broader (more general) types.
  Covariance in return types allows you to return narrower (more specific) types.
   */

  /**
   *
   * 1. Invariant, Covariant, Contravariant
   *  Parking[T](things : List[T]) {
   *    park(vehicle : T)
   *    impound(vehicle : List[T])
   *    checkVehicles(conditions : String) : List[T]
   *  }
   *
   * 2. used someone else's API : IList[T]
   * 3. parking = monad!
   *    -flatMap
   */

  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle
  class IList[T]

  // INVARIANCE
  class IParking[T](vehicles : List[T]) {
    def park(vehicle : T) : IParking[T] = ???
    def impound(vehicle : List[T]) : IParking[T] = ???
    def checkVehicles(conditions : String) : List[T] = ???
    def flatMap[S] (f : T => IParking[S]) : IParking[S] = ???
  }

  // COVARIANCE
  class CParking[+T](vehicles : List[T]){
    def park[S >: T](vehicle : S) : CParking[S] = ???
    def impound[S >: T](vehicle : List[S]) : CParking[S] = ???
    def checkVehicles(conditions : String) : List[T] = ???
    def flatMap[S] (f : T => IParking[S]) : CParking[S] = ???
  }

  // CONTRAVARIANCE
  class XParking[-T] (vehicles : List[T]) {
    def park(vehicle : T) : XParking[T] = ???
    def impound(vehicles : List[T]) : XParking[T] = ???
    def checkVehicles[S <: T](conditions : String) : List[S] = ???
    // def flatMap[S] (f : T => IParking[S]) : XParking[S] = ??? // Contravariant type T occurs in covariant position in type T => L44_Variance.IParking[S] of value f

    def flatMap [R <: T,S] (f : Function1[R,XParking[S]]) : XParking[S] = ???

  }

  /*
  Rule of thumb
    - use covariance = COLLECTION OF THINGS
    - use contravariance = GROUP OF ACTIONS
   */

  // COVARIANCE
  class CParking2[+T](vehicles: IList[T]) {
    def park[S >: T](vehicle: S): CParking2[S] = ???
    def impound[S >: T](vehicle: IList[S]): CParking2[S] = ???
    def checkVehicles(conditions: String): List[T] = ???
  }

  // CONTRAVARIANCE
  class XParking2[-T](vehicles: IList[T]) {
    def park(vehicle: T): XParking[T] = ???
    def impound[S <: T](vehicles: IList[S]): XParking[S] = ???
    def checkVehicles[S <: T](conditions: String): List[S] = ???
  }

  // flatMap

}
