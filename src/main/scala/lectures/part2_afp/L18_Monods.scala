package lectures.part2_afp

object L18_Monods extends App {

  /*
  In Scala, a monad is a design pattern used in functional programming.
  It's a mechanism for sequencing computations. Monads can be thought of
  as a type of wrapper around a value, which also provides a set of
  operations to work with that value.
   */

  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }

  object Attempt {
    def apply[A](a: => A): Attempt[A] =
      try {
        Success(a)
      } catch {
        case e: Throwable => Fail(e)
      }
  }

  case class Success[+A](value: A) extends Attempt[A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B] =
      try {
        f(value)
      }
      catch {
        case e: Throwable => Fail(e)
      }
  }

  case class Fail(e: Throwable) extends Attempt[Nothing] {
    def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

  /*

  left-identity

  unit.flatMap(f) = f(x)
  Attempt(x).flatMap(f) = f(x) // success case!
  Success(x).flatMap(f) = f(x) // proved

  right-identity

  attempt.flatMap(unit) = attempt
  Success(x).flatMap(x => Attempt(x)) = Attempt(x) = Success(x)
  Fail(e).flatMap(...) = Fail(e)


  Associativity

  attempt.flatMap(f).flatMap(g) == attempt.flatMap(x=>f(x).flatMap(g))
  Fail(e).flatMap(f).flatMap(g) = Fail(e)
  Fail(e).flatMap(x => f(x).flatMap(g)) = Fail(e)

  Success(v).flatMap(f).flatMap(g) =
    f(v).flatMap(g) or Fail(e)

  Success(v).flatMap(x => f(x).flatMap(g)) =
    f(v).flatMap(g) or Fail(e)

   */

  val attempt = Attempt {
    throw new RuntimeException("My own monad, yes!")
  }

  /*
  Exercise :

  1. implement a lazy[T] monad = computaiton which will only be executed when it's needed
  unit/apply
  flatmap

  2. Monads = unit + flatMap
     Monads = unit + map + flatten

  Monad[T] {
  def flatMap[B](f : T => Monad[B]) : Monad[B] = ....(implemented)

  def map[B](f : T => B) : Monad[B] = ???
  def flatten(m : Monad[Monad[T]]) : Monad[T] = ???
  }
   */

  // 1. Lazy Monad
  class Lazy[+A](value: => A) {
    private lazy val internalValue = value

    def use: A = internalValue

    def flatMap[B](f: (=> A) => Lazy[B]): Lazy[B] = f(internalValue)
  }

  object Lazy {
    def apply[A](value: => A): Lazy[A] = new Lazy(value)
  }

  val lazyInstance = Lazy {
    println("Today i feel like not to do anything")
    42
  }

  //  println(lazyInstance.use) // it will exectued apply method internally
  val flatMappedInstance = lazyInstance.flatMap(x => Lazy {
    10 * x
  })

  val flatMappedInstance2 = lazyInstance.flatMap(x => Lazy {
    10 * x
  })

  /*
    In this below line apply method is called twice because parameter value is call by name
    so whenever it is called it will compute again and again [ possible solution is call by need]

    flatMappedInstance.use
    flatMappedInstance2.use
    */

  // for the below code apply method is called only once...[due to lazy key word]
  flatMappedInstance.use
  flatMappedInstance2.use

  /*
  left-identity
  unit.identity
  unit.flatMap(f) = f(v)
  Lazy.flatMap(f) = f(v)

  right-identity
  l.flatMap(unit) = l
  Lazy(v).flatMap(x => Lazy(x)) = Lazy(v)

  Associativity :
  l.flatMap(f).flatMap(g) = l.flatMap(x => f(x).flatMap(g))
  Lazy(v).flatMap(f).flatMap(g) = f(v).flatMap(g)
  Lazy(v).flatMap(x => f(x).flatMap(g)) = f(v).flatMap(g)

  // 2. Map and flatten in terms of flatMap

  monad[T] { // List
    def flatMap[B](f: T => Monad[B]) : = .......... (implemented)

    def map[B](f : T => B) : Monod[B] = flatMap(x => unit(f(x))) // monad[B]
    def flatten(m : Monad[Monad[T]]) : Monad[T] = m.flatMap((x : Monad[T]) => x)

    List(1,2,3).map(_*2) = List(1,2,3).flatMap(x => List(x*2))
    List(List(1,2),List(3,4)).flatten = List(List(1,2),List(3,4)).flatMap(x => x) = List(1,2,3,4)
  }
  */

}
