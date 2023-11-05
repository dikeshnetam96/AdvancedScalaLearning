package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object L23_ThreadCommunicaiton extends App {

  // The Producer-consumer problem

  //  problem -> [x] -> consumer
  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0

    def set(newValue: Int) = value = newValue

    def get = {
      val result = value
      value = 0
      result
    }
  }

  def naiveProdCOns(): Unit = {
    val container = new SimpleContainer
    val consumer = new Thread(() => {
      println("[Consumer] is waiting....")
      while (container.isEmpty) {
        println("[Consumer] actively waiting....")
      }
      println("[Consumenr] I have consumenrd " + container.get)
    })

    val producer = new Thread(() => {
      println("[Producer] computing....")
      Thread.sleep(500)
      val value = 42
      println("[Producer] I have produced, after long work, the value " + value)
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

  // execution of method of naiveProdCons
  // naiveProdCOns()

  /*
  Some Discussions :

  - Synchronization : locks the object's monitor, it monitors variable in locked by which thread.

  - wait : When a thread calls wait(), it enters a "waiting" state and allows other threads
           to acquire the lock. The thread that calls wait() will remain in the waiting state until
           another thread calls notify() or notifyAll() on the same object, signaling that it can continue.

  - notify : This method is used to wake up one of the threads that are currently waiting on the same object
             (the one on which wait() was called). When a thread calls notify(), it signals one of the
             waiting threads to wake up and continue execution.

  - notifyAll() :  This method is used to wake up all the threads that are currently waiting on the same object.
                   When a thread calls notifyAll(), all waiting threads are awakened, and they will compete for
                   the lock once it is released by the notifying thread.
   */

  // wait and notify
  def smartProdCons() = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting...")
      container.synchronized {
        container.wait()
      }
      // container must have some value
      println("[consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[Producer] Hard to Work...")
      Thread.sleep(2000)
      val value = 42

      container.synchronized {
        println("[Producer] I'm producing " + value)
        container.set(value)
        container.notify()
      }
    })

    consumer.start()
    producer.start()
  }

  //  smartProdCons()

  /*
  Now increasing the level
  producer -> [? ? ?] -> consumer
   */


  def prodConsLargeBuffer() = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random()
      while (true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println("[Consumer] buffer empty, waiting")
            buffer.wait()
          }

          // there must be at least ONE VALUE in the buffer
          val x = buffer.dequeue()
          println("[Consumer] consumed a Value " + x)

          // todo // hey producer, there's empty space available, are you lazy?
          buffer.notify()
        }
        Thread.sleep(random.nextInt(500))
      }
    })

    val producer = new Thread(() => {

      val random = new Random()
      var i = 0

      while (true) {
        buffer.synchronized {
          if (buffer.size == capacity) {
            println("[producer] buffer is full, waiting...")
            buffer.wait()
          }

          // there must be at least ONE EMPTY SPACE in the buffer
          println("[producer] producing " + i)
          buffer.enqueue(i)

          // todo  // hey consumer, new food for you
          buffer.notify()

          i += 1
        }
        Thread.sleep(random.nextInt(500))
      }
    })
    consumer.start()
    producer.start()
  }

  // main execution code
  // prodConsLargeBuffer()


  /*

  Prod-Cons, level 3 :  limited resources, with multiple producer and multiple consumers

   Producer 1 --> [? ? ? ] --> Consumer 1
      Producer 1 ----^ ^---- Consumer 1
   */

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      var i = 0

      while (true) {
        buffer.synchronized {
          while (buffer.size == capacity) {
            println(s"[producer $id] buffer is full, waiting...")
            buffer.wait()
          }

          // there must be at least ONE EMPTY SPACE in the buffer
          println(s"[producer $id] producing " + i)
          buffer.enqueue(i)

          // todo  // hey consumer, new food for you
          buffer.notify()

          i += 1
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }

  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {

    /*
              producer produces value, two Cons are waiting
              notifies ONE consumer, notifies on buffer
              notifies the other consumer

             */
    override def run(): Unit = {
      val random = new Random()
      while (true) {
        buffer.synchronized {
          while (buffer.isEmpty) {
            println(s"[Consumer $id] buffer empty, waiting")
            buffer.wait()
          }

          // there must be at least ONE VALUE in the buffer
          val x = buffer.dequeue()
          println(s"[Consumer $id] consumed a Value " + x)

          // todo // hey producer, there's empty space available, are you lazy?
          buffer.notify()
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }

  def multiProdCons(nConsumers: Int, nProducers: Int): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3
    (1 to nConsumers).foreach(i => new Consumer(id = i, buffer = buffer).start())
    (1 to nConsumers).foreach(i => new Producer(id = i, buffer = buffer, capacity = capacity).start())
  }

  //  multiProdCons(3,6)

  /*
  Exercises :

  1. Think of an example where notifyAll acts in a different way than notify?
  2. create a deadlock
  3. create a live lock
   */


  //notifyall
  // notify vs notifyall --> notify will works differently here, because all threads are in waiting but notify will release
  // only one and left will be blocked...
  def testNotifyAll() = {
    val bell = new Object

    (1 to 10).foreach(i => new Thread(() => {
      bell.synchronized {
        println(s"[thread $i] waiting....")
        bell.wait()
        println(s"[thread $i] hooray!....")
      }
    }).start())

    new Thread(() => {
      Thread.sleep(2000)
      println("[announcer] Rock'n roll!")
      bell.synchronized {
        //        bell.notifyAll()
        bell.notify()
      }
    }).start()
  }
  // execution code below
  //  testNotifyAll()

  // 2. DeadLock
  case class Friend(name: String) {
    def bow(other: Friend) = {
      this.synchronized {
        println(s"$this : I am bowing to my friend $other")
        other.rise(this)
        println(s"$this : my friend $other has risen")
      }
    }

    def rise(other: Friend) = {
      this.synchronized {
        println(s"$this : I am rising to my firned $other")
      }
    }

    var side = "right"

    def switchSide() = {
      if (side == "right") side = "left"
      else side = "right"
    }

    def pass(other: Friend) = {
      while (this.side == other.side) {
        println(s"$this : oh, but please, $other, feel free to pass")
        switchSide()
        Thread.sleep(1000)
      }
    }
  }

  val sam = Friend("Sam")
  val pierre = Friend("Pierre")
  /*
    new Thread(() => sam.bow(pierre)).start()
    new Thread(() => pierre.bow(sam)).start()
  */


  // 3 - live lock
  new Thread(() => sam.pass(pierre)).start()
  new Thread(() => pierre.pass(sam)).start()
}
