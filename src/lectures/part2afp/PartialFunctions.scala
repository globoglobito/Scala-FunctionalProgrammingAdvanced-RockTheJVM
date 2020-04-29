package lectures.part2afp

object PartialFunctions extends App {

  val aFunction = (x: Int) => x + 1 // Function1[Int, Int] === Int => Tnt

  // But what if i only want to accept a subset of the domain of int, maybe only accept {1,2,5}

  //clunky but perfectly valid
  val aFussyFunction = (x: Int) =>
    if (x ==1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException

  //Using Pattern matching
  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  // this nicer function is a type of partial function because it accepts only a part of the Int domain {1,2,5} as argument
  //Now lets make it shorter and sweeter.

  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  } // partial function value (the one above is not a partial function)



  println(aPartialFunction(2)) // remember this is the apply method for this instance of function1
  //println(aFussyFunction(233)) will crash with Match Error


  //PF Utilities
  println(aPartialFunction.isDefinedAt(56)) // Used to show if the value is defined for the function (instead of blowing with an exception)

  // list
  val lifted = aPartialFunction.lift // transforms the partial function into one that returns Option[Int]
  println(lifted(2))
  println(lifted(233)) // wont explode!

  val pfChain = aPartialFunction.orElse[Int,Int] {
    case 45 => 67
  } // extends the original function (in a new one) for more matches

  //PF extend normal functions
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // HOF accept partial functions as well
  val aMappedList = List(1,2,3).map {
    case 1 => 45
    case 2 => 78
    case 3=> 1000
  }
  println(aMappedList) // if you changed 3 to 5 then you get match error

  /*
  Note: PF can only have ONE parameter type
   */

  //EX define your own PF

  val aManualFussyFunction = new PartialFunction[Int, Int] {
    override def apply(x: Int): Int = x match {
      case 1=> 42
      case 2 => 65
      case 5 => 999
    }

    override def isDefinedAt(x: Int): Boolean =
      x ==1 | x== 2 | x == 3

  } // again this is a partial function because it only encompasses 3 ints out of the entirety of Int

  // a simple chatbot

  val chatbot: PartialFunction[String, String] = {
    case "hello" => "Hello!"
    case "goodbye" => "Why must you forsake me"
    case "rematch" => "gg no re"
    case _ =>  "I'm afraid I cannot do this" // only because i dont want this to return an exception
  }

  //scala.io.Source.stdin.getLines().foreach(line => println("chatbot says:" + chatbot(line))) // allows you to write into the console and get a response
  scala.io.Source.stdin.getLines().map(chatbot).foreach(println) // another way


}
