package part5ts

object RockingInheritance extends App {


  // convenience
  trait Writer[T] {
    def write(value: T): Unit
  }
  trait Closeable {
    def close(status: Int): Unit
  }

  trait GenericStream[T] {
    //some more methods
    def foreach(f: T => Unit): Unit
  }

  def processStream[T](stream: GenericStream[T] with Writer[T] with Closeable): Unit ={ // genericStream with ... its its own type and has access to all the API.
    stream.foreach(println)
    stream.close(0)
  }
  // Convenience: Whenever we don't know who exactly mixes in specific traits we can use them all ina  specific type in a  parameter of a method

  // diamond problem

  trait Animal {def name: String}
  trait Lion extends Animal {override def name: String = "lion"}
  trait Tiger extends Animal {override def name: String = "tiger"}
  class Mutant extends Lion with Tiger

  val m = new Mutant
  println(m.name) // tiger.... why?

  /*
  Mutant
  extends Animal with {override def name: String = "lion"} // so far name has ben overwritten to now be lion
  with {override def name: String = "tiger"} // animal has already been evaluated once, so compiler goes directly to the other trait, and overwrites again

  LAST OVERWRITE GETS SELECTED   (that's how Scala solves the diamond problem)
   */


  /* The super problem + type linearization
   (super accesses the member or method of a parent class or trait)
   */

  trait Cold {
    def print = println("cold")
  }

  trait Green extends Cold {
    override def print: Unit = {
      println("green")
      super.print
    }
  }

  trait Blue extends Cold {
    override def print: Unit = {
      println("blue")
      super.print
    }
  }

  class Red {
    def print = println("red")
  }

  class White extends Red with Green with Blue {
    override def print: Unit = {
      println("white")
      super.print // so what print this references? Red, Cold?
    }

    val color = new White
    color.print

    /* TYPE LINEARIZATION HAPPENS
    White = AnyRef with <Red> with <Cold> with <Green> with <Blue> with <White>
    super calls the left type
    SO,
    println(white)
    then super print (of blue) -> println(blue)
    then super print (of green) -> println(green)
    then super print (of cold) --> println(cold)

    RED IS NEVER CALLED

     */





  }





}
