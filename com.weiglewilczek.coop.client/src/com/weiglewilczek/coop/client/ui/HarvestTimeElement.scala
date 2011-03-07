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

class HarvestTimeElement(parent: Composite, style: Int = SWT.NONE) extends Element(parent: Composite, style: Int) {
	createContent()
	
	protected var duration:Label = null
	protected var project:Label = null
	protected var task:Label = null
	protected var text:Label = null
	
	override def createContent()
	{
		super.createContent
		
		duration = new Label(this, SWT.RIGHT)
		duration.setBackground(duration.getDisplay.getSystemColor(SWT.COLOR_YELLOW))
		val durationLayoutData = new FormData()
		durationLayoutData.top = new FormAttachment(user, 5, SWT.BOTTOM)
		durationLayoutData.left = new FormAttachment(image, 5, SWT.RIGHT)
		durationLayoutData.width = 37
		duration.setLayoutData(durationLayoutData)
		addChild(duration)
		
		project = new Label(this, SWT.NONE)
		val projectLayoutData = new FormData()
		projectLayoutData.top = new FormAttachment(user, 5, SWT.BOTTOM)
		projectLayoutData.left = new FormAttachment(duration, 5, SWT.RIGHT)
		project.setLayoutData(projectLayoutData)
		addChild(project)
		
		task = new Label(this, SWT.NONE)
		val taskLayoutData = new FormData()
		taskLayoutData.top = new FormAttachment(user, 5, SWT.BOTTOM)
		taskLayoutData.left = new FormAttachment(project, 5, SWT.RIGHT)
		taskLayoutData.right = new FormAttachment(100, -5)
		task.setLayoutData(taskLayoutData)
		addChild(task)
		
		text = new Label(this, SWT.WRAP)
		val textLayoutData = new FormData()
		textLayoutData.top = new FormAttachment(duration, 5, SWT.BOTTOM)
		textLayoutData.left = new FormAttachment(image, 5, SWT.RIGHT)
		textLayoutData.right = new FormAttachment(100, -5)
		text.setLayoutData(textLayoutData)
		addChild(text)
	}
}