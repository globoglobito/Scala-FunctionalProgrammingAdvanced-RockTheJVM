package lectures.part2afp

object LazyEvaluation extends App {

  // lazy DELAYS the evaluation of values
  lazy val x: Int ={
    println("hello")
    45
  }

  println(x) // will evaluate everything, thus printing the hello and 42
  println(x) // 2nd time will no longer evaluate all, just return the final value

  // examples of implications:

  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }
  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition

  println(if (simpleCondition && lazyCondition) "yes" else "no") // remember call by name vs call by value??


  // in conjunction with call by name

  def byNameMethod(n: => Int): Int = {
    // CALL BY NEED
    lazy val t = n
    t + t + t + 1
  }

  def retrieveMagicValue = {
    // Basically if you the function was not "call by need" this println would appear 3 times because of the waiting time (42 isnt available immediately)
    println("waiting")
    Thread.sleep(3000)
    42
  }

  println(byNameMethod(retrieveMagicValue))

  // filtering with lazy vals

  def lessthat30(i:Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }
  def greaterThan20(i:Int): Boolean = {
    println(s"$i is greater than 30?")
    i >20
  }


  val numbers = List(1,24,40,5,33)
  val lt30 = numbers.filter(lessthat30)
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  // compare this with:

  val lt30lazy = numbers.withFilter(lessthat30)
  val gt20lazy = lt30lazy.withFilter(greaterThan20)
  println
  println(gt20lazy) // not what you expected!
  gt20lazy.foreach(println) // now compare this with gt20

  // for-comprehension with guards Use withFilter, not filter

  for {
    a <- List(1, 2, 3) if a % 2 == 0 // uses lazy vals
  } yield a + 1

  //same as ...
  List(1,2,3).withFilter(_ % 2 == 0).map(_ + 1) // list[Int]



  // implement a lazily evaluated singly linked Stream of element (it is implemented in StreamsPlayground in Exercises


  abstract class MyStream[+A] {
    def isEmpty: Boolean
    def head: A
    def tail: MyStream[A]

    def #::[B >: A](element: B): MyStream[B] // rightassociative preppend operator ... because covariant B supertype of A ">:"
    def ++[B >: A](anotherStream: MyStream[B]): MyStream[B] // concatenation covariant B supertype of A ">:"

    def foreach(f:A => Unit): Unit
    def map[B](f: A => B): MyStream[B]
    def flatMap[B](f: A => MyStream[B]): MyStream[B]
    def filter(predicate: A => Boolean): MyStream[A]

    def take(n: Int): MyStream[A] // takes the first n elements out of this stream

  }

  object MyStream {
    def from[A](start: A)(generator: A => A) : MyStream[A] = ???
  }








}
