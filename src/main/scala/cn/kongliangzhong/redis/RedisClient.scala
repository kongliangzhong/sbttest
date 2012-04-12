package cn.kongliangzhong.redis

import com.redis._
import cn.kongliangzhong.util._

class RedisClientTest extends Slf4j {
  val client = new RedisClient("localhost", 6379)

  def test {
    kvTest
  }

  private def kvTest {
    client.set("key1", "some value")
    val value = client.get("key1")
    logger.error("value=" + value)
  }
}
