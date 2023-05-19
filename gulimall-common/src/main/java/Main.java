public class Main {
    public static void main(String[] args) {

    }
}

sealed class Animal permits Dog, Cat, Fish {

}

final class Dog extends Animal {

}

sealed class Cat extends Animal permits BigCat, SmallCat {

}

final class BigCat extends Cat {

}

final class SmallCat extends Cat {

}

non-sealed class Fish extends Animal {

}

class BigFish extends Fish {

}
