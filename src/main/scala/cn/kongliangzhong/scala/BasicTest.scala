package cn.kongliangzhong.scala

class BasicTest {
  def test {
    utilTest
  }

  private def utilTest {
    for(i <- 0 until 10 by 2)
      println(i + "")
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


object FileMatcher {
  private val filesHere = (new java.io.File(".")).listFiles

  def filesEnding(query: String) =
    for(file <- filesHere; if(file.getName.endsWith(query)))
      yield file.getName

  def filesContaining(query: String) =
    for(file <- filesHere; if(file.getName.contains(query)))
      yield file.getName

  def filesRegex(query: String) =
    for(file <- filesHere; if(file.getName.matches(query)))
      yield file.getName

  def filesMatching(matcher: String => Boolean) =
    for(file <- filesHere; if(matcher(file.getName)))
      yield file.getName

  def filesEnding2(query: String) =
    filesMatching(_.endsWith(query))

  def filesContaining2(query: String) =
    filesMatching(_.contains(query))
}
