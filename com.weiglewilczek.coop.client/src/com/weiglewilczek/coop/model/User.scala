package com.weiglewilczek.coop.model

import org.eclipse.swt.graphics.Image

case class User(val id:AnyVal, var image:Image, var name:String, var firstName:String) {

}