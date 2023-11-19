package lectures.part5ts

object FBoundedPolymorphism extends App {

  /* trait Animal{
     def breed : List[Animal]
   }
   class Cat extends Animal {
     override def breed: List[Animal] = ???  // List[Cat] !!!
   }
   class Dog extends Animal {
     override def breed: List[Animal] = ???  // List[Dog] !!!
   }
 */

  // Solution 1 - Naive
  /*
  trait Animal {
    def breed: List[Animal]
  }
  class Cat extends Animal {
    override def breed: List[Cat] = ???
  }
  class Dog extends Animal {
    override def breed: List[Dog] = ???
  }
  */

  // Solution 2 - Recursive type : F : Bounded Polymorphism // but it also has limitations
/*
  trait Animal[A <: Animal[A]] {
    def breed: List[Animal[A]]
  }

  class Cat extends Animal[Cat] {
    override def breed: List[Animal[Cat]] = ???
  }

  class Dog extends Animal[Dog] {
    override def breed: List[Animal[Dog]] = ???
  }

  // below code is compilable but logically it should not be allowed....
  class Crocodile extends Animal[Dog] {
    override def breed: List[Animal[Dog]] = ???
  }

  // This sort of technique used in ORM method
  trait Entity[E <: Entity[E]] // ORM

  class Person extends Comparable[Person] { // F-Bounded Polymorphism
    override def compareTo(o: Person): Int = ???
  }
*/

  // Solution No. - 3 : F-Bounded Polymorphism + self- types

  /*
  trait Animal[A <: Animal[A]] { self : A =>
    def breed: List[Animal[A]]
  }

  class Cat extends Animal[Cat] {
    override def breed: List[Animal[Cat]] = ???
  }

  class Dog extends Animal[Dog] {
    override def breed: List[Animal[Dog]] = ???
  }
  */

/* Now the below mentioned problem is resolved by using self types
  // below code is compilable but logically it should not be allowed....
  class Crocodile extends Animal[Dog] {
    override def breed: List[Animal[Dog]] = ???
  }
*/

/*
  trait Fish extends Animal[Fish]

  // but below written logic is wrong
  class Shark extends Fish {
    override def breed: List[Animal[Fish]] = List(new Cod)
  }

  class Cod extends Fish{
    override def breed: List[Animal[Fish]] = ???
  }
*/

  // Solution No. 4  - Type Classes

/*
  trait Animal
  trait CanBreed[A] {
    def breed(a : A) : List[A]
  }

  class Dog extends Animal
  object Dog {
    implicit object DogsCanBreed extends CanBreed[Dog] {
      override def breed(a: Dog): List[Dog] = List()
    }
  }

  implicit class CanBreedOps[A](animal: A) {
    def breed(implicit canBreed: CanBreed[A]) : List[A] = canBreed.breed(animal)
  }

  val dog = new Dog
  dog.breed
  /*
   new CanBreedOps[Dog](dog).breed(Dog.DogsCanBreed)
   implicit value to pass to breed : Dog.DogsCanBreed
   */

  class Cat extends Animal
  object Cat {
    implicit object CatsCanBreed extends CanBreed[Dog] {
      override def breed(a: Dog): List[Dog] = List()
    }
  }

  val cat = Cat
  cat.breed
*/

  trait Animal[A] {  // Pure type classes
    def breed(a : A) : List[A]
  }

  class Dog
  object Dog {
    implicit object DogAnimal extends Animal[Dog]{
      override def breed(a: Dog): List[Dog] = List()
    }
  }

  class Cat
  object Cat {
    implicit object DogAnimal extends Animal[Dog] {
      override def breed(a: Dog): List[Dog] = List()
    }
  }

  implicit class AnimalOps[A](animal: A){
    def breed(implicit animalTypeClassInstance : Animal[A]) : List[A] =
      animalTypeClassInstance.breed(animal)
  }

  val dog = new Dog
  dog.breed

  // below code's error found in current scala version
/*  val cat = new Cat
  cat.breed*/
}
