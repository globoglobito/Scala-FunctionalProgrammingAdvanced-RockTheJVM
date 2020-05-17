package part5ts

object Variance extends App {

  trait Animal

  class Dog extends Animal

  class Cat extends Animal

  class Crocodile extends Animal

  //what is variance?
  // "inheritance" type substitution of generics


  class Cage[T] // should a caged cat inherit from caged animal?
  // yes - covariance
  class CCage[+T]

  val ccage: CCage[Animal] = new CCage[Cat] // makes sense because we are replacing a general cage of animals with a specific cage of Cats
  // no - invariance
  class ICage[T] // cannot replace type of Cage with other Types
  //val icage: ICage[Animal] = new ICage[Cat]    THIS DOESNT WORK
  // val x: Int = "hello"  // SAME AS DOING THIS

  //hell no aka 'opposite' = contraVariance
  class XCage[-T]

  val xcage: XCage[Cat] = new XCage[Animal] // why would you do this?? replacing a specific a specific type for a general type

  class InvariantCage[T](val animal: T) // invariant

  //covariant positions
  class CovariantCage[+T](val animal: T) // covariant position

  //class ContravariantCage[-T](val animal:T)  // wont compile

  /*
  if it would compile, it would mean this is possible
  val catCage: XCage[Cat] = new XCage[Animal](new Crocodile) hence this is a problem
   */

  // class CovariantVariableCage[+T](var animal: T) // wont compile .. types of vars are in CONTRAVARIANT position

  /*
  val ccage: CCage[Animal] = new CCage[Cat](new Cat)
  ccage.animal = newCrocodile
   */

  // class ContravariantVariableCage[-T](var animal: T) // ALSO in COVARIANT POSITION
  /*
  if it would compile, it would mean this is possible
  val catCage: XCage[Cat] = new XCage[Animal](new Crocodile) hence this is a problem
   */

  class inVariantVariableCage[T](val animal: T) // A ok

  /* trait AnotherCovariantCage[+T] {
        def addAnimal(animal: T) // METHOD ARGUMENT IS IN CONTRAVARIANT POSITION, thus WONT COMPILE
      }
     val ccage: CCage[Animal] = new CCage[Dog]
     val ccage.add(new Cat) /// which would be wrong

   */

  class AnotherContravariantCage[-T] {
    def addAnimal(animal: T) = true
  } // Aok
  val acc: AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
  acc.addAnimal(new Cat)
  class Kitty extends Cat
  acc.addAnimal(new Kitty)

  class MyList[+A] {
    def add[B >: A](element: B): MyList[B] = new MyList[B] // widening the type with SUPER TYPE "B is supertype of A '>:'"
  }

  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)
  val moreAnimals = animals.add(new Cat)
  val evenMoreAnimals = moreAnimals.add(new Dog) // we want to keep the property that all the elements have a common type, thus this list is now Animal

  // METHOD ARGUMENT IS IN CONTRAVARIANT POSITION

  // return types
  class PetShop[-T] {
    //    def get(isItaPuppy: Boolean): T // METHOD RETURN TYPES ARE IN COVARIANT POSITION
    /*
      val catShop = new PetShop[Animal] {
        def get(isItaPuppy: Boolean): Animal = new Cat
      }
      val dogShop: PetShop[Dog] = catShop
      dogShop.get(true)   // give back an EVIL CAT not a puppy!
     */

    def get[S <: T](isItaPuppy: Boolean, defaultAnimal: S): S = defaultAnimal // solution, using SUBTYPES <:
  }

  val shop: PetShop[Dog] = new PetShop[Animal]
  //  val evilCat = shop.get(true, new Cat) // cat does not extend dog, and thus it is ILLEGAL
  class TerraNova extends Dog
  val bigFurry = shop.get(true, new TerraNova)

  /*
    Big rule
    - method arguments are in CONTRAVARIANT position
    - return types are in COVARIANT position
   */

  /** EX
   * 1. Invariant, covariant, contravariant
   *   Parking[T](things: List[T]) {
   *     park(vehicle: T)
   *     impound(vehicles: List[T])
   *     checkVehicles(conditions: String): List[T]
   *   } DON'T WORRY ABOUT IMPLEMENTATION
   *
   * 2. used someone else's API: IList[T]
   * 3. Parking = monad!
   *     - flatMap
   */
  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle
  class IList[T]

  class IParking[T](vehicles: List[T]) { //invariant parking
    def park(vehicle: T): IParking[T] = ???
    def impound(vehicles: List[T]): IParking[T] = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => IParking[S]): IParking[S] = ???
  } // only allow one type and only one

  class CParking[+T](vehicles: List[T]) { //covariant parking
    def park[S >: T](vehicle: S): CParking[S] = ??? // we use supertyping :> aka widening our types
    def impound[S >: T](vehicles: List[S]): CParking[S] = ??? // again widening, but i eality a bit weird, if our parking only can impound cars and bikes, no point in widening
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => CParking[S]): CParking[S] = ???
  }

  class XParking[-T](vehicles: List[T]) { // contravariant
    def park(vehicle: T): XParking[T] = ???
    def impound(vehicles: List[T]): XParking[T] = ???
    def checkVehicles[S <: T](conditions: String): List[S] = ???

    def flatMap[R <: T, S](f: Function1[R, XParking[S]]): XParking[S] = ??? // only different flatmap, "T => XParking[S}" is in a  covariant position, thus we add R, which is part of T for it to compile
  }

  /*
    Rule of thumb
    - use covariance = COLLECTION OF THINGS, like a collection of vehicles
    - use contravariance = GROUP OF ACTIONS you want to perform on your types
   */
  // PART 2
  class CParking2[+T](vehicles: IList[T]) {
    def park[S >: T](vehicle: S): CParking2[S] = ???
    def impound[S >: T](vehicles: IList[S]): CParking2[S] = ??? // we remain with S and supertyping, because whilst Parking is covariant iList is invariant!
    def checkVehicles[S >: T](conditions: String): IList[S] = ???
  }

  class XParking2[-T](vehicles: IList[T]) {
    def park(vehicle: T): XParking2[T] = ???
    def impound[S <: T](vehicles: IList[S]): XParking2[S] = ???
    def checkVehicles[S <: T](conditions: String): IList[S] = ??? // we leave the type restriction for similar reasoning as the supertyping
  }

  // flatMap


}
