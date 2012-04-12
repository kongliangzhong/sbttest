package cn.kongliangzhong.mongo

import com.twitter.logging.Logger
import com.mongodb.casbah.Imports._
import com.yunrang.social.comitium._
import scala.collection.JavaConversions._
import scalaj.collection.Imports._

trait Common {
  val RID = "_id"

  val host = "10.20.68.21"
  val port = 31000
  val dbName = "comitium"
  val batchNum = 500

  val mongoConn = MongoConnection(host, port)

  val logger = Logger.get(getClass)

  def initLog: Unit = {
    import com.twitter.logging.config._
    import com.twitter.logging.{Level, Policy}

    val fileConfig = new LoggerConfig {
      node = ""
      level = Level.DEBUG
      handlers = new FileHandlerConfig {
        filename = "log/updateTopicIds_RQA.log"
        roll = Policy.Hourly
      }
    }
    fileConfig()

    // val consoleConfig = new LoggerConfig {
    //   node = ""
    //   level = Level.DEBUG
    //   handlers = new ConsoleHandlerConfig
    // }
    // consoleConfig()
  }

  def DBObjectToBasicInfo(base: BasicInfo, dbo: DBObject): BasicInfo = {
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
