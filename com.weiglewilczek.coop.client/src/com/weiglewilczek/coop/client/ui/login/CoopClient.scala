package com.weiglewilczek.coop.client.ui.login

import com.weiglewilczek.coop.model.User
import com.weiglewilczek.coop.model.Group
import com.weiglewilczek.coop.client.Activator
import org.osgi.service.prefs.BackingStoreException
import org.eclipse.core.runtime.preferences.IEclipsePreferences
import org.eclipse.ui.preferences.ScopedPreferenceStore
import scala.Long
import org.eclipse.equinox.security.storage.SecurePreferencesFactory
import org.eclipse.equinox.internal.security.storage.SecurePreferencesWrapper

object CoopClient {
  private val PREFERENCE_USERNAME = "username"
  private val PREFERENCE_PASSWORD = "coop_password"
  private val PREFERENCE_REMEMBER = "remember"
  private val PREFERENCE_OFFLINE = "offline"
  private val PREFERENCE_UPDATE_INTERVALL = "intervall"
  private val PREFERENCE_RESULT_LIST_ENTRIES = "resultEntries"

  // prefs are stored in
  // .metadata/.plugins/org.eclipse.core.runtime/.settings/com.weiglewilczek.coop.client.prefs
  private val preferenceStore = Activator.getDefault()
    .getPreferenceStore()
  private val securedPreferenceStore = SecurePreferencesFactory.getDefault()

  private def commitPreferenceStore() {
    val nodes = preferenceStore.asInstanceOf[ScopedPreferenceStore]
      .getPreferenceNodes(false)

    nodes.forall(eclipsePreferences => {
      if (eclipsePreferences.name().equals(
        Activator.getDefault().getBundle().getSymbolicName())) {
        eclipsePreferences.flush()
      }
      true
    })
  }

  private def commitSecuredPreferenceStore() {
    securedPreferenceStore.asInstanceOf[SecurePreferencesWrapper].flush()
  }

  val getInstance: CoopClient = {
    val instance = new CoopClient
    val storeName = preferenceStore.getString(PREFERENCE_USERNAME)
    if (storeName != null && storeName.length > 0) {
      instance.user = storeName
    }

    try {
      val storePassword = securedPreferenceStore.get(PREFERENCE_PASSWORD, null /*default*/ )
      if (storePassword != null && storePassword.length > 0) {
        instance.password = storePassword
      }
    } catch {
      case e: Throwable => println(e)
    }

    val storeRemember = preferenceStore.getBoolean(PREFERENCE_REMEMBER)
    if (storeRemember) {
      instance.remember = storeRemember
    }

    val storeOffline = preferenceStore.getBoolean(PREFERENCE_OFFLINE)
    if (storeOffline) {
      instance.offline = storeOffline
    }

    val storeUpdateIntervall = preferenceStore.getLong(PREFERENCE_UPDATE_INTERVALL)
    if (storeUpdateIntervall > 0) {
      instance.updateIntervall = storeUpdateIntervall
    }

    val storeResultListEntries = preferenceStore.getString(PREFERENCE_RESULT_LIST_ENTRIES)
    if (storeResultListEntries != null && storeResultListEntries.length > 0) {
      instance.resultListEntries = ResultListType.withName(storeResultListEntries)
    }

    instance
  }

  def storeUserNamePassword {
    if (getInstance.remember) {
      preferenceStore.setValue(PREFERENCE_USERNAME, getInstance.user)
      preferenceStore.setValue(PREFERENCE_REMEMBER, getInstance.remember)
      commitPreferenceStore

      securedPreferenceStore.put(PREFERENCE_PASSWORD, getInstance.password, true)
      commitSecuredPreferenceStore
    }
  }

  def storePreferences {
    preferenceStore.setValue(PREFERENCE_REMEMBER, getInstance.remember)

    preferenceStore.setValue(PREFERENCE_UPDATE_INTERVALL, getInstance.updateIntervall)
    preferenceStore.setValue(PREFERENCE_RESULT_LIST_ENTRIES, getInstance.resultListEntries.toString)

    commitPreferenceStore
  }
  def restorePreferences {
    val storeRemember = preferenceStore.getBoolean(PREFERENCE_REMEMBER)
    if (storeRemember) {
      getInstance.remember = storeRemember
    }

    val storeUpdateIntervall = preferenceStore.getLong(PREFERENCE_UPDATE_INTERVALL)
    if (storeUpdateIntervall > 0) {
      getInstance.updateIntervall = storeUpdateIntervall
    }

    val storeResultListEntries = preferenceStore.getString(PREFERENCE_RESULT_LIST_ENTRIES)
    if (storeResultListEntries != null && storeResultListEntries.length > 0) {
      getInstance.resultListEntries = ResultListType.withName(storeResultListEntries)
    }
  }
}

class CoopClient {
  var user: String = null
  var password: String = null
  var remember: Boolean = false
  var offline: Boolean = false

  var updateIntervall: Long = 10000
  var resultListEntries: ResultListType.Value = ResultListType.Default

  var loggedIn: Boolean = false

  var currentUser: User = null
  var currentGroup: Group = null

  def getUser: String =
    {
      user
    }

  def setUser(user: String) {
    this.user = user
  }

  def getPassword: String =
    {
      password
    }

  def setPassword(password: String) {
    this.password = password
  }

  def getRemember: Boolean =
    {
      remember
    }

  def setRemember(remember: Boolean) {
    this.remember = remember
  }

  def getOffline: Boolean =
    {
      offline
    }

  def getUpdateIntervall: String =
    {
      updateIntervall.toString
    }

  def setUpdateIntervall(updateIntervall: String) {
    this.updateIntervall = updateIntervall.toInt
  }

  def getResultListEntries: ResultListType.Value =
    {
      resultListEntries
    }

  def setResultListEntries(resultListEntries: ResultListType.Value) {
    this.resultListEntries = resultListEntries
  }
}

object ResultListType extends Enumeration {
  type ResultListType = Value
  val Thirty = Value("thirty")
  val Today = Value("today")
  val TwoDays = Value("two_days")
  val ThreeDays = Value("three_days")
  val Default = Value("default")
}