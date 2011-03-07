package com.weiglewilczek.flexlist

import org.eclipse.swt.graphics.Image

trait IFlexListContentProvider {
	
	def getValue(obj: AnyRef, index:Int):AnyRef
	
	def getImage(obj: AnyRef, index:Int):Image
	
	def getSource(obj: AnyRef, index:Int):List[AnyRef]
}