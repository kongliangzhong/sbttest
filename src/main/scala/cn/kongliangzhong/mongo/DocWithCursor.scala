package cn.kongliangzhong.mongo

import com.yunrang.social.comitium._
import com.mongodb.casbah.Imports._
import com.yunrang.base.thrift.ThriftSerialization._
import org.apache.thrift.TBase

trait DocWithCursor {
  self: Common =>

  private val cursorMap = scala.collection.mutable.Map[String, Long]()
  val rt = classOf[Review]
  val qt = classOf[Question]
  val at = classOf[Answer]

  def getDocs[T <: TBase[_, _]](limit: Int)(implicit m: Manifest[T]): Seq[T] = {
    logger.info("T = " + m.erasure)
    val collName = getCollName
    if(collName == null) return Nil
    val coll = mongoConn(dbName)(collName)
    val cursor = getCursor(collName)
    val condition = RID $gte cursor
    val mongoCursor = coll.find(condition).sort(MongoDBObject(RID -> 1)).limit(limit)
    //val dbObjs = mongoCursor.toSeq
    val docs = mongoCursor.map(bsonToDoc(_)(m)).toSeq
    if(docs.nonEmpty)
      setCursor(collName, getBaseFromDoc(docs(docs.size-1)).id + 1)
    docs
  }

  def getCollName[T: Manifest]: String =
    manifest[T].erasure match {
      case `rt` => "reviews"
      case `qt` => "questions"
      case `at` => "answers"
      case _ => null
    }

  def getBaseFromDoc[T: Manifest](doc: T) =
    manifest[T].erasure match {
      case `rt` => doc.asInstanceOf[Review].base
      case `qt` => doc.asInstanceOf[Question].base
      case `at` => doc.asInstanceOf[Answer].base
    }

  // private def getIdFromDoc[T: Manifest](doc: T): Long =
  //   manifest[T].erasure match {
  //     case `rt` => doc.asInstanceOf[Review].base.id
  //     case `qt` => doc.asInstanceOf[Question].base.id
  //     case `at` => doc.asInstanceOf[Answer].base.id
  //   }

  // def getReviews(limit: Int): Seq[Review] = {
  //   val dbObjs = getDBObjs("reviews", limit)
  //   val docs = dbObjs.map(b => bsonToDoc(b.asInstanceOf[DBObject], new Review))
  //   if(docs.nonEmpty)
  //     setCursor(collName, docs(docs.size-1).base.id + 1)
  // }

  // private def getDBObjs(collName: String, limit: Int): Seq[AnyRef] = {
  //   val coll = mongoConn(dbName)(collName)
  //   val cursor = getCursor(collName)
  //   val condition = RID $gte cursor
  //   val mongoCursor = coll.find(condition).sort(MongoDBObject(RID -> 1)).limit(limit)
  //   mongoCursor.toSeq
  // }

  private def getCursor(collName: String): Long = {
    if(!cursorMap.contains(collName))
      cursorMap += (collName -> Long.MinValue)
    cursorMap(collName)
  }

  private def setCursor(collName: String, value: Long): Unit ={
    cursorMap(collName) = value
  }

  // private def bsonToReview(obj: DBObject): Review = {
  //   val review = fromBytes[Review](obj.getAs[Array[Byte]]("T").get)
  //   review.setBase(DBObjectToBasicInfo(review.base, obj))
  //   //setOriginIdToThrift(obj, review)
  //   review
  // }

  private def bsonToDoc[T <: TBase[_, _] : Manifest](obj: DBObject): T = {
    val doc = fromBytes[T](obj.getAs[Array[Byte]]("T").get)
    val m = manifest[T]
    logger.info("curr type =" + m.erasure)
    m.erasure match {
      case `rt` =>
        val review = doc.asInstanceOf[Review]
        review.setBase(DBObjectToBasicInfo(review.base, obj))
        review
      case `qt` =>
        val question = doc.asInstanceOf[Question]
        question.setBase(DBObjectToBasicInfo(question.base, obj))
        question
      case `at` =>
        val answer = doc.asInstanceOf[Answer]
        answer.setBase(DBObjectToBasicInfo(answer.base, obj))
        answer
      case _ =>
        logger.error("scala match error: None matched. class = " + m.erasure)
    }
    doc
  }

  // private def getReviewsWithCursor(num: Int): Seq[Review] = {
  //   val coll = mongoConn(dbName)("reviews")
  //   val condition = RID $gte cursor
  //   val mongoCursor = coll.find(condition).sort(MongoDBObject(RID -> 1)).limit(num)
  //   val reviews = mongoCursor.map(bsonToReview).toSeq
  //   logger.ifDebug("-------- get reviews in " + dbName + ", size = " + reviews.size)
  //   if(reviews.isEmpty)
  //     hasNext = false
  //   else
  //     cursor = reviews(reviews.size - 1).base.id + 1
  //   reviews
  // }
}
