package cn.kongliangzhong.gui

import _root_.scala.swing._

object GuiTest {

}

object FirstSwingApp extends SimpleGUIApplication {
  def top = new MainFrame {
    title = "First Swing App"
    contents = new Button {
      text = "Click me"
    }
  }
}
