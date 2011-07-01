package com.weiglewilczek.coop.client

import org.eclipse.ui.plugin.AbstractUIPlugin
import org.osgi.framework.BundleContext
import org.eclipse.jface.resource.ImageDescriptor

object Activator
{
	val PLUGIN_ID = "com.weiglewilczek.coop.client"
	var plugin:Activator = null
		
	def getImageDescriptor(path:String) : ImageDescriptor = {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path)
	}
		
	def getDefault() : Activator = {
		return plugin
	}
	
	def setPlugin(plugin:Activator)
	{
		this.plugin = plugin
	}
	
	def unsetPlugin()
	{
		this.plugin = null
	}
}
class Activator extends AbstractUIPlugin {
	
	override def start(context:BundleContext)
	{
		super.start(context)
		Activator.setPlugin(this)
	}
	
	override def stop(context:BundleContext)
	{
		Activator.unsetPlugin()
		super.stop(context)
	}
}
