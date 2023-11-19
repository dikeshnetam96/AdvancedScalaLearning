package lectures.part4implicits

object L34_TypeClasses extends App {
/*
  A type class is defined by a trait that declares one or more methods representing
  the behavior or functionality. This trait serves as a contract that types must adhere
  to if they want to be considered instances of the type class.
 */

  //  a type class is a design pattern used to define and enforce behaviors for types
  //  in a generic and extensible way.

  trait HTMLWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHtml: String = s"<div> $name ($age yo) <a href = $email/> </div>"
  }

  println(User("John", 32, "john@rockthejvm.com").toHtml)

  /*
  1 - for the types WE write
  2 - ONE implementation out of quite a number
   */

  // Option 2 - pattern matching
  object HTMLSerializerPM {
    def serializeToHtml(value: Any) = value match {
      case User(n, a, e) =>
      //      case java.util.Date =>
      case _ =>
    }
  }

  /*
  1 - lost type safety
  2 - need to modify the code every time
  3 - still ONE implementation
   */

  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  implicit object UserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div> ${user.name} (${user.age} yo) <a href = ${user.email}/> </div>"
  }

  val john = User("John", 32, "john@rockthejvm.com")

  println("generic method to use code efficiently")
  println(UserSerializer.serialize(john))

  // 1 - we can define serializer for other types

  import java.util.Date

  object DataSerializer extends HTMLSerializer[Date] {
    override def serialize(date: Date): String = s"<div> ${date.toString} </div>"
  }

  // 2 - we can define MULTIPLE Serializers
  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div> ${user.name} </div>"
  }

  /*
  Equality
   */
  // TYPE class
  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }

  // TYPE Class Instances
  object nameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  // TYPE Class Instances
  implicit object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name && a.email == b.email
  }

  val res = nameEquality(User("John", 32, "john@rockthejvm.com"), User("John", 32, "john@rockthejvm.com"))
  println(res)

  val res2 = FullEquality(User("John2", 32, "john@rockthejvm.com"), User("John", 32, "john@rockthejvm.com"))
  println(res2)

  // Part 2
  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = s"<div style : color = blue> $value </div>"
  }
  /*
  implicit object UserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div> ${user.name} (${user.age} yo) <a href = ${user.email}/> </div>"
  }
   */

  println(HTMLSerializer.serialize(42)) // implicit IntSerializer is here to be passed
  /*
  In the code's main part, it first serializes an integer 42 using HTMLSerializer.serialize(42).
  Since there's an implicit IntSerializer available, it uses that serializer to convert 42 into the HTML string.
   */
  println(HTMLSerializer.serialize(john)) // here , implicit object UserSerializer will be passed
  /*
  HTMLSerializer.serialize(john). In this case, the code comments out the implicit UserSerializer,
  so it should not compile unless you uncomment it. The UserSerializer would be used to serialize
  the User object into an HTML string.
   */

  // It has access to entire type class interface
  println(HTMLSerializer[User].serialize(john))

  /*
    Exercise :
      - Implement the TC pattern for the Equality tc.
   */

  object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]): Boolean =
      equalizer.apply(a, b)
  }

  val anotherJohn = User("John", 32, "john@rockthejmmmm.com")
  println(Equal(john, anotherJohn)) // AD-HOC polymorphism
  // when two distinct or potentially unrelated types have Equalizers implemented, then we can
  // call this equal thing on them regardless on their types
  /*
  Compiler takes care to fetch the correct type class instance for our types
   */

  // part 3
  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }
  //  println(john.toHtml(UserSerializer )) // pre-written --> println(new HTMLEnrichment[User](john).toHTML(UserSerialize))
  //
  println(new HTMLEnrichment[User](john).toHTML(UserSerializer))

  println(john.toHtml)  // println(new HTMLEnrichment[User](john).toHTML(UserSerializer))

  /*
   - extend to new type
   - choose implementation
   - Super Expressive
   */

  println(2.toHTML) // println(new HTMLEnrichment[User](john).toHTML(UserSerializer))
  //  println(john.toHtml(PartialUserSerializer))

  /*
  - type class itself --- HTMLSerializer[T] {...}
  - type class instances (some of which are implicit)  ---
  - conversion with implicit classes
   */
}