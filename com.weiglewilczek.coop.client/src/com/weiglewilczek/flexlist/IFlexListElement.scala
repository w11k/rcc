package com.weiglewilczek.flexlist

import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control
import scala.collection.mutable.ListBuffer

trait IFlexListElement {
  var children: ListBuffer[Control] = new ListBuffer[Control]

  def getListChildren(): List[Control] = {
    return children.toList
  }

  def getChild(index: Int): Control = {
    return children.apply(index)
  }

  def addChild(child: Control) = {
    children.append(child)
  }

}