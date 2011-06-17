package com.weiglewilczek.coop.client.ui.preferences;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.handlers.HandlerUtil;

class OpenPreferences extends AbstractHandler {

	def execute(event:ExecutionEvent):Object = {
		val dialog = new PreferenceDialog(HandlerUtil.getActiveShell(event))
		dialog.open()
		return null
	}

	override def isEnabled():Boolean = {
		return true
	}

}
