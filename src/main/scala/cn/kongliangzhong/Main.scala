package cn.kongliangzhong

import cn.kongliangzhong.thrift.{ThriftServer, ThriftClient}
import net.lag.configgy.Configgy
import net.lag.logging.Logger
import cn.kongliangzhong.guice.GuiceTest
import cn.kongliangzhong.twitter.LogTest
import cn.kongliangzhong.scala._

object Main {

  def main(args: Array[String]): Unit = {
    //guiceTest
    //twitterLog
//    basicTest
    new TypeParamTest().test
  }

  private def basicTest {
    new BasicTest().test
  }

  private def guiceTest {
    new GuiceTest().test
  }

  private def twitterLog {
    import com.twitter.logging.config._
    import com.twitter.logging.{Level, Policy}

    val config = new LoggerConfig {
      node = ""
      level = Level.DEBUG
      handlers = new FileHandlerConfig {
        filename = "log/twitter.log"
        roll = Policy.SigHup
      }
    }
    config()
    new LogTest().test
  }

  private def laglogTest {
    Configgy.configure("config/test.conf")
    val logger = Logger.get
    logger.info("info message from net.lag.logging.Logger")
  }

  private def thriftTest {
    new ThriftServer().startThriftService

    Thread sleep 1000
    new ThriftClient().invoke
  }
}


//object ThriftMain {

//  def main(args: Array[String]) {
  //  new ThriftClient().invoke
 // }
//}
