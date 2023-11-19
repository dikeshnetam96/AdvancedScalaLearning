package Exercise

import lectures.part4implicits.L34_TypeClasses.{HTMLSerializer, User, john}

import java.security.Permission

object EqualtyPlayground extends App {
  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }


  // TYPE Class Instances
  implicit object nameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  // TYPE Class Instances
   object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name && a.email == b.email
  }

  object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]): Boolean =
      equalizer.apply(a, b)
  }

  val anotherJohn = User("John", 32, "john@rockthejmmmm.com")
  //println(Equal(john, anotherJohn))
  // AD-HOC polymorphism

  /*
  Exercise -  Improve the Equal TC with an implicit conversion class
  ===(another value : T)
  !==(anotherValue : T)
   */

  implicit class TypeSafeEqual[T](value : T) {
    def ===(other : T) (implicit equalizer : Equal[T]) : Boolean = equalizer.apply(value,other)
    def !==(other : T) (implicit equalizer : Equal[T]) : Boolean = !equalizer.apply(value,other)

  }

/*
 println(john)
  println(anotherJohn)
*/



  println(john.==(anotherJohn))  // this line has written in below mentioned explanation
/*
john.==(anotherJohn)
new TypeSafeEqual[User](join).===(anotherJohn)
new TypeSafeEqual[User](join).===(anotherJohn)(NameEquality)
 */

  /*
  TYPE SAFE
   */

  /*
  Scala 3 added a feature called "multiversal equality" , and john==42 will not compile, but in older version it works
   */
//  println(jon==43)

//  println(john === 43) // TYPE SAFETY // compiler will force, that both values should be of same type

  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }
  // context bounds
  def htmlBoilerPlate[T](content : T)(implicit serializer : HTMLSerializer[T]) : String = s"<html><body> ${content.toHTML(serializer)} </html></body>"

  // context bounding  --> here we can not use content.toHTML(serializer)
  /*
  Cond  - we can not use serializer by name.... because the compiler inject it.....
   */

  // implicitly
  def htmlSugar[T :  HTMLSerializer] (content : T) : String = {
    val serialzer = implicitly[HTMLSerializer[T]]
    s"<html><body> ${content.toHTML(serialzer)} </html></body>"
  }

  // implicitly
  case class Permissions(mask : String)
  implicit val defaultPermissions: Permissions = EqualtyPlayground.Permissions("0744")

  // in some other part of the code
  /*
  so notice that we use implicit values mostly as implicit parameters into methods and we only use their APIs inside those methods
  but if we need them in some other part the code, it makes sense to want to surface them out and the implicitly method does exactly that.
   */
  val standardPerms = implicitly[Permissions]

}
