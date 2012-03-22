package cn.kongliangzhong.mongo

import com.yunrang.social.comitium._
import com.mongodb.casbah.Imports._
import com.mongodb.WriteConcern
import java.util.{List => JList}
import com.yunrang.base.thrift.ThriftSerialization._
import scalaj.collection.Imports._
import scala.collection.JavaConversions._
import com.twitter.logging.Logger

object ReviewTopicIdsMatcher {
  val RID = "_id"

  val host = "localhost"
  val port = 18000
  val dbName = "testdb"
  val batchNum = 500

  var cursor = Long.MinValue
  var hasNext = true

  val mongoConn = MongoConnection(host, port)
  val logger = Logger.get(getClass)

  private def initLog: Unit = {
    import com.twitter.logging.config._
    import com.twitter.logging.{Level, Policy}

    val fileConfig = new LoggerConfig {
      node = ""
      level = Level.DEBUG
      handlers = new FileHandlerConfig {
        filename = "log/updateReview.log"
        roll = Policy.Hourly
      }
    }
    fileConfig()

    val consoleConfig = new LoggerConfig {
      node = ""
      level = Level.DEBUG
      handlers = new ConsoleHandlerConfig
    }
    consoleConfig()
  }

  def process: Unit = {
    initLog

    while(hasNext){
      processBatch
      Thread sleep 100
    }
  }

  def processBatch = {
    getReviewsWithCursor(batchNum).foreach(processOne)
  }

  private def processOne(review: Review): Unit = {
    val tags = review.base.topics
    if(tags == null || tags.isEmpty){
      logger.ifDebug ("review with id: " + review.base.id + " has empty topic tags, skip!")
      return
    }
    val topicIds = review.base.topic_ids
    // if(topicIds != null && topicIds.nonEmpty){
    //   logger.ifDebug("review with id: " + review.base.id + " already has nonEmpty tipic_ids, skip it.")
    //   return
    // }
    logger.ifDebug ("review with id: " + review.base.id + " has topics:" + tags)
    var tids: List[Long] = Nil
    tags.asScala.foreach {
      topic =>
        val topicId = getTopicIdWithName(topic)
        topicId match {
          case Some(id) =>
            tids = tids ::: List(id)
          case None =>
        }
    }
    logger.ifDebug("review.topic_id =" + review.topic_id)
    if(!tids.contains(review.topic_id)){
      logger.ifDebug("add reviews topic_id to topic_ids")
      tids = review.topic_id :: tids
    }
    logger.ifDebug ("found matched topicIds: " + tids)
    updateReviewTopicIds(review.base.id, tids)
  }

  private def getReviewsWithCursor(num: Int): Seq[Review] = {
    val coll = mongoConn(dbName)("reviews")
    val condition = RID $gte cursor
    val mongoCursor = coll.find(condition).sort(MongoDBObject(RID -> 1)).limit(num)
    val reviews = mongoCursor.map(bsonToReview).toSeq
    logger.ifDebug("-------- get reviews in " + dbName + ", size = " + reviews.size)
    if(reviews.isEmpty)
      hasNext = false
    else
      cursor = reviews(reviews.size - 1).base.id + 1
    reviews
  }

  private def getTopicIdWithName(topicTitle: String): Option[Long] = {
    val coll = mongoConn(dbName)("topics")
    val condtions = MongoDBObject("title" -> topicTitle)
    val cursor = coll.findOne(condtions)
    val topic = cursor.map(bsonToTopic).toSeq
    if(topic.isEmpty) {
      logger.ifDebug("can not find a topicId match the title:" + topicTitle)
      None
    }
    else Some(topic(0).base.id)
  }

  private def updateReviewTopicIds(reviewId: Long, topicIds: Seq[Long]): Unit = {
    val coll = mongoConn(dbName)("reviews")
    val condition = MongoDBObject(RID -> reviewId)
    val topicIdDBObjects = topicIds.map(id => MongoDBObject("t" -> id))
    val action = $set("B_topic_ids" -> MongoDBList(topicIdDBObjects: _*))
    coll.update(condition, action, false, false, WriteConcern.SAFE)
  }

  private def bsonToReview(obj: DBObject): Review = {
    val review = fromBytes[Review](obj.getAs[Array[Byte]]("T").get)
    review.setBase(DBObjectToBasicInfo(review.base, obj))
    //setOriginIdToThrift(obj, review)

    review
  }

  private def bsonToTopic(obj: DBObject): Topic = {
    val topic = fromBytes[Topic](obj.getAs[Array[Byte]]("T").get)
    topic.setBase(DBObjectToBasicInfo(topic.base, obj))
    obj.getAs[String]("title").foreach(topic.setTitle)
    topic
  }

  private def DBObjectToBasicInfo(base: BasicInfo, dbo: DBObject): BasicInfo = {

    // topics
    dbo.getAs[BasicDBList]("B_topics") foreach {
      dblist =>
        val topics = dblist.map(_.asInstanceOf[DBObject].getAs[String]("t").getOrElse("")).filter(_.nonEmpty)
        if (topics.nonEmpty) base.setTopics(topics) else base.setTopicsIsSet(false)
    }

    // topicIds
    dbo.getAs[BasicDBList]("B_topic_ids") foreach {
      dblist =>
        val topicIds = dblist.map(_.asInstanceOf[DBObject].getAs[Long]("t").getOrElse(0L)).filter(_ != 0L)
        if (topicIds.nonEmpty) base.setTopic_ids(topicIds.asJava) else base.setTopic_idsIsSet(false)
    }

    base
  }
}
