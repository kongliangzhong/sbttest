package cn.kongliangzhong.scala

import scala.io.Source

object LongLines {

  def processFile(fileName: String, length: Int) = {
    // def processLine(line: String) = {
    //   if(line.length > length)
    //     println(fileName + ": " + line.trim)
  //}
    val processLine = (line: String) => {
      if(line.length > length)
        println(fileName + ": " + line.trim)
    }

    Source.fromFile(fileName).getLines.foreach(processLine)
  }

  def echoTest = {
    def echo(args: String*) = args.foreach(println)
    val arr = Array("string", "array", "as", "String*")
    echo(arr: _*)
  }

}

object FuncTest {
  def boom(x: Int): Int =
    if(x == 0) throw new Exception("boom")
    else boom(x-1)

  def curryTest = {
    def curriedSum(x: Int)(y: Int) = x + y

    println("curriedSum: " + curriedSum(1)(2))
    val addOne = curriedSum(1) _
    println("addOne: " + addOne(2))

    def first(x: Int) = (y: Int) => x + y
    val addTwo = first(2)
    println("addTwo: " + addTwo(2))
  }

  def byNameParam(cond: => Boolean) {
    if(cond) println("cond: " + true)
    else println("false")
  }

  def andThenTest{
    def func1(): String = {
      val str = "str generated in func1"
      println("str: " + str)
      str
    }

    def func2(str: Char): Int = {
      println("print str in func2: " + str)
      33
    }

    def func3(n: Int) = {
      println("n: " + n)
    }

    val func = func1 andThen func2 andThen func3
  }

}
