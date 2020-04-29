package lectures.part1as

object AdvancedPatternMatching extends App {

val numbers = List(1)
val description = numbers match {
  case head :: Nil => println(s"the only element is $head")
  case _ => "whatever"
}

  /*Pattern matching works with:
    constants
    wildcards
    case classes
    tuples
    some special magic
   */


  // the 1%, what if you cant use a case class?

  class Person(val  name: String, val age: Int)

  object Person{
    def unapply(person: Person): Option[(String, Int)] = Some((person.name, person.age))

    def unapply(age: Int): Option[String] = Some(if (age <21) "minor" else "major") // you cna overload the unapply
  }

  val bob = new Person( "Bob", 25)
  val greeting = bob match {
    case Person(n,a) => s"My name is $n and my age is $a"
  }

  println(greeting)

  val LegalStatus = bob.age match {
    case Person(status)  => s"My legal status is $status"
  }
  println(LegalStatus)
  /* Not case class, so what gives?
   Whats important here is the Singleton Object, not the class (Rename the object, keep the class as is and rename the calls and it will still work)
   For this custom pattern to work you need the singleton object, an unapply (making this an extractor method) that returns an Option (tho more stuff can be used)  ,
    */

  /* EX: Transform the following into a nicer version with custom pattern matching
   */
  val n: Int = 7
  // this
  val mathProperty = n match {
    case x if x <10 => "single digit"
    case x if x % 2 == 0 => "an even number"
    case _ => "no property"
  }

  object even {
    def unapply(arg: Int): Option[Boolean] = if (arg %2 ==0) Some(true) else None // verbose way
  }
  object singleDigit {
    def unapply(arg: Int): Boolean = arg > -10 && arg < 10 // simpler way
  }

  val nicerMathProperty = n match {
    case singleDigit() => "single digit" // no need for the _
    case even(_) => "an even number" // requires _ or x or whatever
    case _ => "no property"
  }

  println(nicerMathProperty)

// infix patterns
  case class Or[A,B](a:A, b:B)
  val either = Or(2,"two")
  val humanDescription = either match {
    case number Or string => s"$number is written as $string" // custom infix pattern Or
      //note this infix patterns only work with 2 items.
  }
  println(humanDescription)


  // decomposing sequences
  val vararg = numbers match {
    case List(1,_*) => "starting with 1" // How can you apply this _* to your own custom sequences?
  }

  abstract class MyList[+A] {
    def head: A = ???
    def tail: MyList[A] = ???
  }
  case object Empty extends MyList[Nothing]
  case class Cons[+A](override  val head: A, override val tail: MyList[A]) extends MyList[A]
  // that's your custom sequence

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }
  // that singleton object allows you  that functionality

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3,Empty)))
  val decomposed = myList match {
    case MyList(1,2,_*) => "starts with 1 and 2"
    case _ => "something else"
  }

  println(decomposed)


  // custom return types for unapply (very very very rare)
  // isEmpty: Boolean, get: something

  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      def isEmpty = false
      def get = person.name
    }
  }

  println(bob match {
    case PersonWrapper(n) => s"This person name is $n"
    case _ => "Weh"
  })




}
