package com.weiglewilczek.coop.model

import java.util.Date

case class HarvestProject(val id:AnyVal, val name:String, val tasks:List[HarvestTask]) {
}