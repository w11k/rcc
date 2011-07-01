package com.weiglewilczek.coop.client.ui

import org.eclipse.swt.graphics.Point
import org.eclipse.swt.widgets.Display
import org.eclipse.ui.application.ActionBarAdvisor
import org.eclipse.ui.application.IActionBarConfigurer
import org.eclipse.ui.application.IWorkbenchWindowConfigurer
import org.eclipse.ui.application.WorkbenchWindowAdvisor
import com.weiglewilczek.coop.client.ui.login.CoopClient
import scala.collection.mutable.ListBuffer
import org.eclipse.ui.IWindowListener
import org.eclipse.ui.IWorkbenchWindow

class ApplicationWorkbenchWindowAdvisor(configurer: IWorkbenchWindowConfigurer) extends WorkbenchWindowAdvisor(configurer: IWorkbenchWindowConfigurer) with IWindowListener with TitleListener {

  override def createActionBarAdvisor(configurer: IActionBarConfigurer): ActionBarAdvisor = {

    return new ApplicationActionBarAdvisor(configurer)
  }

  override def preWindowOpen() {
    val configurer = getWindowConfigurer()
    configurer setInitialSize new Point(400, 650)
    configurer setShowCoolBar true
    configurer setShowStatusLine false
    configurer setTitle Messages.applicationName
  }

  override def postWindowOpen() {
    def configurer = getWindowConfigurer()
    configurer.setTitle(Messages.applicationName + " - " + CoopClient.getInstance.currentUser.firstName + " " + CoopClient.getInstance.currentUser.name)

    setWorkbenchWindowConfigurer(getWindowConfigurer)
    TitleListener.addListener(this)
  }

  def windowActivated(window: IWorkbenchWindow) {
    windowActivated(true)
  }

  def windowDeactivated(window: IWorkbenchWindow) {
    windowActivated(false)
  }

  def windowClosed(window: IWorkbenchWindow) {
  }

  def windowOpened(window: IWorkbenchWindow) {
  }
}

trait TitleListener {
  private var configurer: IWorkbenchWindowConfigurer = null
  var windowActivated = false
  private var newEntries: Int = 0

  def setWorkbenchWindowConfigurer(configurer: IWorkbenchWindowConfigurer) {
    this.configurer = configurer
  }

  def updateUnreadEntries(newEntries: Int) {
    this.newEntries = this.newEntries + newEntries

    configurer match {
      case configurer: IWorkbenchWindowConfigurer =>
        Display.getDefault.syncExec(new Runnable() {
          override def run() {
            if (newEntries == 0) {
              configurer.setTitle(Messages.applicationName + " - " + CoopClient.getInstance.currentUser.firstName + " " + CoopClient.getInstance.currentUser.name)
            } else {
              configurer.setTitle(Messages.applicationName + " - " + CoopClient.getInstance.currentUser.firstName + " " + CoopClient.getInstance.currentUser.name + " (" + newEntries + ")")
            }
          }
        })
      case _ =>
    }

    if (windowActivated) {
      resetUnreadEntries()
    }
  }

  def resetUnreadEntries() {
    this.newEntries = 0
  }

  def windowActivated(windowActivated: Boolean) {
    this.windowActivated = windowActivated
  }
}

object TitleListener {

  private val listeners = new ListBuffer[TitleListener]

  def addListener(listener: TitleListener) {
    listeners.append(listener)
  }

  def fireUnreadEntriesAdded(newEntries: Int) {
    listeners.toList.forall({ listener =>
      listener.updateUnreadEntries(newEntries)
      true
    })
  }
}
