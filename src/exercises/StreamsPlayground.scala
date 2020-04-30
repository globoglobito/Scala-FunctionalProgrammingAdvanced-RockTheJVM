package exercises


/* remember there is a difference between call by name and currying/ Generic type implementation

Call by name : def example(x: => Int) // wont be evaluated unless called
Currying: def example(X: Int => Int) // takes a function that returns an int as a parameter
Currying with Call By name: def example(x: => Int => Int)

 */




  abstract class MyStream[+A] {
    def isEmpty: Boolean

    def head: A

    def tail: MyStream[A]

    def #::[B >: A](element: B): MyStream[B] // rightassociative preppend operator ... because covariant B supertype of A ">:"
    def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] // concatenation covariant B supertype of A ">:"

    def foreach(f: A => Unit): Unit

    def map[B](f: A => B): MyStream[B]

    def flatMap[B](f: A => MyStream[B]): MyStream[B]

    def filter(predicate: A => Boolean): MyStream[A]

    def take(n: Int): MyStream[A] // takes the first n elements out of this stream

    def takeAsList(n: Int): List[A] = take(n).toList()

    final def toList[B >: A] (acc: List[B] = Nil): List[B] =
      if (isEmpty) acc.reverse
      else tail.toList(head :: acc)

  }

  object EmptyStream extends MyStream[Nothing] {

      def isEmpty: Boolean = true

      def head: Nothing = throw new NoSuchElementException

      def tail: MyStream[Nothing] = throw new NoSuchElementException

      def #::[B >: Nothing](element: B): MyStream[B] = new Cons(element, this)

      def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

      def foreach(f: Nothing => Unit): Unit = ()

      def map[B](f: Nothing => B): MyStream[B] = this

      def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this

      def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

      def take(n: Int): MyStream[Nothing] = this

    }

  class Cons[+A](h: A, tl: => MyStream[A]) extends MyStream[A] { // notice the call by name of tl, we need it for the stream to eb lazily evaluated, otherwise it will crash
    def isEmpty: Boolean = false

    override val head: A = h
    override lazy val  tail: MyStream[A] = tl // call by need, remember not all streams need to have more than an element

    /*
    val s = new Cons(1, EmptyStream)
    val prepended = 1 #:: s ---> new Cons(1,s)
     */
    def #::[B >: A](element: B): MyStream[B] = new Cons(element, this)
    def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new Cons(head, tail ++ anotherStream)

    def foreach(f: A => Unit): Unit = {
      f(head)
      tail.foreach(f)
    }

    /*
    s = new COns(1,?)
    mapped = s.map(_+1) = new Cons(2, s.tail.map(_ + 1))
     */

    def map[B](f: A => B): MyStream[B] = new Cons(f(head), tail.map(f)) // preserves the lazy eval
    def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)
    def filter(predicate: A => Boolean): MyStream[A] =
      if(predicate(head)) new Cons(head, tail.filter(predicate))
      else tail.filter(predicate)

    def take(n: Int): MyStream[A] =
      if(n <= 0 ) EmptyStream
      else if (n == 1) new Cons(head, EmptyStream)
      else new Cons(head, tail.take(n-1))
  }


    object MyStream {
      def from[A](start: A)(generator: A => A): MyStream[A] =
        new Cons(start, MyStream.from(generator(start))(generator))
    }

object StreamsPlayground extends App {
  val naturals = MyStream.from(1)( _ + 1)
  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)

  val startFrom0 = 0 #:: naturals // naturals.#::(0)

  println(startFrom0.head)

  startFrom0.take(23).foreach(println)

  //map, flatmap

  println(startFrom0.map(_ * 2).take(100).toList())

  println(startFrom0.flatMap(x => new Cons(x, new Cons(x + 1, EmptyStream))).take(10).toList())

  println(startFrom0.filter(_< 10).take(10).toList()) // if take 11, then stack overflow

  def fibonacci(first: BigInt, second: BigInt): MyStream[BigInt] =
    new Cons(first, fibonacci(second, first + second))

  println(fibonacci(1,1).take(100).toList())

  def eratosthenes(numbers: MyStream[Int]): MyStream[Int] =
    if (numbers.isEmpty) numbers
    else new Cons(numbers.head, eratosthenes((numbers.tail.filter(_ % numbers.head != 0))))





}




