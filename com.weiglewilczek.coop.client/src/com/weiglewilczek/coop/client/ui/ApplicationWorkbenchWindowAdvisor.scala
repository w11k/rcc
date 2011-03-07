package com.weiglewilczek.coop.client.ui

import org.eclipse.swt.graphics.Point
import org.eclipse.ui.application.ActionBarAdvisor
import org.eclipse.ui.application.IActionBarConfigurer
import org.eclipse.ui.application.IWorkbenchWindowConfigurer
import org.eclipse.ui.application.WorkbenchWindowAdvisor

class ApplicationWorkbenchWindowAdvisor(configurer:IWorkbenchWindowConfigurer) extends WorkbenchWindowAdvisor(configurer:IWorkbenchWindowConfigurer) {
	
    override def createActionBarAdvisor(configurer:IActionBarConfigurer):ActionBarAdvisor= {
        return new ApplicationActionBarAdvisor(configurer)
    }
    
    override def preWindowOpen() {
        val configurer = getWindowConfigurer()
        configurer setInitialSize new Point(400, 600)
        configurer setShowCoolBar false 
        configurer setShowStatusLine false
        configurer setTitle "Co-op"
    }
}