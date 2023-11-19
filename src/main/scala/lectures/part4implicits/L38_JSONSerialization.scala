package lectures.part4implicits

import java.util.Date

object L38_JSONSerialization extends App {

  /*
    Users, Ports, feeds
    Serialize to JSON
   */

  case class User(name: String, age: Int, email: String)

  case class Post(content: String, createdAt: Date)

  case class Feed(user: User, posts: List[Post])

  /*
  1 - intermediate data types : Int, String, List, Date
  2 - type classes for conversion to intermediate data types
  3 - serialize to JSON
  */

  sealed trait JSONValue { // Intermediate Data Type
    def stringify: String
  }

  final case class JSONString(value: String) extends JSONValue {

    def stringify: String = "\"" + value + "\""

  }

  final case class JSONNumber(value: Int) extends JSONValue {

    override def stringify: String = value.toString

  }

  final case class JSONArray(list: List[JSONValue]) extends JSONValue {
    override def stringify: String = list.map(_.stringify).mkString("[", ",", "]")
  }

  final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    /*
    {
      name : "John"
      age : 22
      friend : [ ... ]
      latestPost : {
          content : "Scala Rocks"
          date : ...
      }
    }
     */

    override def stringify: String = values.map {
      case (key, value) => "\"" + key + "\"" + value.stringify
    }.mkString("{", ",", "}")
  }

  val data = JSONObject(Map(
    "user" -> JSONString("Daniel"),
    "posts" -> JSONArray(List(
      JSONString("Scala Rocks!!"), JSONNumber(453)
    ))
  ))

  println(data.stringify)

  // type class
  /*
    1 - type class
    2 - type class instances (implicit)
    3 - pim library to use type class instances
   */

  //2.1
  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }

  // 2.3 Conversion

  implicit class JSONOps[T](value: T) {
    def toJSON(implicit converter: JSONConverter[T]): JSONValue =
      converter.convert(value)
  }

  // 2.2
  implicit object StringConverter extends JSONConverter[String] {
    def convert(value: String): JSONValue = JSONString(value)
  }

  implicit object NumberConverter extends JSONConverter[Int] {
    def convert(value: Int): JSONValue = JSONNumber(value)
  }

  // custom data types
  implicit object UserConverter extends JSONConverter[User] {
    def convert(user: User): JSONValue = JSONObject(Map(
      "name" -> JSONString(user.name),
      "age" -> JSONNumber(user.age),
      "email" -> JSONString(user.email)
    ))
  }

  implicit object PostConverter extends JSONConverter[Post] {
    override def convert(post: Post): JSONValue = JSONObject(Map(
      // content: String, createdAt: Date
      "content" -> JSONString(post.content),
      "created" -> JSONString(post.createdAt.toString)
    ))
  }

  implicit object FeedConverter extends JSONConverter[Feed] {
    override def convert(feed: Feed): JSONValue = JSONObject(Map(
//      "user" -> UserConverter.convert(feed.user),
      "user" -> feed.user.toJSON,
//      "posts" -> JSONArray(feed.posts.map(PostConverter.convert))
      "posts" -> JSONArray(feed.posts.map(_.toJSON))
    ))
  }



  // call stringify on result
  val now = new Date(System.currentTimeMillis())
  val john = User("John",34,"john@rockthejvm.com")
  val feed = Feed(john,List(
    Post("hello",now),
    Post("look at this cute puppy",now)
  ))

  /*
  The line println(feed.toJSON.stringify) is working due to the usage of type classes and implicit conversions defined in the code.
  Let's break down how this line works step by step:

  feed.toJSON: This part of the line is calling the toJSON method on the feed object. This method is an implicit
  conversion method that has been added to all values of type T for which a JSONConverter[T] is defined. In this case,
  it converts the feed object into a JSONValue by using the FeedConverter that has been defined as an implicit object.

  .stringify: After converting feed into a JSONValue, the stringify method is called on the resulting JSONValue.
  This method is defined for all types that extend the JSONValue trait and is used to convert the JSONValue into
  a string representation of JSON.

  So, in summary, the line println(feed.toJSON.stringify) works as follows:

  It first converts the feed object into a JSONValue by using the FeedConverter implicit object,
  which knows how to convert a Feed object into a JSONValue. Then, it converts the resulting
  JSONValue into a JSON string representation using the stringify method.
   */
  println(feed.toJSON.stringify)


}
