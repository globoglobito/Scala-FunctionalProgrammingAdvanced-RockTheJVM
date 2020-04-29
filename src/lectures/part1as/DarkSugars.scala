package lectures.part1as

import scala.util.Try

object DarkSugars extends App {

  // syntax sugar #1: Methods with single param
  def singleArgMethod(args: Int): String = s"$args little ducks"

  val description = singleArgMethod{
    //some code
    val f = 45
    42 + f
  }
  println(description) // you return 87

  val aTryInstance = Try{ // java's try {.....} Basically you are using the apply method of try with this argument
    throw new RuntimeException("An example")
  }

  List(1,2,3).map { x =>
    x + 1
  } // this compiles and works like saying List(1,2,3).map(x => x+1)

  // syntax sugar #2: single abstract method
  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  } // this can be improved to be less verbose

  val aBetterInstance: Action = (x:Int ) => x + 1 // see?

  //example 2: Runnables
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("hello, Scala") // how java does it
  })

  val aSweeterThread = new Thread(() => println("Sweet, Scala")) // how Scala can do it

  // Works for classes that has some methods implemented BUT have 1 METHOD that is unimplemented

  abstract class AnAbstractType {
    def implemented: Int = 55
    def f(a: Int): Unit
  }
  val anAbstractInstance: AnAbstractType = (a: Int) => println("Sweet!") //Case and Point! THIS ISN'T AN IMPLICIT BTW, just syntax sugar


  // syntax sugar #3 the :: and #:: methods are special

  val prependedList = 2:: List(2,3)
  // 2.::(List(3,4)) ??
  // In actuality more like List(3,4)::(2) after all the 2 is the head of the list

  // scalac specification: Last char decides associativity of method(whether its an a prepend or an append)
  1 :: 2:: 3 :: List(4,5)
  List(4,5).::(3).::(2).::(1) // equivalent... but much less pretty

  class MyStream[T] {
    def -->:(value: T):MyStream[T] = this //or an actual implementation
  }
  val mystream = 1 -->: 2 -->: new MyStream[Int]

  // syntax sugar # 4: multi-word methods
  class Potato(name: String) {
    def `and then this`(action: String) = println(s"$name was $action")
  }

  val yukonGold = new Potato("Yukon")
  yukonGold `and then this`("Deep fried")

  // syntax sugar #5: infix types
  class Composite[A,B]
  val composite: Int Composite String = ??? // instead of Composite[Int, String]

  class -->[A,B]
  val towards: Int --> String = ???

  // syntax sugar #6 update() is very special, much like apply()
  val anArray = Array(1,2,3)
  anArray(2) = 7 // rewritten as anArray.update(2,7)
  // used in mutable collection
  // remember apply() and update()


  // syntax sugar #7: setters for mutable containers
  class Mutable {
    private var internalMember: Int = 0 // private OO encapsulation
    def member: Int = internalMember // getter
    def member_=(value: Int): Unit = internalMember = value
  }
  val aMutableContainer = new Mutable
  aMutableContainer.member = 45 // re-written as aMutableContainer.member_=(45)
















}
