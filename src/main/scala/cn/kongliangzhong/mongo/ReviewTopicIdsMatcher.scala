package cn.kongliangzhong.mongo

import com.yunrang.social.comitium._
import com.mongodb.casbah.Imports._
import com.mongodb.WriteConcern
import java.util.{List => JList}
import com.yunrang.base.thrift.ThriftSerialization._
import scalaj.collection.Imports._
import scala.collection.JavaConversions._
import com.twitter.logging.Logger

object ReviewTopicIdsMatcher extends DocWithCursor with Common {
  // val RID = "_id"

  // val host = "dd02" //"10.20.68.21"
  // val port = 6000 //31000
  // val dbName = "comitium"
  // val batchNum = 500

  // var cursor = Long.MinValue
  // var hasNext = true

  //val logger = Logger.get(getClass)

  def process: Unit = {
    initLog

    // while(hasNext){
    //   processBatch
    //   Thread sleep 100
    // }
    List(manifest[Review]).foreach {
      m =>
        var hasMore = true
        while(hasMore) {
          val docs = getDocs(batchNum)(m)
          if(docs.isEmpty) hasMore = false
          docs.foreach(processDoc(_))
          Thread sleep 10
        }
    }

  }

  // def processBatch = {
  //   //getReviews(batchNum).foreach(processOne)
  // }

  /**
  * processDuplicatedTags : delete topic_ids with same titles.
  * keep only one of them.
  * since duplicated tags can only occur between topic_ids(0)
  * and the rest of topic_ids elements. we will keep topic_ids(0)
  * and remove the element in topic_ids the have same tag with it.
  */
  // private def processDuplicatedTags(review: Review): Unit = {
  //   def getTidWithSameTag(tid: Long, tids: List[Long]): Option[Long] = {
  //     val title = getTopicTitleWithId(tid)
  //     for(id <- tids){
  //       val t = getTopicTitleWithId(id)
  //       if(t == title)
  //         return Some(id)
  //     }
  //     return None
  //   }

  //   val r_tids = review.base.topic_ids.asScala
  //   val r_tid = review.topic_id
  //   getTidWithSameTag(r_tid, r_tids) match {
  //     case Some(id) =>
  //       val new_tids = r_tids diff List(id)
  //       updateReviewTopicIds(review.base.id, new_tids)
  //     case None =>
  //   }
  // }

  // private def processOne(review: Review): Unit = {
  //   val tags = review.base.topics
  //   if(tags == null || tags.isEmpty){
  //     logger.ifDebug ("review with id: " + review.base.id + " has empty topic tags, skip!")
  //     return
  //   }
  //   val topicIds = review.base.topic_ids
  //   // if(topicIds != null && topicIds.nonEmpty){
  //   //   logger.ifDebug("review with id: " + review.base.id + " already has nonEmpty tipic_ids, skip it.")
  //   //   return
  //   // }
  //   logger.ifDebug ("review with id: " + review.base.id + " has topics:" + tags)
  //   var tids: List[Long] = Nil
  //   tags.asScala.foreach {
  //     topic =>
  //       val topicId = getTopicIdWithName(topic)
  //       topicId match {
  //         case Some(id) =>
  //           tids = tids ::: List(id)
  //         case None =>
  //       }
  //   }
  //   logger.ifDebug("review.topic_id =" + review.topic_id)
  //   if(!tids.contains(review.topic_id)){
  //     logger.ifDebug("add reviews topic_id to topic_ids")
  //     tids = review.topic_id :: tids
  //   }
  //   logger.ifDebug ("found matched topicIds: " + tids)
  //   updateReviewTopicIds(review.base.id, tids)
  // }

  /**
  * processDoc: process Review, Question and Answers topic_ids.
  */
  private def processDoc[T: Manifest](doc: T): Unit = {
    val base = getBaseFromDoc(doc)
    val tagList = base.topics
    if(tagList == null || tagList.isEmpty){
      logger.ifDebug ("doc with id: " + base.id + " has empty topic tags, skip!")
      return
    }
    var tags = tagList.asScala.toList
    // val topicIds = base.topic_ids
    // if(topicIds != null && topicIds.nonEmpty){
    //   logger.ifDebug("review with id: " + review.base.id + " already has nonEmpty tipic_ids, skip it.")
    //   return
    // }
    logger.ifDebug ("doc with id: " + base.id + " has topics:" + tags)
    var tids: List[Long] = Nil
    //var newTags: List[String] = Nil
    if(manifest[T].erasure == rt || manifest[T].erasure == qt) {
      logger.info("filter duplicated title.")
      val topicId = getTopicIdFromDoc(doc)
      val title = getTopicTitleWithId(topicId).getOrElse("")
      tags = tags diff List(title)
      tids = topicId :: tids
    }

    tags.foreach {
      tag =>
        val topicId = getTopicIdWithName(tag)
        topicId match {
          case Some(id) =>
            tids = tids ::: List(id)
          case None =>
        }
    }

    updateDocTopicIds(base.id, tids)
  }

  // private def concatTopicIdWithIds(topicId: Long, topicIds: List[Long]): List[Long] = {
  //   def getDuplicatedId(title: String, ids: List[Long]): Option[Long] = {
  //     ids.foreach {
  //       id =>
  //         val tag = getTopicTitleWithId(id).getOrElse("")
  //         if(title == tag) return Some(id)
  //     }
  //     None
  //   }

  //   val tagIds = topicIds diff List(topicId)
  //   val title = getTopicTitleWithId(topicId).getOrElse("")
  //   if(title.size == 0) return tagIds
  //   getDuplicatedId(title, tagIds) match {
  //     case Some(id) =>
  //       val newTagIds = tagIds diff List(id)
  //       topicId :: newTagIds
  //     case None => topicId :: tagIds
  //   }
  // }

  private def getTopicIdFromDoc[T: Manifest](doc: T) =
    manifest[T].erasure match {
      case `rt` => doc.asInstanceOf[Review].topic_id
      case `qt` => doc.asInstanceOf[Question].topic_id
  //    case `at` => doc.asInstanceOf[Answer].topic_id
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

  private def getTopicTitleWithId(topicId: Long): Option[String] = {
    val coll = mongoConn(dbName)("topics")
    val conditions = MongoDBObject(RID -> topicId)
    val cursor = coll.findOne(conditions)
    val topic = cursor.map(bsonToTopic).toSeq
    if(topic.isEmpty) {
      logger.warning("can not find topic with id:" + topicId)
      None
    } else
      Some(topic(0).title)
  }

  private def updateDocTopicIds[T: Manifest](docId: Long, topicIds: Seq[Long]): Unit = {
    val collName = getCollName
    val coll = mongoConn(dbName)(collName)
    val condition = MongoDBObject(RID -> docId)
    val topicIdDBObjects = topicIds.map(id => MongoDBObject("t" -> id))
    val action = $set("B_topic_ids" -> MongoDBList(topicIdDBObjects: _*))
    coll.update(condition, action, false, false, WriteConcern.SAFE)
  }


  private def bsonToTopic(obj: DBObject): Topic = {
    val topic = fromBytes[Topic](obj.getAs[Array[Byte]]("T").get)
    topic.setBase(DBObjectToBasicInfo(topic.base, obj))
    obj.getAs[String]("title").foreach(topic.setTitle)
    topic
  }

}
