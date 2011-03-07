package com.weiglewilczek.coop.client.ui

import org.eclipse.ui.application.IWorkbenchWindowConfigurer
import org.eclipse.ui.application.WorkbenchAdvisor
import org.eclipse.ui.application.WorkbenchWindowAdvisor
import org.eclipse.swt.widgets.Display
import com.weiglewilczek.coop.client.ui.login.LoginDialog
import com.weiglewilczek.coop.client.ui.login.CoopClient
import com.weiglewilczek.coop.client.ui.login.LogonManager

class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

  override def createWorkbenchWindowAdvisor(configurer: IWorkbenchWindowConfigurer): WorkbenchWindowAdvisor = {
    return new ApplicationWorkbenchWindowAdvisor(configurer)
  }

  override def getInitialWindowPerspectiveId(): String = {
    return ApplicationWorkbenchAdvisor.PERSPECTIVE_ID
  }

  override def preStartup {
    if (CoopClient.getInstance.remember && CoopClient.getInstance.user != null && CoopClient.getInstance.user.length > 0 && CoopClient.getInstance.password != null && CoopClient.getInstance.password.length > 0) {
      LogonManager.login
    }

    var trys = 0

    while (trys < 3 && !CoopClient.getInstance.loggedIn) {
      trys += 1
      val dialog = LoginDialog(Display.getDefault())
      dialog.open
      val result = dialog.result
      if (result == LoginDialog.RESULT_LOGIN) {
    	  CoopClient.storeUserNamePassword
        LogonManager.login
      } else {
        System.exit(0)
      }
    }

    if (!CoopClient.getInstance.loggedIn) {
      System.exit(0)
    }

  }

}

object ApplicationWorkbenchAdvisor {
  val PERSPECTIVE_ID = "com.weiglewilczek.coop.client.ui.Perspective"

}