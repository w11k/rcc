package com.weiglewilczek.coop.model

import java.util.Date

case class HarvestTime(override val id: String, override val user: User, override val time: Date, override val text: String, var duration: Long, var project: HarvestProject, var task: HarvestTask) extends Coop(id, user, time, text) {
}