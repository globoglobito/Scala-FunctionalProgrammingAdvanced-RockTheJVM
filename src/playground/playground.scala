package playground

object playground extends App {

  //println(1 +: List(2, 3) :+ 4)
//
  //println(1 :: 2 :: List(2, 3) )
//


  def example(x: Int => Int): Int = x(2)
  val helper = (x: Int) => x * 2
  def example2(x: => Int): Int = {
    println(5)
    x + 2
  }
  def example3(x: => Int => Int): Int = {
    println(33)
    x(2)
  }

  lazy val test = example(helper)
  lazy val test2 = example2(5)
  lazy val test3 = example3(helper)

  //println(test(2))
 if (test < 5 ) {
   println(test2)
   if (test < 3) {
     println(test3)}
 }


  println("###################################")
  println(test2)
  println(test3)



}
