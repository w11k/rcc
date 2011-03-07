package com.weiglewilczek.coop.client.ui

import com.weiglewilczek.flexlist.IFlexListElement
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Canvas
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Link
import org.eclipse.swt.layout.FormLayout
import org.eclipse.swt.layout.FormData
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.FormAttachment
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.SWT

abstract class Element(parent: Composite, style:Int) extends Composite(parent: Composite, style: Int) with IFlexListElement {
	protected var image:Canvas = null
	protected var user:Link = null
	protected var time:Label = null
	
	protected var bottomBorder:Canvas = null
	
	protected def createContent
	{
		setLayout(new FormLayout())
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE))
		
		image = new Canvas(this, SWT.BORDER)
		val imageLayoutData = new FormData()
		imageLayoutData.top = new FormAttachment(0, 5)
		imageLayoutData.left = new FormAttachment(0, 5)
		imageLayoutData.height = 20
		imageLayoutData.width = 20
		image.setLayoutData(imageLayoutData)
		addChild(image)
		
		user = new Link(this, SWT.NONE)
		val userLayoutData = new FormData()
		userLayoutData.top = new FormAttachment(0, 5)
		userLayoutData.left = new FormAttachment(image, 5, SWT.RIGHT)
		user.setLayoutData(userLayoutData)
		addChild(user)
		
		time = new Label(this, SWT.NONE)
		time.setForeground(getDisplay().getSystemColor(SWT.COLOR_GRAY))
		val timeLayoutData = new FormData()
		timeLayoutData.top = new FormAttachment(0, 5)
		timeLayoutData.left = new FormAttachment(user, 5, SWT.RIGHT)
		time.setLayoutData(timeLayoutData)
		addChild(time)
		
		bottomBorder = new Canvas(this, SWT.BORDER)
		val bottomBorderData = new FormData()
		bottomBorderData.bottom = new FormAttachment(100, 0)
		bottomBorderData.left = new FormAttachment(0, 0)
		bottomBorderData.right = new FormAttachment(100, 0)
		bottomBorderData.height = 0
		bottomBorder.setLayoutData(bottomBorderData)
	}
	
	def showBorder(show:Boolean)
	{
		bottomBorder.setVisible(show)
	}
	
	override def computeSize(wHint:Int, hHint:Int, changed:Boolean):Point =
	{
		val size = super.computeSize(wHint, hHint, changed)
		size.y = size.y + 5
		return size
	}
}