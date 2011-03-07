package com.weiglewilczek.coop.client.ui

import com.weiglewilczek.flexlist.IFlexListContentProvider
import scala.collection.immutable.List
import org.eclipse.swt.graphics.Image
import com.weiglewilczek.coop.model.Coop
import com.weiglewilczek.coop.model.HarvestTime
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date

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
          case index if (index.equals(3)) => return coop.text
          case _ => return ""
        }
      }
      case _ => return null
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

  def getSource(obj: Object, index: Int): List[AnyRef] = { Nil }

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
  
  	def coopHumanFriendlyDate(created:Date):String = {
		// today
		val today = new Date()
 
		// how much time since (ms)
		val duration = today.getTime() - created.getTime()
 
		val second = 1000
		val minute = second * 60
		val hour = minute * 60
		val day = hour * 24
 
		if (duration < second * 7) {
			return "right now";
		}
 
		if (duration < minute) {
			val n = Math.floor(duration / second).intValue;
			return n + " seconds ago";
		}
 
		if (duration < minute * 2) {
			return "about 1 minute ago";
		}
 
		if (duration < hour) {
			val n = Math.floor(duration / minute).intValue;
			return "about " + n + " minutes ago";
		}
 
		if (duration < hour * 2) {
			return "about 1 hour ago";
		}
 
		if (duration < day) {
			val n = Math.floor(duration / hour).intValue;
			return "about " + n + " hours ago";
		}
		if (duration > day && duration < day * 2) {
			return "yesterday";
		}
 
		if (duration < day * 365) {
			val n = Math.floor(duration / day).intValue;
			return "about " + n + " days ago";
		} else {
			return "over a year ago";
		}
	}
}