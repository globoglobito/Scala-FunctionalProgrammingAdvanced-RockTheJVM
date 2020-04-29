package exercises

trait MySet[A] extends (A => Boolean) {


  def apply(elem: A): Boolean =
    contains(elem)

  def contains(elem: A): Boolean
  def +(elem: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(predicate: A => Boolean): MySet[A]
  def foreach(f:A => Unit): Unit

  def -(elem: A): MySet[A] // remove
  def --(anotherSet: MySet[A]): MySet[A] // difference
  def &(anotherSet: MySet[A]): MySet[A]

  def unary_! : MySet[A]

}

class EmptySet[A] extends MySet[A] {
  def contains(elem: A): Boolean = false
  def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)
  def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  def map[B](f: A => B): MySet[B] = new EmptySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]
  def filter(predicate: A => Boolean): MySet[A] = this
  def foreach(f:A => Unit): Unit = ()

  def -(elem: A): MySet[A] = this
  def --(anotherSet: MySet[A]): MySet[A] = this
  def &(anotherSet: MySet[A]): MySet[A] = this

  def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)

}


// all elements of type A which satisfy a property
// {x in A | property(x) }
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
  def contains(elem: A): Boolean = property(elem)

  // {x in A | property(x) } + element = {x in A | property(x) || x == element }
  def +(elem: A): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || x == elem)
  // {x in A | property(x) } + element = {x in A | property(x) || set contains x }
  def ++(anotherSet: MySet[A]): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || anotherSet(x))

  // because you really cannot determine map or flatmap of a property based set because we dont know if it will be finite!
  def map[B](f: A => B): MySet[B] = politelyFail
  def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail
  def foreach(f:A => Unit): Unit = politelyFail // how do you "iterate" infinity?

  def filter(predicate: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && predicate(x))


  def -(elem: A): MySet[A]  = filter(x => x != elem)
  def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)
  def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)
  def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

  def politelyFail = throw new IllegalArgumentException(" What...")


}



class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  def contains(elem: A): Boolean =
  elem == head || tail.contains(elem)

  def +(elem: A): MySet[A] = {
    if (this contains elem) this
    else new NonEmptySet[A](elem, this)
  }

  /*
  [1 2 3] ++ [4 5] =
  [ 2 3] ++ [4 5] + 1 =
  [3] ++ [4 5] + 1 + 2 =
  [] ++ [4 5] + 1 + 2 + 3 =
  [4 5] + 1 + 2 + 3 = [4 5 1 2 3]
   */

  def ++(anotherSet: MySet[A]): MySet[A] =
    tail ++ anotherSet + head

  def map[B](f: A => B): MySet[B] = (tail map f) + f(head)
  def flatMap[B](f: A => MySet[B]): MySet[B] = (tail flatMap f) ++ f(head)
  def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if (predicate(head)) filteredTail + head
    else filteredTail
  }
  def foreach(f:A => Unit): Unit = {
    f(head)
    tail foreach f
  }

  def -(elem: A): MySet[A] =
    if (head == elem) tail
    else tail - elem + head

  def --(anotherSet: MySet[A]): MySet[A] = filter(x => !anotherSet(x))
  def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet) // same as filter

  def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))

}

object MySet {

  /*
  val s = MySet(1,2,3) = buildSet(seq(1,2,3), [])
  = buildSet(seq(2,3), [] + 1)
  = buildSet(seq(3), [1] + 2)
  = buildSet(seq(), [1,2] + 3)
  = [1,2,3]
   */
  def apply[A](values: A*): MySet[A] = {
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail,acc + valSeq.head)
    buildSet(values.toSeq, new EmptySet[A])
  }
}

object  MySetPlayground extends App {
  val s = MySet(1,2,3,4)
  val e = s ++ MySet(6,7,8)
  e foreach println

  val negative = !s // all the naturals not equal to 1,2,3,4

  println(negative(3))
  println(negative(7))
}
