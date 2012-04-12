package cn.kongliangzhong.scala

abstract class CurrencyZone {
  type Currency <: AbstractCurrency
  def make(x: Long): Currency
  abstract class AbstractCurrency {
    val amount: Long
    def designation: String
    override def toString = amount + " " + designation
    def + (that: Currency): Currency =
      make(this.amount + that.amount)
    def * (x: Double): Currency =
      make((this.amount * x).toLong)

    private def decimals(n: Long): Int =
      if(n == 1) 0 else 1 + decimals(n / 10)
  }

  val CurrencyUnit:Currency
}
