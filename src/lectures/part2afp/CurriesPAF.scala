package lectures.part2afp

object CurriesPAF extends App{

  // curried functions
  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3 = superAdder(3)
  println(add3(5))
  println(superAdder(3)(5))

  // METHOD
  def curriedAdder(x: Int)(y: Int) : Int = x + y //curried method

  val add4: Int => Int = curriedAdder(4) // transforming a method into a function vale of type Tnt -> Int
  // this is called lifting = ETA-Expansion (functions out of methods)
  // functions != methods due to JVM limitation

  //you cant use methods in FP unless they are transformed into function values

  def inc(x: Int): Int = x + 1
  List(1,2,3).map(inc) // ETA conversion into -> List(1,2,3).map(x => inc(x))

  // partial function application
  val add5 = curriedAdder(5) _ // Forces ETA conversion Int => Int

  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int) = x + y
  def curriedAddMethod(x: Int)(y: Int) = x + y

  //add7: Int => Int = y => 7 + y

  val add7 = curriedAddMethod(7) _
  val add7_2 = (y: Int) => simpleAddFunction(7,y) // and derivatives
  val add7_3 = (y: Int) => simpleAddFunction.curried(7)
  val add7_4 = curriedAddMethod(7)(_) // PAF = alternative syntax
  val add7_5 = simpleAddMethod(7,_:Int) // Alternative syntax for turning methods into function values
                                        // y => simpleAddMethod(7,y)
  val add7_6 = (y: Int) => simpleAddFunction(7, _: Int)


  // underscores are powerful ... they force ETA-expansion
  def concatenator(a: String, b: String, c: String) = a + b + c
  val insertName = concatenator("Hello, I am ", _: String," , how are you?" ) // x: String => concatenator(hello, x, how are you)
  println(insertName("Bruno"))

  val fillInTheBlanks = concatenator("hello! ", _: String, _: String) //(x,y) => concatenator("hello! ", x, y )
  println(fillInTheBlanks("i Am ", "Something"))


  /* EX: Process a list of numbers and return their string representations with different formats.
         Use the %4.2f, %8.6f and %14.12f with a curried formatter function
   */

  def curriedFormatter(s: String)(number: Double): String = s.format(number)
  val numbers = List(Math.PI, Math.E, 1, 9.6, 1.3e-12)

  val format1 = curriedFormatter("%4.2f") _ // _ for lifting
  val format2 = curriedFormatter("%8.6f") _ // _ for lifting
  val format3 = curriedFormatter("%14.12f") _ // _ for lifting

  println(numbers.map(format1))
  println(numbers.map(curriedFormatter("%4.2f"))) // no need for the _ the compiler does it for us. Because it knows the curriedFormatter is a curried method


  /*
         Difference between
         - functions vs methods
         - parameters: by name vs o-lambda
   */

  def byName(n: => Int) = n + 1
  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42
  def parenMethod(): Int = 42

  byName(3) //ok
  byName(method) //ok
  byName(parenMethod()) //ok
  byName(parenMethod) //ok but beware it is actually calling parenMethod()
  //byName(() => 42) // Doesn't work because by name argument of value type is NOT the same as a function argument
  byName((() => 42)()) // this works because you are supplying a function (the lambda) but you are also calling it, turning the expression into a value!
  // byName(parenMethod _) // won't work because this is trying to covnert into a function (ETA) and again a function IS NOT a value type parameter

  //byFunction(42) not ok... not a function
  //byFunction(method) not ok... the compiler can't do ETA expansion on a  parenthesisless method (accesor methods)
  byFunction(parenMethod) // here it works, compiler does ETA
  byFunction(() => 46) // obs works its a function value
  byFunction(parenMethod _) // also works, but unnecesary



  // by name parameter doesn't actually call the function, its calls the method, so just => doesn't mean its gonna call the function





}
