package part5ts

object FBoundedPolymorphism extends App {

  //  trait Animal {
  //    def breed: List[Animal]
  //  }
  //
  //  class Cat extends Animal {
  //    override def breed: List[Animal] = ??? // List[Cat] and not animal!!
  //  }
  //
  //  class Dog extends Animal {
  //    override def breed: List[Animal] = ??? // List[Dog] and not animal!!
  //  } How do we force the compiler to do this

  // Solution 1 - naive

  //  trait Animal {
  //    def breed: List[Animal]
  //  }
  //
  //  class Cat extends Animal {
  //    override def breed: List[Cat] = ??? // List[Cat] !! So by doing our due diligence we ensure list of cats
  //  }
  //
  //  class Dog extends Animal {
  //    override def breed: List[Cat] = ??? // List[Dog] !! But this is naive, i can make mistakes and still be valid like here
  //  }


  // Solution 2 - FBP

  //  trait Animal[A <: Animal[A]] { // recursive type: F-Bounded Polymorphism --> forces the return type to be of the desired type.
  //    def breed: List[Animal[A]]
  //  }
  //
  //  class Cat extends Animal[Cat] {
  //    override def breed: List[Animal[Cat]] = ??? // List[Cat] !!
  //  }
  //
  //  class Dog extends Animal[Dog] {
  //    override def breed: List[Animal[Dog]] = ??? // List[Dog] !!
  //  }
  //
  //  trait Entity[E <: Entity[E]] // ORM
  //  class Person extends Comparable[Person] { // FBP
  //    override def compareTo(o: Person): Int = ???
  //  }
  //
  // But this has limitations (i make a mistake):
  //  class Crocodile extends Animal[Dog] {
  //    override def breed: List[Animal[Dog]] = ??? // List[Dog] !!
  //  } // this compiles.... but is wrong

  // Solution 3 - FBP + self-types: This forces us to not make mistakes??

  //  trait Animal[A <: Animal[A]] { self: A =>
  //      def breed: List[Animal[A]]
  //    }
  //
  //    class Cat extends Animal[Cat] {
  //      override def breed: List[Animal[Cat]] = ??? // List[Cat] !!
  //    }
  //
  //    class Dog extends Animal[Dog] {
  //      override def breed: List[Animal[Dog]] = ??? // List[Dog] !!
  //    }
  //  class Crocodile extends Animal[Dog] {
  //    override def breed: List[Animal[Dog]] = ??? // List[Dog] !!
  //  }

  //  // Limitations! One we reach class hierarchy more than one level, FBP cease being effective
  //  trait Fish extends Animal[Fish]
  //  class Shark extends Fish {
  //    override def breed: List[Animal[Fish]] = List(new Cod) // wrong
  //  }
  //
  //  class Cod extends Fish {
  //    override def breed: List[Animal[Fish]] = ???
  //  }

  // Exercise: think about a way to enforce type safety

  // Solution 4 type classes!

  //  trait Animal
  //  trait CanBreed[A] {
  //    def breed(a: A): List[A]
  //  }
  //
  //  class Dog extends Animal
  //  object Dog {
  //    implicit object DogsCanBreed extends CanBreed[Dog] {
  //      def breed(a: Dog): List[Dog] = List()
  //    }
  //  }
  //
  //  implicit class CanBreedOps[A](animal: A) {
  //    def breed(implicit canBreed: CanBreed[A]): List[A] =
  //      canBreed.breed(animal)
  //  }
  //
  //  val dog = new Dog
  //  dog.breed // List[Dog]!!
  //  /*
  //    new CanBreedOps[Dog](dog).breed(Dog.DogsCanBreed)
  //    implicit value to pass to breed: Dog.DogsCanBreed
  //   */
  //
  //  class Cat extends Animal
  //  object Cat {
  //    implicit object CatsCanBreed extends CanBreed[Dog] {
  //      def breed(a: Dog): List[Dog] = List()
  //    }
  //  }
  //
  //  val cat = new Cat
  //  cat.breed // Wont Work, because Cat does not have the right instance class (canbreedcat instance)
  // LIMITATION: a bit too complex


  // Solution #5 Simpler ver

  trait Animal[A] { // pure type classes
    def breed(a: A): List[A]
  }

  class Dog
  object Dog {
    implicit object DogAnimal extends Animal[Dog] {
      override def breed(a: Dog): List[Dog] = List()
    }
  }

  class Cat
  object Cat { // willingly making this wrong
    implicit object CatAnimal extends Animal[Dog] {
      override def breed(a: Dog): List[Dog] = List()
    }
  }

  implicit class AnimalOps[A](animal: A) {
    def breed(implicit animalTypeClassInstance: Animal[A]): List[A] =
      animalTypeClassInstance.breed(animal)
  }

  val dog = new Dog
  dog.breed

  //  val cat = new Cat
  //  cat.breed

}
