package part5ts

object StructuralTypes extends App {


  // structural types

  type JavaCloseable = java.io.Closeable  // type alias

  class HipsterCloseable {
    def close(): Unit = println("yeah yeah I'm closing")
    def closeSilently(): Unit = println("not making a sound")
  }

  //  def closeQuietly(closeable: JavaCloseable OR HipsterCloseable) // ?! We want to do something like this

  type UnifiedCloseable = {
    def close(): Unit
  } // STRUCTURAL TYPE and this allow use both implementations

  def closeQuietly(unifiedCloseable: UnifiedCloseable): Unit = unifiedCloseable.close()

  closeQuietly(new JavaCloseable {
    override def close(): Unit = ???
  })
  closeQuietly(new HipsterCloseable)

  // as you can see we can use both implementations



  // TYPE REFINEMENTS


  type AdvancedCloseable = JavaCloseable {
    def closeSilently(): Unit
  } //  a refined type

  class AdvancedJavaCloseable extends JavaCloseable {
    override def close(): Unit = println("Java closes")
    def closeSilently(): Unit = println("Java closes silently")
  }

  def closeShh(advCloseable: AdvancedCloseable): Unit = advCloseable.closeSilently()

  closeShh(new AdvancedJavaCloseable) // AdvancedJavaCloseable originates from JavaCloseable and also has the closeSilently method, which our refined type has
  // closeShh(new HipsterCloseable) // even if we add the closeSilently method to our HipsterCloseable, it wont work because it doesn't originate from JavaCloseable

  // using structural types as standalone types
  def altClose(closeable: { def close(): Unit }): Unit = closeable.close() // { def close(): Unit } is its own type


  // type-checking => duck typing

  type SoundMaker = {
    def makeSound(): Unit
  }

  class Dog {
    def makeSound(): Unit = println("bark!")
  }

  class Car {
    def makeSound(): Unit = println("vrooom!")
  }

  val dog: SoundMaker = new Dog
  val car: SoundMaker = new Car
  // compiler is fine with this 2 vals because so long as the types of the right hand side conform to the structure defined on the type of the left hand side
  // this is called static duck typing.. like in python!

  // CAVEAT: based on reflection, big IMPACT ON PERFORMANCE

  /*
    Exercises
   */

  // 1.
  trait CBL[+T]  {
    def head: T
    def tail: CBL[T]
  }

  class Human {
    def head: Brain = new Brain
  }

  class Brain {
    override def toString: String = "BRAINZ!"
  }

  def f[T](somethingWithAHead: { def head: T }): Unit = println(somethingWithAHead.head)

  /*
    f is compatible with a CBL and with a Human? Yes, f is compatible with both.
   */
 //EX
  case object CBNil extends CBL[Nothing] {
    def head: Nothing = ???
    def tail: CBL[Nothing] = ???
  }
  case class CBCons[T](override val head: T, override val tail: CBL[T]) extends CBL[T]

  f(CBCons(2, CBNil))
  f(new  Human) // ?! T = Brain !!



  // 2.
  object HeadEqualizer {
    type Headable[T] = { def head: T }

    def ===[T](a: Headable[T], b: Headable[T]): Boolean = a.head == b.head
  }

  /*
    is === compatible with a CBL and with a Human? Yes.
   */
  //EX
  val brainzList = CBCons(new Brain, CBNil)
  val stringsList = CBCons("Brainz", CBNil) //problem, a type unsafe list

  HeadEqualizer.===(brainzList, new Human)
  // but it poses a problem:
  HeadEqualizer.===(new Human, stringsList) // not type safe


}
