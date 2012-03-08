package cn.kongliangzhong.guice

import com.google.inject._

class GuiceTest {
  def test = {
    val injector = Guice.createInjector(new GuiceTestModule)
    (0 to 3).foreach {
      i =>
        println("i = " + i)
        val mainService = injector.getInstance(classOf[MainService])
        mainService.service(124)
        println("mainService = " + mainService)
    }
  }
}

class GuiceTestModule extends AbstractModule {
  override def configure = {
    bind(classOf[MainService]).to(classOf[MainServiceImpl])//.in(classOf[Singleton])
    bind(classOf[ArgService]).to(classOf[ArgServiceImpl]).in(classOf[Singleton])

//    bind(classOf[MainService]).in(classOf[Singleton])
  }
}

trait ArgService {
  def process(num: Int): Unit
}

class ArgServiceImpl extends ArgService {
  override def process (num: Int) = {
    println("ArgImpl.process; num=" + num)
  }
}

trait MainService {
  def service(num: Int): Unit
}

class MainServiceImpl @Inject()(val arg: ArgService) extends MainService {
  override def service(num: Int) = {
    println("argService = " + arg)
    arg.process(num)
  }
}
