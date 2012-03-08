package cn.kongliangzhong.thrift

import com.yunrang.base.server.{YrThriftServiceBuilder, YrThriftClientBuilder}

class ThriftServer {
  val port = 18001

  def startThriftService {
    try {
      val testImpl = new TestServiceImpl
      val service = new YrThriftServiceBuilder[TestService.Iface]()
        .setImplementation(testImpl)
        .setPort(port)
        .setUsingFramedTransport(false)
        .build

      service.start
      println("thrift service started ...")
    }
  }
}


class TestServiceImpl extends TestService.Iface {
  var name: String = null

  def printNameAndTs(name: String): Unit = {
    this.name = name
    println("name = " + name +"; timestamp = " + System.currentTimeMillis)
  }

  def getResult: TestResult = new TestResult().setName(name).setTimestamp(System.currentTimeMillis)
}


class ThriftClient {
  def invoke {
    val client = new YrThriftClientBuilder[TestService.Iface, TestService.Client]()
        .setUsingFramedTransport(false)
        .setServiceAddress("localhost", 18001)
        .build

    if(client == null) println("client build failed!")

    client.printNameAndTs("thriftclient")
    println("result = " + client.getResult)
  }
}
