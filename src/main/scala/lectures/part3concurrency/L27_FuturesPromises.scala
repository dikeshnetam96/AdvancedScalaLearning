package lectures.part3concurrency


import com.sun.jdi.InconsistentDebugInfoException

import java.awt.Paint
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success}
// important for futures
import scala.concurrent.ExecutionContext.Implicits.global

object L27_FuturesPromises extends App {

  // futures are functional ways of computing something in parallel.
  /*
  In Scala, a "Future" is a concurrency construct used to represent a value that may not be available yet,
  but will be computed asynchronously in the future. It is a way to work with asynchronous or concurrent
  operations in a functional and expressive manner.
   */
  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife // calculate the meaning of life on ANOTHER thread
  } // (global) which is passed by the compiler

  println(aFuture.value) // It will return an Option[Try[Int]]

  println("waiting on the future")
  aFuture.onComplete(t => t match {
    case Success(meaningOFLife) => println(s"the meaning of life is $meaningOFLife")
    case Failure(e) => println(s"I have failed with $e")
  }) // SOME Thread

  Thread.sleep(3000)

  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile) = {
      println(s"${this.name} poking ${anotherProfile.name}")
    }
  }

    object SocialNetwork {
      // "database"
      val names = Map(
        "fb.id.1-zuck" -> "Mark",  //fb.id.1-zuck
        "fb.id.2-bill" -> "Bill",
        "fb.id.0-dummy" -> "Dummy"
      )
      val friends = Map(
        "fb.id.1-zuck" -> "fb.id.2-bill"
      )

      val random = new Random()

      // API
      def fetchProfile(id: String): Future[Profile] = Future {
        // fetching from the DB
        Thread.sleep(random.nextInt(300))
        Profile(id, names(id))
      }

      def fetchBestFriend(profile: Profile): Future[Profile] = Future {
        Thread.sleep(random.nextInt(400))
        val bfId = friends(profile.id)
        Profile(bfId, names(bfId))
      }
    }

    // client : mark to poke bill



    // functional composition
    // Map, flatMap, filter
    /*
          val nameOnTheWall = mark.map(profile => profile.name)
          val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
          val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("Z"))
    */

    /*     // for-comprehensions
         for{
           mark <- SocialNetwork.fetchProfile()
         }*/



  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
 /* println("seee")
  mark.onComplete {
    case Success(markProfile) => {
      val bill = SocialNetwork.fetchBestFriend(markProfile)
      bill.onComplete {
        case Success(billProfile) => markProfile.poke(billProfile) // this is the only useful code, which is written here inside nested code[THIS IS NOT GOOD]
        case Failure(e) => e.printStackTrace()
      }
    }
    case Failure(ex) => ex.printStackTrace()
  }*/


  // functional composition of futures
  // map, flatmap, filter

  // Below mentioned code is the one of the best way to write the above code
  val nameOnTheWall = mark.map(profile => profile.name)

  val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))

  val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("Z"))

  // for-comprehensions
  for{
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)

  Thread.sleep(1000)

  // fallbacks
 val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown-id").recover{
    case e : Throwable => Profile("fb.id.0-dummy","Forever alone")
  }  // recover system on dummy input

  val aFetchedProfileNOMatterWaht = SocialNetwork.fetchProfile("unknown id").recoverWith{
    case e : Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
  }

  // first below function will compute values and then if we found some error it will go to fallbackTo method  // below call is incorrect, focus on concepts
  val fallbackResult = SocialNetwork.fetchProfile("unknown id").fallbackTo(SocialNetwork.fetchProfile(SocialNetwork.fetchProfile("fb.id.0-dummy").toString))

  // Online banking app
  case class User(name : String)
  case class Transaction(sender : String, recover : String, amount: Double, status : String)

  object BankingApp{
    val name = "Rock the JVM Banking"
    def fetchUser(name : String) : Future[User] = Future {
      // simulate fetching from the DB
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName : String, amount : Double) : Future[Transaction] = Future{
      // simulate some processes
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    def purchase(username : String, item : String, merchantName : String, cost : Double) : String = {
      // fetch the user from the DB
      // create a transaction
      // WAIT for the transaction to finish
      val transcationStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user,merchantName,cost)

      } yield transaction.status
      Await.result(transcationStatusFuture, 2.seconds) // implicit conversions -> pimp my library
      /*
      In Scala, Await.result() is a function that allows you to block the current thread until a
      Future completes and returns its result. It's a way to make asynchronous code behave
      synchronously at a specific point in your program.
       */
    }
  }

  println(BankingApp.purchase("Danial", "iPhone 12","Rock the JVM Store", 4000))

  // Promises

  val promise = Promise[Int] // "controller" over a future
  val future = promise.future

  // thread 1 - "consumer"
  future.onComplete{
    case Success(r) => println("[Consumer] I've received "+r)
  }

  // thread 2 - "producer"
  val producer = new Thread(() => {
    println("[producer] crunching numbers....")
    Thread.sleep(1000)
    // "fulfilling" the promise
    promise.success(42)
    println("[producer] done")
  })
  producer.start()
  Thread.sleep(1000)
}
