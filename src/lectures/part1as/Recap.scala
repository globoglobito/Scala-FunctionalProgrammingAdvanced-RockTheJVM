package lectures.part1as
import scala.annotation.tailrec
import scala.text.DocGroup
object Recap extends App {


  val aCondition:Boolean = false
  val aConditionedVal = if(aCondition) 42 else 65

  //compiler infers type for us and the value of a code block is the last line.
  val aCodeClock = {
    if(aCondition) 54
    56
  }

  // Unit = void
  val theUnit = println("hello, scala")

  //functions
  def aFunction(x: Int): Int = x + 5

  //recursion: stack and tail.
 @tailrec def factorial(n: Int, accum: Int):Int = {
   if(n<= 0) accum
   else factorial(n-1, n*accum)
 }

  // Object-oriented programming

  class Animal
  class Dog extends Animal
  val aDog: Animal = new Dog //subtyping polymorphism

  trait Carnivore { // remember traits are like abstract classes that cannot be instantiated
    def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("crunch!")
     }

  // method notations

  val aCroc = new Crocodile
  aCroc.eat(aDog)
  aCroc eat aDog // like natural language

  // anonymous classes
  val aCarnivore = new Carnivore {
    override def eat(a: Animal): Unit = println("( ͡° ͜ʖ ͡°)")
  } // this is the same as creating a new class from extending from trait carnivore

  // generics
  abstract class MyList[+A] // variance and variance problems will be seen in this course

  object Mylist // singleton companion object

  //case class: classes with more boilerplate (having apply method, all params being fields by default, etc)
  case class Person(name:String, age: Int)

  // Exceptions and try/catch/finally

  val throwsException = throw new RuntimeException
  val aPotentialFailure = try {
    throw new RuntimeException
  } catch {
    case e: Exception => "I caught an exception"
  } finally {
    println("some logs") // will always print this as its under finally
  }


  // Functional Programming

  val incrementer = new Function[Int, Int] { // function type that takes an int and returns an int
    override def apply(v1: Int): Int = v1 + 1
  }

  incrementer(1)

  // Pimper version
  val anonymousIncrementer = (x:Int) => x+ 1
  List(1,2,3).map(anonymousIncrementer) //HOF

  //for-comprehension
  val pairs = for {
    num <- List(1,2,3) // you can add an If guard, that would act as  a filter
    char <- List('a','b','c')
  } yield num + "-" + char

  // Scala collections: Seqs, Arrays, List, Vectors, Maps, Tuples
  val aMap = Map(
    "Daniel" -> 789,
    "Jess" -> 555
  )

  // collections: Options , Try
  val anOption = Some(2)

  // pattern matching... like a switch

  val x = 2
  val order = x match{
    case 1 => "first"
    case 2 => "Second"
    case _ => "looser"
  }

  val bob = Person("Bob", 22)
  val greeting = bob match {
    case Person(n,_) => s"Hi, my name is $n"
  }




}
