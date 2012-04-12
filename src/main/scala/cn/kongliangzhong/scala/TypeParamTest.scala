package cn.kongliangzhong.scala

class TypeParamTest {
  def test {
    queueTest
  }

  private def now = System.currentTimeMillis

  private def queueTest {
    val t0 = now
    val times = 10000
    // invoke append 100 times, then head 100 times, then tail 100 times:
    var slowAppendQueue = new SlowAppendQueue[String](Nil)
    (0 to times).foreach{
      i =>
        slowAppendQueue = slowAppendQueue.append("content " + i)
    }
    val t1 = now
    println("slowAppendQueue take " + (t1 - t0) + "ms to append 100 times")
    (0 to times).foreach(i => slowAppendQueue.head)
    val t2 = now
    println("slowAppendQueue take " + (t2 - t1) + "ms to invoke head 100 times")
    (0 to times).foreach(i => slowAppendQueue.tail)
    val t3 = now
    println("slowAppendQueue take " + (t3 - t2) + "ms to invoke tail 100 times")

    var slowHeadQueue = new SlowHeadQueue(List[String]())
    (0 to times).foreach(i => {slowHeadQueue = slowHeadQueue.append("content " + i)})
    val t4 = now
    println("slowHeadQueue take " + (t4 - t3) + "ms to invoke append 100 times")
    (0 to times).foreach(_ => slowHeadQueue.head)
    val t5 = now
    println("slowHeadQueue take " + (t5 - t4) + "ms to invoke head 100 times")
    (0 to times).foreach(_ => slowHeadQueue.tail)
    val t6 = now
    println("slowHeadQueue take " + (t6 - t5) + "ms to invoke tail 100 times")

    var queue = new Queue[String](Nil, Nil)
    (0 to times).foreach{
      i =>
        queue = queue.append("content " + i)
    }
    val t7 = now
    println("queue take " + (t7 - t6) + "ms to append 100 times")
    (0 to times).foreach(_ => queue.head)
    val t8 = now
    println("queue take " + (t8 - t7) + "ms to invoke head 100 times")
    (0 to times).foreach(_ => queue.tail)
    val t9 = now
    println("queue take " + (t9 - t8) + "ms to invoke tail 100 times")

    var queue2 = Queue2("")
    (0 to times).foreach{
      i =>
        queue2 = queue2.append("content " + i)
    }
    val t10 = now
    println("queue2 take " + (t10 - t9) + "ms to append 100 times")
    (0 to times).foreach(_ => queue2.head)
    val t11 = now
    println("queue2 take " + (t11 - t10) + "ms to invoke head 100 times")
    (0 to times).foreach(_ => queue2.tail)
    val t12 = now
    println("queue2 take " + (t12 - t11) + "ms to invoke tail 100 times")

  }
}

class SlowAppendQueue[T](elems: List[T]) {
  def head = elems.head
  def tail = new SlowAppendQueue(elems.tail)
  def append(e: T) = new SlowAppendQueue(elems ::: List(e))
}

class SlowHeadQueue[T](smele: List[T]) {
  def head = smele.last
  def tail = new SlowHeadQueue(smele.init)
  def append(e: T) = new SlowHeadQueue(e :: smele)
}

class Queue[T](
  private val leading: List[T],
  private val trailing: List[T]){

  private def mirror =
    if(leading.isEmpty)
      new Queue(trailing.reverse, Nil)
    else
      this

  def head = mirror.leading.head

  def tail = {
    val q = mirror
    new Queue(q.leading.tail, q.trailing)
  }

  def append(e: T) = new Queue(leading, e :: trailing)
}

class Queue2[+T] private (
  private[this] var leading: List[T],
  private[this] var trailing: List[T]
){
  private def mirror() =
    if(leading.isEmpty)
      while(trailing.nonEmpty) {
        leading = trailing.head :: leading
        trailing = trailing.tail
      }

  def head: T = {
    mirror()
    leading.head
  }

  def tail: Queue2[T] = {
    mirror()
    new Queue2(leading.tail, trailing)
  }

  def append[U >: T](x: U): Queue2[U] = {
    new Queue2(leading, x :: trailing)
  }
}

object Queue2 {
  def apply[T](xs: T*) =
    new Queue2(xs.toList, Nil)
}


/**
   * covariant/contravariant test
   *
   */
class Publication(val title: String)
class Book(title: String) extends Publication(title)

object Library {
  val books: Set[Book] =
    Set(
      new Book("programming in scala"),
      new Book("math")
    )

  def printBookList(info: Book => AnyRef){
    for (book <- books)
      println(info(book))
  }
}


// object Customer extends Application {
//   def getTitle(p: Publication): String = p.title
//   Library.printBookList(getTitle)
// }
