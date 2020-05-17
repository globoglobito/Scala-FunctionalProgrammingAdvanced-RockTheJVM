package part5ts

object PathDependentTypes extends App {


  class Outer {
    class Inner
    object InnerObject
    type InnerType

    def print(i: Inner) = println(i)
    def printGeneral(i: Outer#Inner) = println(i)
  }

  def aMethod: Int = {
    class HelperClass // you can define calsses and objects everywhere... but not types!, they gotta be alias of something
    type HelperType = String // like this! it is an alias
    2
  }

  // per-instance
  val o = new Outer
  val inner = new o.Inner // o.Inner is a TYPE now

  val oo = new Outer
  //  val otherInner: oo.Inner = new o.Inner REMEMBER  oo.Inner and o.inner are different types

  o.print(inner)
  //  oo.print(inner)

  // path-dependent types

  // Outer#Inner, this is the supertype of all inner types
  o.printGeneral(inner)
  oo.printGeneral(inner)
  // this works because the method printGeneral makes reference to the supertype

  /*
    Exercise
    DB keyed by Int or String, but maybe others
   */

  /* HINT
    use path-dependent types
    abstract type members and/or type aliases
   */

  trait ItemLike {
    type Key // solution 1
  }

  trait Item[K] extends ItemLike { // now Item extends from our abstract type
    type Key = K
  }

  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ??? // forcing our itemType to be upper bounded

  get[IntItem](42) // ok
  get[StringItem]("home") // ok

  // get[IntItem]("scala") // not ok, we want to prevent this
}