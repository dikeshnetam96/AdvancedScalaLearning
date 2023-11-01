package lectures.part3concurrency

import java.util.concurrent.ThreadLocalRandom

object L22_JVMConcurrencyProblem {

  def runINParallel(): Unit = {
    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })

    val thread2 = new Thread(() => {
      x = 2
    })

    thread1.start()
    thread2.start()
    // sleep statement is optional
    println(x) // we can not control the output at any point of time, it is the biggest draw back of it.
    // This above condition is called Race Condition
    // It cause because we used mutable variable
  }

  case class BankAccount(var amount : Int)

  def buy(bankAccount: BankAccount, thing: String, price: Int) : Unit = {
    /*
    involves 3 steps
    - read old values
    - compute result
    - write new value
     */
    bankAccount.amount -= price
  }

  def buySafe(bankAccount: BankAccount, thing: String, price: Int) : Unit = {
    bankAccount.synchronized{ // does not allow multiple thread to run the critical section AT THE SAME TIME
      bankAccount.amount-=price // critical section
    }
  }


  /*
  Example of race condition :
    thread1 (shoes)
      - read amount 50000
      - compute result 50000-3000 = 47000

    thread2 (iPhone)
      - reads amount 50000
      - compute result 50000 - 3000 = 46000

    thread1 (shoes)
      - write amount 47000
    thread2 (iPhone)
      - write amount 46000
   */
  def demoBankingProblem():Unit ={

    (1 to 10000).foreach { _ =>
      val account = BankAccount(50000)
      val thread1 = new Thread(() => buySafe(account,"shoes",3000))
      val thread2 = new Thread(() => buySafe(account,"iPhone",4000))
      thread1.start()
      thread2.start()
      thread1.join()
      thread2.join()
      if(account.amount != 43000) println(s" AHA! I've just broken the bank : ${account.amount}")
    }
  }

  /**
   Exercises
   1. create "inception threads"
    thread 1
   --> thread 2
      --> thread 3
        .....

   each thread prints "hello from thread $i"
   print all message in REVERSE ORDER



   */

    // Assessment No. 1
/*    def createThread( n : Int  ) : Unit = {
      if(n!=0) {
        val create = new Thread()
        create.start()
        createThread(n-1)
        println(create.threadId()+" : is the thread id")
      }
      else {
       }
    }*/

    def inceptionThreads(maxThreads : Int, i: Int =1 ) : Thread =
      new Thread(()=> {
        if(i<maxThreads) {
          val newThread = inceptionThreads(maxThreads, i+1)
          newThread.start()
          newThread.join()
        }
        println(s"hello from thread $i")
      })

    // Assessment No. 2
    def minMaxX() : Unit = {
      var x = 0
      val threads = (1 to 100).map(_=> new Thread(()=>x+=1))
      threads.foreach(_.start())
    }

  // Assessment No. 3
  /*
  almost always, message = "Scala is awesome"
  is it guaranteed ? NOOOOO
  Obnoxious situation (possible) :

  main Thread :
    message = "Scala sucks"
    awesomeThread.start()
    sleep(1001) - yield execution
  awesome thread:
    sleep(1000) - yield execution
  OS gives the CPU to some important thread, takes> 2s
  OS give the CPU back to the main thread

  main thread :
    println(message) // "Scala Sucks"
  awesome thread :
    message = "Scala is awesome"
   */
  def demoSleepFallacy(): Unit = {
    var message = ""
    val awesomeThread = new Thread(() => {
      Thread.sleep(1000)
      message = "Scala is Awesome"
    })

    message = "Scala sucks"
    awesomeThread.start()
    Thread.sleep(1001)
    // solution : join the worker thread
    awesomeThread.join()
    println(message)
  }
  def main(args: Array[String]): Unit = {
//    runINParallel()
//    demoBankingProblem()
    inceptionThreads(4).start()  // Assessment NO. 1
    demoSleepFallacy()

  }

}
