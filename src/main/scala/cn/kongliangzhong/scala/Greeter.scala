package cn.kongliangzhong.scala

object testPrefs {
  implicit val prompt = new PreferredPrompt("prompt >")
  implicit val drink = new PreferredDrink("drink")
}

object Greeter {
  def greet(name: String)(implicit prompt: PreferredPrompt, drink: PreferredDrink) {
    println("name = " + name)
    println(prompt.preference)
    println(drink.preference)
  }
}

class PreferredPrompt(val preference: String)
class PreferredDrink(val preference: String)


object ImplicitTest {
  def maxListImpParm[T](elements: List[T])(implicit orderer: T => Ordered[T]): T = {
    elements match {
      case List() => throw new IllegalArgumentException("list is empty")
      case List(x) => x
      case x :: rest =>
        val maxRest = maxListImpParm(rest)(orderer)
        if(orderer(x) > maxRest) x
        else maxRest
    }
  }

  def maxList[T <% Ordered[T]](elements: List[T]): T = {
    elements match {
      case List() => throw new IllegalArgumentException("list is empty")
      case List(x) => x
      case x :: rest =>
        val maxRest = maxListImpParm(rest)
        if(x > maxRest) x
        else maxRest
    }
  }
}
