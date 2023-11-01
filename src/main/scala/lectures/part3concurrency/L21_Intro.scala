package lectures.part3concurrency

import java.util.concurrent.{Executor, Executors}

object L21_Intro extends App {

  /*
    Notes :
    1. So much use of var will be here
    2. Focuse on JVM Threads (parallel programming = thread)
   */

  /*
  interface Runnable {
  public void run()
  }
   */

  val runnable = new Runnable {
    override def run(): Unit = println("Running in parallel ")
  }
  val aThread = new Thread(runnable)

  aThread.start() // gives signal to the JVM to start a JVM thread
  // create a JVM thread => OS thread(runs on top of OS thread)

  runnable.run() // doesn't do anything in parallel

  aThread.join() // it will block current thread execution until current thread complete it's execution...


  /* ----- Threads are very expensive to start and kill ------ */

  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
  val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("good bye")))

  threadHello.start()
  threadGoodbye.start()
  // different runs produce different results

  // executors : n Java, Executors is a utility class that provides a high-level and simplified way
  // to manage and work with concurrent tasks and threads in a multi-threaded application.
  // It's part of the java.util.concurrent package

  // executors
  val pool = Executors.newFixedThreadPool(10)
  // replacing runnable using ()
  pool.execute(() => println("something in the thread pool"))

  pool.execute(() => {
    Thread.sleep(1000)
    println("done after 1 second")
  })

  pool.execute(() => {
    Thread.sleep(1000)
    println("almost done") //     println("done after 1 second") this is also working at the same time...
    Thread.sleep(1000)
    println("done after 2 seconds")
  })

  /*
  pool.shutdown()
  pool.execute(() => println("should not appear")) // throws an exception in the calling thread
  */

  /*
   shutdownNow() attempts to stop tasks, it cannot guarantee that all tasks will be terminated immediately,
   as some tasks may be unresponsive to interruption or may be in a state where they cannot be safely stopped.
   */
  // pool.shutdownNow()

  pool.shutdown()
  //  pool.execute(() => println("should not appear")) // after shutdown pool will not accept new task.

  /*
  When you call pool.shutdown(), the following happens:

  1. The thread pool stops accepting new tasks. It prevents any new tasks from being submitted for execution.
  2. The thread pool continues to execute tasks that are already in its queue or actively running, allowing them to complete.
  3. After all active tasks have completed, the thread pool shuts down, releasing its resources and terminating its worker threads.
   */
  println(pool.isShutdown)
}
