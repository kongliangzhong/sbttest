package cn.kongliangzhong.scala.actor

import scala.actors._
import scala.actors.Actor._

object SillyActor extends Actor {
  def act() {
    for(i <- 1 to 5) {
      println("acting ... " + i)
      Thread.sleep(1000)
    }
  }
}


object ActorTest {
  val echoActor = actor {
    while(true) {
      receive {
        case msg: Int =>
          println("received message: " + msg)
      }
    }
  }

  def echoTest {
    echoActor ! "hi there"
    echoActor ! 122

    Actor.self ! "dsjfkdsjf"
    Actor.self.receive { case x => println(x)}
  }

  val sillyActor2 = actor {
    def emoteLater() = {
      val mainActor = self
      actor {
        Thread.sleep(1000)
        mainActor ! "Emote"
      }
    }

    var emoted = 0
    emoteLater()

    loop {
      react {
        case "Emote" =>
          println("I'm acting")
          emoted += 1
          if(emoted <= 5)
            emoteLater()
        case msg =>
          println("receive msg: " + msg)
      }
    }
  }

  def actorTest {
    sillyActor2 ! "hi there"
  }
}

object NameResolver extends Actor {
  import java.net.{InetAddress, UnknownHostException}
  def act() {
    react {
      case (name: String, actor: Actor) =>
        actor ! getIp(name)
        act()
      case "EXIT" =>
        println("exiting...")
      case msg =>
        println("Unhandled exception msg: " + msg)
        act()
    }
  }

  def getIp(name: String): Option[InetAddress] = {
    try{
      Some(InetAddress.getByName(name))
    } catch {
      case _: UnknownHostException => None
    }
  }
}

object NameResolver2 extends Actor {
  import java.net.{InetAddress, UnknownHostException}
  def act() {
    loop {
      react {
        case (name: String, actor: Actor) =>
          actor ! getIp(name)
        case msg =>
          println("Unhandled exception msg: " + msg)
      }
    }
  }

  def getIp(name: String): Option[InetAddress] = {
    try{
      Some(InetAddress.getByName(name))
    } catch {
      case _: UnknownHostException => None
    }
  }
}

import java.net.{InetAddress, UnknownHostException}

case class LookupIp(name: String, responseTo: Actor)
case class LookupResult(name: String, address: Option[InetAddress])

object NameResolver3 extends Actor {
  def act() {
    loop {
      react {
        case LookupIp(name, actor) =>
          actor ! LookupResult(name, getIp(name))
        case msg =>
          println("Unhandled exception msg: " + msg)
      }
    }
  }

  def getIp(name: String): Option[InetAddress] = {
    try{
      Some(InetAddress.getByName(name))
    } catch {
      case _: UnknownHostException => None
    }
  }
}
