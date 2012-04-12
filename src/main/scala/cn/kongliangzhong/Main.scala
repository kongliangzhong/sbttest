package cn.kongliangzhong

import cn.kongliangzhong.thrift.{ThriftServer, ThriftClient}
import net.lag.configgy.Configgy
import net.lag.logging.Logger
import cn.kongliangzhong.guice.GuiceTest
import cn.kongliangzhong.twitter.LogTest
import cn.kongliangzhong.scala._
import cn.kongliangzhong.scala.actor._
import cn.kongliangzhong.redis.RedisClientTest

import cn.kongliangzhong.scala.testPrefs._

object Main {

  def main(args: Array[String]): Unit = {
    //appInit
    //guiceTest
    //twitterLog
    basicTest
    //new TypeParamTest().test
    //redisTest
    //actorTest
  }

  private def actorTest {
    //ActorTest.echoTest
    import _root_.scala.actors.Actor

    val act = NameResolver3.start()
    val act2 = NameResolver2.start()
    act ! LookupIp("www.scala-lang.org", Actor.self)
    Actor.self.receiveWithin(1000) { case x => println(x) }
    NameResolver2 ! LookupIp("wwwww.scala-lang.org", Actor.self)
    Actor.self.receiveWithin(100000) { case x => println(x) }
    NameResolver2 ! ("EXIT")
    Actor.self.receiveWithin(100000) { case x => println(x) }
  }

  private def actorTest2 {
    ActorTest.actorTest
  }

  private def redisTest {
    new RedisClientTest().test
  }

  private def basicTest {
    //new BasicTest().test
    //LongLines.processFile("/home/kongliangzhong/diffs/edm.diff", 20)
    //LongLines.echoTest
    // FuncTest.byNameParam(2>3)
    //FuncTest.andThenTest
    // val res = FileMatcher.filesContaining2("s")
    // res.foreach(println)
    //MySimulation.test
    //Greeter.greet("kdfjds")
    /*
    val m1 = ImplicitTest.maxList(List(1, 2, 3))
    val m2 = ImplicitTest.maxList(List(1.1, 2.2, 3.4))
    val m3 = ImplicitTest.maxList(List("a", "djfhsd", "ccccc"))
    println("m1: " + m1)
    println("m2: " + m2)
    println("m3: " + m3)
    */
    //ForTest.test
    Spiral.test
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

//import org.apache.log4j.xml._

trait AppInit {

  def appInit {

    //initLog4j
  }

  private def initTwitterLog {
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
  }

  // private def initLog4j {
  //   println("load log4j config ...")
  //   new DOMConfigurator().doConfigure(this.getClass.getResourceAsStream("log4j.xml"), LogManager.getLoggerRepository)
  // }

}
