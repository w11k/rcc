package com.weiglewilczek.coop.client

import org.eclipse.equinox.app.IApplication
import org.eclipse.equinox.app.IApplicationContext
import org.eclipse.swt.widgets.Display
import org.eclipse.ui.IWorkbench
import org.eclipse.ui.PlatformUI
import com.weiglewilczek.coop.client.ui.ApplicationWorkbenchAdvisor
import java.lang.Exception

class Application extends IApplication {

  @throws(classOf[Exception])
  def start(context: IApplicationContext): Object = {
    val display = PlatformUI.createDisplay();
    try {
      val returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor())
      val applicationReturn = returnCode match {
        case code: Int if code equals PlatformUI.RETURN_RESTART => IApplication.EXIT_RESTART
        case _ => IApplication.EXIT_OK
      }
      return applicationReturn
    } finally {
      display dispose
    }
  }

  def stop(): Unit = {
	  val workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return;
		val display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			def run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
  }

}