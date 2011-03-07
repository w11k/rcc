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

class CoopElement(parent: Composite, style: Int = SWT.NONE) extends Element(parent: Composite, style: Int) {
  createContent()

  protected var text: Label = null

  override def createContent() {
    super.createContent

    text = new Label(this, SWT.WRAP)
    val textLayoutData = new FormData()
    textLayoutData.top = new FormAttachment(user, 5, SWT.BOTTOM)
    textLayoutData.left = new FormAttachment(image, 5, SWT.RIGHT)
    textLayoutData.right = new FormAttachment(100, -5)
    text.setLayoutData(textLayoutData)
    addChild(text)
  }
}