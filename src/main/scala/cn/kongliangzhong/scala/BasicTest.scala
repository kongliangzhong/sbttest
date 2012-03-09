package cn.kongliangzhong.scala

class BasicTest {
  def test {
    tupleTest
  }

  private def tupleTest {
    println("tuple test begin --------------")
    val tuple1 = ("tuple1", 123, 1L)
    val tuple2 = "tuple2" -> 2  //another way to create a tuple.
    val tuple3 = "tuple3" -> 3 -> "ttt"
    println("tuple1 = " + tuple1)
    println(tuple1._1 + " : " + tuple1._2 + " : " + tuple1._3)
    println("tuple2 = " + tuple2)
    println(tuple2._1 + " : " + tuple2._2)
    println("tuple3 = " + tuple3)
    println(tuple3._1 + " : " + tuple3._2 )
    println("tuple test end  --------------")
  }
}
