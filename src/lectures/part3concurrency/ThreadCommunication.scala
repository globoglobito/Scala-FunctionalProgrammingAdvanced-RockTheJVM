package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {


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

  def naiveProdCons() = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[Consumer] waiting ...")
      while (container.isEmpty) {
        println("[Consumer] is actively waiting")
      }

      println("[Consumer] I have consumed " + container.get)
    })


    val producer = new Thread(() => {
      println("[producer] computing ...")
      Thread.sleep(500) // simulates heavy computing
      val value = 85
      println("[Producer] I have produced, after long work, the value" + value)
      container.set(value)
    })
    consumer.start()
    producer.start()
  }

  //naiveProdCons()

  // very simple example: It works well tbh as it does its purpose. However it is a very inefficient design as you are actively waiting (while loop) wasting computer resources

  /*
  Synchronized:
  Entering a .synchronized expression on a object locks the object.

  val someObject = "hello"
  someObject.synchronized {   <------- locks the object monitor
      // witty code    <--------- any other thread trying to run this, will block
      } <---- releases the lock

    Only AnyRefs can have synchronized blocks, primitive types like  Boolean or int doesn't have

  General Principles:
    - make no assumption about who gets the lock first
    - keep locking to a minimum
    - maintain thread safety all times in parallel applications

   */


  /* wait() and notify()
  wait() - ing on an object monitor suspends the thread indefinitely

  //thread 1
  val someObject = "Hello"
  someObject.synchronized { <-- locks the object's monitor
   ... code part 1
  someObject.wait() <- release the lock and ... wait
   ... code part 2 <--- when allowed to proceed, lock the monitor again and run the code

  // thread 2
  someObject.synchronized { <-- locks the object's monitor
     ... code
     someObject.notify() <--- signal ONE sleeping thread they may continue (which thread you don't know, it sup to the JVM)
      /// more code
      } <----- BUT only after I'm done and unlock the monitor

   */

  //better version
  def smartProdCons(): Unit = {

  val container = new SimpleContainer
  val consumer = new Thread(() => {
    println("[Consumer] waiting ...")
    container.synchronized {
      container.wait()
    }

    //container must have some value by this part
    println("[Consumer] I have consumed " + container.get)
  })

  val producer = new Thread(() => {
    println("[producer] computing ...")
    Thread.sleep(2200) // simulates heavy computing
    val value = 85

    container.synchronized {
      println("[producer] computing value " + value)
      container.set(value)
      container.notify() // because we ONLY have 1 waiting thread this is the one that will be notified
    } // but it will only be notified when the lock finishes... here
  })

  consumer.start()
  producer.start()
  }


  //smartProdCons()

  // proper way to coding parallel code, no busy waiting!



  /*
    producer -> [ ? ? ? ] -> consumer
   */

  def prodConsLargeBuffer(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random()

      while(true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println("[consumer] buffer empty, waiting...")
            buffer.wait()
          }

          // there must be at least ONE value in the buffer
          val x = buffer.dequeue()
          println("[consumer] consumed " + x)

          // hey producer, there's empty space available, are you lazy?!
          buffer.notify()
        }

        Thread.sleep(random.nextInt(250))
      }
    })

    val producer = new Thread(() => {
      val random = new Random()
      var i = 0

      while(true) {
        buffer.synchronized {
          if (buffer.size == capacity) {
            println("[producer] buffer is full, waiting...")
            buffer.wait()
          }

          // there must be at least ONE EMPTY SPACE in the buffer
          println("[producer] producing " + i)
          buffer.enqueue(i)

          // hey consumer, new food for you!
          buffer.notify()

          i += 1
        }

        Thread.sleep(random.nextInt(100))
      }
    })

    consumer.start()
    producer.start()
  }
  //prodConsLargeBuffer()


  /*
   Prod-cons, level 3
        producer1 ->  [ ? ? ? ] -> consumer1
        producer2 -----^     ^---- consumer2
  */


  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random()

      while(true) { // IF meant if empty or if woken up, now even if it wakes up, unless the buffer is empty it wont do anything
        buffer.synchronized {
          /*
            producer produces value, two Cons are waiting
            notifies ONE consumer, notifies on buffer
            notifies the other consumer
           */
          while (buffer.isEmpty) {
            println(s"[consumer $id] buffer empty, waiting...")
            buffer.wait()
          }

          // there must be at least ONE value in the buffer
          val x = buffer.dequeue() // OOps.!
          println(s"[consumer $id] consumed " + x)

          buffer.notifyAll()
        }

        Thread.sleep(random.nextInt(250))
      }
    }
  }

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      var i = 0

      while(true) {
        buffer.synchronized {
          while (buffer.size == capacity) {
            println(s"[producer $id] buffer is full, waiting...")
            buffer.wait()
          }

          // there must be at least ONE EMPTY SPACE in the buffer
          println(s"[producer $id] producing " + i)
          buffer.enqueue(i)

          buffer.notifyAll()

          i += 1
        }

        Thread.sleep(random.nextInt(500))
      }
    }
  }

  def multiProdCons(nConsumers: Int, nProducers: Int): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 20

    (1 to nConsumers).foreach(i => new Consumer(i, buffer).start())
    (1 to nProducers).foreach(i => new Producer(i, buffer, capacity).start())
  }

  multiProdCons(3, 6)


  /*
      Exercises.
      1) think of an example where notifyALL acts in a different way than notify?
      2) create a deadlock
      3) create a livelock
     */

  // notifyall vs notify -  In this case notifyall will wake up all threads, whilst notify will only wake 1 thread leaving the rest waiting. Because here there is a single call, no while loop that is constantly managing the relationships
  def testNotifyAll(): Unit = {
    val bell = new Object

    (1 to 10).foreach(i => new Thread(() => {
      bell.synchronized {
        println(s"[thread $i] waiting...")
        bell.wait()
        println(s"[thread $i] hooray!")
      }
    }).start())

    new Thread(() => {
      Thread.sleep(2000)
      println("[announcer] Rock'n roll!")
      bell.synchronized {
        bell.notify()
      }
    }).start()
  }

  // testNotifyAll()

  // 2 - deadlock
  case class Friend(name: String) {
    def bow(other: Friend) = {
      this.synchronized {
        println(s"$this: I am bowing to my friend $other")
        other.rise(this)
        println(s"$this: my friend $other has risen")
      }
    }

    def rise(other: Friend) = {
      this.synchronized {
        println(s"$this: I am rising to my friend $other")
      }
    }

    var side = "right"
    def switchSide(): Unit = {
      if (side == "right") side = "left"
      else side = "right"
    }

    def pass(other: Friend): Unit = {
      while (this.side == other.side) {
        println(s"$this: Oh, but please, $other, feel free to pass...")
        switchSide()
        Thread.sleep(1000)
      }
    }
  }

  val sam = Friend("Sam")
  val pierre = Friend("Pierre")

  //  new Thread(() => sam.bow(pierre)).start() // sam's lock,    |  then attempts to start pierre's lock... however because both thread start at roughly the same moment
  //  new Thread(() => pierre.bow(sam)).start() //  pierre's locked ad well  |  then attempts to start sam's lock (which is already locked so we have a deadlock, nothing happens)

  // 3 - livelock: oh after you, ad nauseum


  new Thread(() => sam.pass(pierre)).start()
  new Thread(() => pierre.pass(sam)).start()












}


