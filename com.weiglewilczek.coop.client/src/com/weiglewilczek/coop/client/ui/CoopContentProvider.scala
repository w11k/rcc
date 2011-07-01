package com.weiglewilczek.coop.client.ui

import com.weiglewilczek.flexlist.IFlexListContentProvider
import scala.collection.immutable.List
import org.eclipse.swt.graphics.Image
import com.weiglewilczek.coop.model.Coop
import com.weiglewilczek.coop.model.HarvestTime
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import org.eclipse.osgi.util.NLS

class CoopContentProvider extends IFlexListContentProvider {

  def getValue(obj: AnyRef, index: Int): Object = {
    obj match {
      case time: HarvestTime =>
        {
          index match {
            case index if (index.equals(1)) => return time.user.name + " " + time.user.firstName
            case index if (index.equals(2)) => return coopHumanFriendlyDate(time.time)
            case index if (index.equals(3)) => return durationFormat(time.duration)
            case index if (index.equals(4)) => return time.project.name
            case index if (index.equals(5)) => return time.task.name
            case index if (index.equals(6)) => return time.text
            case _ => return null
          }
        }
      case coop: Coop =>
        {
          index match {
            case index if (index.equals(1)) => return coop.user.name + " " + coop.user.firstName
            case index if (index.equals(2)) => return coopHumanFriendlyDate(coop.time)
            case index if (index.equals(3)) => return prepareLinks(coop.text)
            case _ => return ""
          }
        }
      case _ => return ""
    }
  }

  def getImage(obj: Object, index: Int): Image = {
    obj match {
      case coop: Coop =>
        index match {
          case index if (index.equals(0)) => return coop.user.image
          case _ => return null
        }
      case _ => return null
    }
    null
  }

  def getSource(obj: Object, index: Int): AnyRef = {
    obj match {
      case _ => return null
    }
  }

  def prepareLinks(text: String): String = {
    return prepareLink(text, 0)
  }

  def prepareLink(text: String, cursor: Int): String =
    {
      val part1 = text.substring(0, cursor)
      val part2 = text.substring(cursor)

      // TODO besseres matching
      if (part2.indexOf("http") > -1) {
        val startAtHttp = part2.substring(part2.indexOf("http"))
        val partBeforeLink = part2.substring(0, part2.indexOf("http"))
        if (startAtHttp.indexOf(" ") > -1) {
          val link = startAtHttp.substring(0, startAtHttp.indexOf(" "))
          val end = startAtHttp.substring(startAtHttp.indexOf(" "))
          val currentString = part1 + partBeforeLink + "<a>" + link + "</a>" + end
          return prepareLink(currentString, currentString.lastIndexOf("</a>") + 4)
        } else {
          return part1 + partBeforeLink + "<a>" + startAtHttp + "</a>"
        }
      } else {
        return text
      }
    }

  def durationFormat(duration: Long): String = {
    val hours = duration / (3600 * 1000)
    val durationMinusHours = duration - (hours * 3600000)
    val minutes = durationMinusHours / (60000)
    val durationMinusMinutes = durationMinusHours - (minutes * 60000)
    val secs = durationMinusHours / 1000

    var minutesString = ""
    if (minutes < 10) {
      minutesString = "0" + minutes
    } else {
      minutesString = minutes.toString
    }

    var secsString = ""
    if (secs < 10) {
      secsString = "0" + secs
    } else {
      secsString = secs.toString
    }

    if (secs > 0) {
      return hours + ":" + minutesString + ":" + secsString
    } else {
      return hours + ":" + minutesString
    }
  }

  def coopHumanFriendlyDate(created: Date): String = {
    // today
    val today = new Date()

    // how much time since (ms)
    val now: Long = today.getTime()
    val createdTime: Long = created.getTime()
    val duration: Long = now - createdTime

    val second = 1000
    val minute = second * 60
    val hour = minute * 60
    val yesterday_start = hour * today.getHours()
    val yesterday_end = hour * (today.getHours() + 24)
    val day = hour * 24
    
    val durationTimeCleared: Long = duration - (duration % day) + day

    if (duration < second * 7) {
      return Messages.rightNow
    }

    if (duration < minute) {
      val n = Math.round(duration / second).intValue
      return NLS.bind(Messages.secondsAgo, n)
    }

    if (duration < minute * 2) {
      return Messages.aboutOneMinuteAgo
    }

    if (duration < hour) {
      val n = Math.round(duration / minute).intValue
      return NLS.bind(Messages.aboutNMinutesAgo, n)
    }

    if (duration < hour * 1.5) {
      return Messages.aboutOneHourAgo
    }

    if (duration < yesterday_start) {
      val n = Math.round(duration / hour).intValue
      return NLS.bind(Messages.aboutNHoursAgo, n)
    }
    if (duration > yesterday_start && duration < yesterday_end) {
      return Messages.yesterday
    }

    if (duration < day * 365) {
      val hours = durationTimeCleared / day
      val n = hours.intValue
      return NLS.bind(Messages.aboutNDaysAgo, n)
    } else {
      return Messages.overAYearAgo
    }
  }
}