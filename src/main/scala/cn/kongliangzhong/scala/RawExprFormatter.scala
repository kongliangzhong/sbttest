package cn.kongliangzhong.scala

import scala.collection.mutable.Stack

class RawExprFormatter {

  abstract trait Expr
  case class SubExpr(contend: String, isInPatheses: Boolean) extends Expr

  def format(expr: String): String = {

    val outterDivs = findOutterDivPosition(expr)
    //if(outterDivs.size > 0)
      //(numerator, denominator)

    ""
  }

  private def splitExpr(expr: String): List[SubExpr] = {
    def crtElementFromStack(stack: Stack[Char]): SubExpr = {
      var content = ""
      while(stack.nonEmpty)
        content = content + stack.pop
      if(content.size == 0) return null
      val isInPatheses = content.startsWith("(") && content.endsWith(")")
      return SubExpr(content, isInPatheses)
    }

    var result: List[SubExpr] = Nil
    val stack: Stack[Char] = new Stack()
    var pLevel: Int = 0
    expr.foreach {
      c =>
        stack.push(c)
        if(c == '(') {
          if(pLevel == 0)
            result = result ::: List(crtElementFromStack(stack))
          pLevel += 1
        } else if(c == ')') {
          pLevel -= 1
          if(pLevel == 0)
            result = result ::: List(crtElementFromStack(stack))
        }
    }
    result.filter(_ != null)
  }

  // private def getSubexpr(expr:String): List[String] = {

  // }

  private def findOutterDivPosition(expr: String): List[Int] = {
    def isDivInParentheses(expr: String, divPos: Int): Boolean = {
      if(divPos < 0 || divPos >= expr.size) return false
      var numLP = 0
      var numRP = 0
      expr.substring(0, divPos).foreach {
        c =>
          if (c == '(') numLP += 1
          else if( c == ')') numRP += 1
      }
      return numLP != numRP
    }

    var result: List[Int] = Nil
    var pos = 0
    expr.foreach {
      c =>
        if(c == '/' && isDivInParentheses(expr, pos))
          result = result ::: List(pos)
        pos += 1
    }

    result
  }
}
