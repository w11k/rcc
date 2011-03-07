package com.weiglewilczek.coop.client.ui

import org.eclipse.jface.action.IMenuManager
import org.eclipse.ui.IWorkbenchWindow
import org.eclipse.ui.application.ActionBarAdvisor
import org.eclipse.ui.application.IActionBarConfigurer

class ApplicationActionBarAdvisor(configurer:IActionBarConfigurer) extends ActionBarAdvisor(configurer:IActionBarConfigurer) {

    override protected def makeActions(window:IWorkbenchWindow) {
    }

    override protected def fillMenuBar(menuBar:IMenuManager) {
    }
}