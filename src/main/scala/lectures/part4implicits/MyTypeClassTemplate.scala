package lectures.part4implicits

// TYPE CLASS
// by using below trait we can implement many methods 1st can be, which operates Ordering data types, 2nd other like Date, etc...

trait MyTypeClassTemplate[T] {
  def action(value: T): String
}
object MyTypeClassTemplate {
  def apply[T](implicit instance :  MyTypeClassTemplate[T]) = instance
}
