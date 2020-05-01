package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App{


  // JVM thread


  val runnable = new Runnable {
    override def run(): Unit = println("Running in parallel") // code you want to run in parallel
  }

  val aThread =  new Thread(runnable)   // a Thread is an instance of a class

  //aThread.start() // gives the signal to the JVM to start a JVM thread
  // Creates a JVM thread that runs on top of an OS thread. This thread is a different one than the one used to evaluate all this code.
  //runnable.run() // does jack shit in parallel
  // if you want to run code in in parallel you need to call the start method on a thread instance and NOT the run method on the runnable
  //aThread.join() // blocks until a thread finishes running

  // EX:1
  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello"))) // receives a runnable (in lambda form) and prints 5 times hello
  val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("good bye"))) // receives a runnable (in lambda form) and prints 5 times hello
  //threadHello.start()
  //threadGoodbye.start()

  // run it a couple of times, you'll get different results. Thread scheduling depends on the OS and the JVM implementation

  // Thread are very expensive to start and kill, so the solution is to re use them, withe executors and thread pools

  // Executors
  val pool = Executors.newFixedThreadPool(10)
  //pool.execute(() => println("something in the thread pool")) // So this runnable (in lambda form again) will get executed by one of the 10 threads managed by the thread pool.
  // i don't really need to care about starting and stopping threads

  // example 2: using the pool
 //pool.execute(() => {
 //  Thread.sleep(1000)
 //  println(" done after 1 second")
 //})

 //pool.execute(() => {
 //  Thread.sleep(1000)
 //  println(" almost done")
 //  Thread.sleep(1000)
 //  println(" done after 2 second")
 //})

  //pool.shutdown() // stops the pool from accepting more action after this line. Basically a graceful kill
  // pool.execute(() => ("will this run")) // No it wont, it will throw an exception in the calling thread because the pool has been killed. Only accepting the previous 3 actions

   // pool.shutdownNow // akin to a kill now or interrupt now. Basically it will kill the pool before the tasks finish (in my run only the first task which does something immediately returned something)
  //println(pool.isShutdown) // will return true even if you have 2 threads still doing stuff. Shutdown only stops from accepting new actions, unlike shutdownNow


  // Main Pain point
  def runInParallel = {
    var x = 0
    val thread1 = new Thread(() => {
      x = 1
    })
    val thread2 = new Thread(() => {
      x = 2
    })
    thread1.start()
    thread2.start()
    println(x)
  }

  //for (_ <- 1 to 100) runInParallel// you'll see that 99.9% of the time it will be 0, because the println() got executed before any of the threads got to run

  // This is called a race condition, because 2 threads are attempting to set the same memory zone at the same time (very bad and very hard to fix)

  // Race condition case 1 (comment all other threads before running this one):

  class BankAccount(var amount: Int) {
    override def toString: String = "" + amount
  }

  def buy(account: BankAccount, thing: String, price: Int) = {
    account.amount -= price
    //println("I've bought " + thing) // debug
    //println("My account is now " + account) // debug
  }

  //for (_ <- 1 to 10000) {
  //  val account = new BankAccount(80000)
  //  val thread1 = new Thread(() => buy(account, "potato", 1000))
  //  val thread2 = new Thread(() => buy(account, "rhubarb", 9000))
//
  //  thread1.start()
  //  thread2.start()
  //  Thread.sleep(10)
  //  if (account.amount != 70000) println("RACE CONDITION" + account.amount)
  //  //println() // debug
//
  //  // In my last run, we got 2 race conditions out of 10K  (one with 79k another with 71k), which is 2 too many for a banking software
  //}
  /* Rough sketch of what happens, both execute at the precise same time and this happens:
  thread1 (potato): 80000
   - account = 80000 - 1000 = 79000
   thread2 (rhubarb): 80000
   - account = 80000 - 9000 = 71000
   */

  // Option #1: use synchronized() (Comment all previous threads executions)

  def buySafe(account: BankAccount, thing: String, price: Int) =
    account.synchronized{
      account.amount -= price
      //println("I've bought " + thing) // debug
      //println("My account is now " + account) // debug
    }
  for (_ <- 1 to 10000) {
    val account = new BankAccount(80000)
    val thread1 = new Thread(() => buySafe(account, "potato", 1000))
    val thread2 = new Thread(() => buySafe(account, "rhubarb", 9000))

    thread1.start()
    thread2.start()
    Thread.sleep(10)
    if (account.amount != 70000) println("RACE CONDITION" + account.amount)


    // no issues :D
  }

  // Option 2: use @ volatile

  /*
    class BankAccount(@volatile var amount: Int) {
    override def toString: String = "" + amount
  }

  // and thats it

   */

  // Better option is to use synchronized (more powerful)






}
