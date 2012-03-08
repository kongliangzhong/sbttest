package cn.kongliangzhong.twitter

import com.twitter.logging.Logger

class LogTest {
  private val logger = Logger.get(getClass)

  def test = {
    val msg = "======== test msg"
    logger.ifDebug("debug msg: =========== ")
    logger.ifInfo("info msg ================")
    logger.warning("warn msg --------------")
  }
}
