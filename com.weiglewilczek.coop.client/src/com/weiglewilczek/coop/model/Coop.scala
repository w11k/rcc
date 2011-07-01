package com.weiglewilczek.coop.model

import java.util.Date

case class Coop(val id:String, val user:User, val time:Date, val text:String) {

}