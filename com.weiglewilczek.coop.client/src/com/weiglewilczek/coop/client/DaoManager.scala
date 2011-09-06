package com.weiglewilczek.coop.client

import com.weiglewilczek.coop.model.User
import com.weiglewilczek.coop.model.Coop
import com.weiglewilczek.coop.model.HarvestTime
import com.weiglewilczek.coop.model.HarvestTask
import com.weiglewilczek.coop.model.HarvestProject
import java.net.URL
import java.net.URLConnection
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.InputStream
import java.lang.StringBuffer
import scala.xml.XML
import scala.xml.Elem
import scala.xml.NodeSeq
import java.util.Date
import com.weiglewilczek.coop.client.ui.login.CoopClient
import scala.collection.mutable.ListBuffer
import java.awt.Toolkit
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.widgets.Display
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.GregorianCalendar
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask
import org.eclipse.core.databinding.observable.list.WritableList
import com.weiglewilczek.coop.client.ui.TitleListener
import com.weiglewilczek.coop.client.ui.login.ResultListType
import org.eclipse.core.runtime.jobs.Job
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.Status
import org.eclipse.core.runtime.IStatus

object DaoManager {

  val users = new ListBuffer[User]
  val coops = new WritableList()
  val coopIds = new ListBuffer[String]

  private var lastUpdate: Date = new Date
  private val updateJob = new UpdateJob

  private var updateRunning = false

  class UpdateJob extends Job("Update Coop entries") {
    protected def run(monitor: IProgressMonitor): IStatus = {
      try {
        updateCoops()
        return Status.OK_STATUS;
      } finally {
        schedule(CoopClient.getInstance.updateIntervall)
      }
    }
  }

  def getUsers: List[User] = {
    return users.toList
  }

  def getUser(id: String): Option[User] = {
    for (user <- DaoManager.getUsers) {
      if (user.id.equals(id)) {
        return Some(user)
      }
    }

    return None
  }

  def getProjects: List[HarvestProject] = {
    return new HarvestProject(0, "Dummy Project", getTasks(null)) :: Nil
  }

  def getTasks(project: HarvestProject): List[HarvestTask] = {
    return new HarvestTask(0, "Dummy Task", true) :: Nil
  }

  def getCoops: WritableList = {
    return coops
  }

  def getTimes: List[HarvestTime] = {
    //    val c5 = new HarvestTime("4", DaoManager.getUsers.head, new Date, "5 iruzveiru tuewrht urgt erut eurtg utger t uezgt urtgerztg erterzgt zertt", 18000000, DaoManager.getProjects.head, DaoManager.getTasks(DaoManager.getProjects.head).head)
    //    val c4 = new HarvestTime("3", DaoManager.getUsers.head, new Date, "4 iruzveiru tuewrht urgt erut eurtg utger t uezgt urtgerztg erterzgt zertt", 14400000, DaoManager.getProjects.head, DaoManager.getTasks(DaoManager.getProjects.head).head)
    //    val c3 = new HarvestTime("2", DaoManager.getUsers.head, new Date, "3 iruzveiru tuewrht urgt erut eurtg utger t uezgt urtgerztg erterzgt zertt", 10800000, DaoManager.getProjects.head, DaoManager.getTasks(DaoManager.getProjects.head).head)
    //    val c2 = new HarvestTime("1", DaoManager.getUsers.head, new Date, "2 iruzveiru tuewrht urgt erut eurtg utger t uezgt urtgerztg erterzgt zertt", 7200000, DaoManager.getProjects.head, DaoManager.getTasks(DaoManager.getProjects.head).head)
    //    val c1 = new HarvestTime("0", DaoManager.getUsers.head, new Date, "1 iruzveiru tuewrht urgt erut eurtg utger t uezgt urtgerztg erterzgt zertt", 3600000, DaoManager.getProjects.head, DaoManager.getTasks(DaoManager.getProjects.head).head)
    //    return c5 :: c4 :: c3 :: c2 :: c1 :: Nil
    return Nil
  }

  def getAll: List[Coop] = {
    return Nil
  }

  def initialize() {
    loadUsers
    loadCoops
  }

  private def loadUsers() {
    try {

      val usersXML = {
        if (!CoopClient.getInstance.offline) {
          new RestReader("http://coopapp.com/groups/" + CoopClient.getInstance.currentGroup.id + "/users").read.get
        } else {
          <users>
            <user>
              <id>0</id>
              <last-name>Blank</last-name>
              <first-name>Daniela</first-name>
              <email>blank@weiglewilczek.com</email>
              <avatar-thumb-url/>
            </user>
          </users>
        }
      }

      for (user <- (usersXML \ "user")) {
        val userId = user \ "id" text
        val userName = user \ "last-name" text
        val userFirstName = user \ "first-name" text
        val userEmail = user \ "email" text
        val userImageUrl = user \ "avatar-thumb-url" text
        val userImage: Image = loadImage(userImageUrl)

        val currentUser = new User(userId, userEmail, userImage, userName, userFirstName)
        if (CoopClient.getInstance.user equals userEmail) {
          CoopClient.getInstance.currentUser = currentUser
        }

        users.+=(currentUser)
      }
    } catch {
      case ex: Throwable => println(ex)
    }
  }

  def loadImage(file: String): Image =
    {
      if (file != null && file.length > 0) {
        var userImage: Image = null
        var inputStream: InputStream = null
        try {
          val url = new URL(file.replaceAll(" ", "%20"))
          inputStream = url.openStream
          userImage = new Image(Display.getDefault(), inputStream)
        } catch {
          case ex: Throwable => println(ex)
        } finally {
          if (inputStream != null)
            inputStream.close()
        }

        return userImage
      }

      return null
    }

  private def loadCoops() {
    try {
      lastUpdate = new Date()

      val coopXML =
        {
          if (!CoopClient.getInstance.offline) {
            CoopClient.getInstance.resultListEntries match {
              case selected if (selected.equals(ResultListType.Default)) =>
                new RestReader("http://coopapp.com/groups/" + CoopClient.getInstance.currentGroup.id).read.get
              case selected if (selected.equals(ResultListType.Thirty)) =>
                val xmlResult = new RestReader("http://coopapp.com/groups/" + CoopClient.getInstance.currentGroup.id).read.get
                var counter = 0
                var statusList: NodeSeq = (xmlResult \ "status").head
                // TODO turn into method or function and break with return
                for (coopElem <- (xmlResult \ "status")) {
                  counter match {
                    case currentCounter if (currentCounter == 0) =>
                      counter = currentCounter + 1
                    case currentCounter if (currentCounter < 30 && currentCounter > 0) =>
                      statusList = statusList ++ coopElem
                      counter = currentCounter + 1
                    case _ =>
                  }
                }
                <statusList>{ statusList }</statusList>
              case selected if (selected.equals(ResultListType.Today)) =>
                val now = new Date()
                val format = new SimpleDateFormat()
                format.applyPattern("yyyy-MM-dd")
                val today = format.format(now)
                new RestReader("http://coopapp.com/groups/" + CoopClient.getInstance.currentGroup.id + "/" + today).read.get
              case selected if (selected.equals(ResultListType.TwoDays)) =>
                val now = new Date()
                val yesterday = new Date(now.getTime - 1000 * 3600 * 24)
                val format = new SimpleDateFormat()
                format.applyPattern("yyyy-MM-dd")
                val today = format.format(now)
                val secondDay = format.format(yesterday)
                val todayXML: NodeSeq = new RestReader("http://coopapp.com/groups/" + CoopClient.getInstance.currentGroup.id + "/" + today).read.get
                var todayStatusList = todayXML \ "status"

                val yesterdayXML: NodeSeq = new RestReader("http://coopapp.com/groups/" + CoopClient.getInstance.currentGroup.id + "/" + secondDay).read.get

                for (coopElem <- (yesterdayXML \ "status")) {
                  todayStatusList = todayStatusList ++ coopElem
                }
                <statusList>{ todayStatusList }</statusList>
              case selected if (selected.equals(ResultListType.ThreeDays)) =>
                val now = new Date()
                val yesterday = new Date(now.getTime - 1000 * 3600 * 24)
                val dayBeforYesterday = new Date(now.getTime - 1000 * 3600 * 24 * 2)
                val format = new SimpleDateFormat()
                format.applyPattern("yyyy-MM-dd")
                val today = format.format(now)
                val secondDay = format.format(yesterday)
                val thridDay = format.format(dayBeforYesterday)
                val todayXML: NodeSeq = new RestReader("http://coopapp.com/groups/" + CoopClient.getInstance.currentGroup.id + "/" + today).read.get
                var todayStatusList = todayXML \ "status"

                val yesterdayXML: NodeSeq = new RestReader("http://coopapp.com/groups/" + CoopClient.getInstance.currentGroup.id + "/" + secondDay).read.get
                val dayBeforYesterdayXML: NodeSeq = new RestReader("http://coopapp.com/groups/" + CoopClient.getInstance.currentGroup.id + "/" + thridDay).read.get
                for (coopElem <- (yesterdayXML \ "status")) {
                  todayStatusList = todayStatusList ++ coopElem
                }
                for (coopElem <- (dayBeforYesterdayXML \ "status")) {
                  todayStatusList = todayStatusList ++ coopElem
                }
                <statusList>{ todayStatusList }</statusList>
            }
          } else {
            <statusList>
              <status>
                <id>0</id>
                <user><id>0</id></user>
                <text>test element</text>
              </status>
            </statusList>
          }
        }

      for (coopElem <- (coopXML \ "status")) {
        val id = coopElem \ "id" text
        val user = coopElem \ "user" \ "id" text
        val timeString = coopElem \ "updated-at" text
        val text = coopElem \ "text" text

        val time = restStringToDate(timeString)

        val coop = new Coop(id, getUser(user).get, time, text)

        coops.add(coop)
        coopIds.append(coop.id)
      }
    } catch {
      case ex: Throwable => println(ex)
    }

    updateJob.schedule(CoopClient.getInstance.updateIntervall)

    Nil
  }

  private def updateCoops() {
    updateRunning = true

    try {
      val now = new Date()
      val format = new SimpleDateFormat()
      format.applyPattern("yyyy-MM-dd")
      val today = format.format(now)
      val todayDate = format.parse(today)

      val coopXML = {
        if (!CoopClient.getInstance.offline) {
          new RestReader("http://coopapp.com/groups/" + CoopClient.getInstance.currentGroup.id + "/" + today).read.get
        } else {
          <statusList></statusList>
        }
      }

      if (!CoopClient.getInstance.offline) {
        val added = new ListBuffer[Coop]
        val removed = new ListBuffer[Coop]
        val todayCoops = new ListBuffer[String]

        for (coopElem <- (coopXML \ "status")) {
          val id = coopElem \ "id" text
          val user = coopElem \ "user" \ "id" text
          val timeString = coopElem \ "updated-at" text
          val text = coopElem \ "text" text

          val time = restStringToDate(timeString)

          val coop = new Coop(id, getUser(user).get, time, text)
          if (!coopIds.contains(coop.id)) {
            added.append(coop)
            coopIds.append(coop.id)
          } else {
            todayCoops.append(coop.id)
          }
        }

        coops.getRealm().exec(new Runnable() {
          override def run() {
            val coopsIterator = coops.iterator
            while (coopsIterator.hasNext) {
              val coop = coopsIterator.next.asInstanceOf[Coop]
              if (!coop.time.before(todayDate) && !todayCoops.contains(coop.id)) {
                removed.append(coop)
              }
            }

            removed.toList.forall({ deletedElement =>
              coops.remove(deletedElement)
              true
            })

            added.toList.forall({ newElement =>
              coops.add(0, newElement)
              true
            })
          }
        })

        TitleListener.fireUnreadEntriesAdded(added.size)

        if (added.size > 0) {
          // TODO: play audio signal
        }
      }

      lastUpdate = now
    } catch {
      case ex: Throwable => println(ex)
    } finally {
    	updateRunning = false
    }

    Nil
  }

  def dispose() {
    for (user <- DaoManager.getUsers) {
      if (user.image != null)
        user.image.dispose()
    }
  }

  private def restStringToDate(timeString: String): Date =
    {
      var time = new Date()
      if (timeString.length >= 20) {
        val year = timeString.substring(0, 4).toInt
        val month = timeString.substring(5, 7).toInt
        val day = timeString.substring(8, 10).toInt
        val hour = timeString.substring(11, 13).toInt
        val minute = timeString.substring(14, 16).toInt
        val second = timeString.substring(17, 19).toInt

        val timezoneOffset = timeString.substring(19, timeString.length);

        val calendar = new GregorianCalendar()
        //        calendar.setTimeZone(TimeZone.getTimeZone(timezoneOffset))

        if (timeString.length < 25) {
          // 2011-06-07T08:06:13Z
          //TODO test timezoneOffset = "Z"
          calendar.setTimeZone(TimeZone.getTimeZone(timezoneOffset))
        } else if (timeString.length >= 25) {
          // 2011-06-07T08:06:13+02:00
          val calendar = new GregorianCalendar()
          calendar.setTimeZone(TimeZone.getTimeZone("UTC" + timezoneOffset))
        }

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, second)
        time = calendar.getTime
      }

      time
    }

  def addCoop(text: String) {
    val coop = new Coop(null, CoopClient.getInstance.currentUser, new Date(), text)
    Activator.getDefault().getLog().log(new Status(IStatus.INFO, Activator.getDefault().getBundle().getSymbolicName(), 0, "update running: " + updateRunning + "; text: " + text, null))

    if (CoopClient.getInstance.offline) {
      coops.getRealm().exec(new Runnable() {
        override def run() {
          coops.add(0, coop)
        }
      })
    } else {
      updateJob.cancel()
      new RestWriter("http://coopapp.com/statuses/update.xml").write(text)
      updateJob.schedule()
    }

  }

}