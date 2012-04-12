package cn.kongliangzhong.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

trait Slf4j {
  lazy val logger: Logger = LoggerFactory.getLogger(this.getClass)
}
