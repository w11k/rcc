package com.weiglewilczek.coop.model

import java.util.Date

case class HarvestTime(override val id: AnyVal, override val user: User, override val time: Date, override val text: String, var duration: Long, var project: HarvestProject, var task: HarvestTask) extends Coop(id: AnyVal, user: User, time: Date, text: String) {
}