package com.weiglewilczek.coop.client.ui.login

import com.weiglewilczek.coop.model.User
import com.weiglewilczek.coop.client.Activator
import org.osgi.service.prefs.BackingStoreException
import org.eclipse.core.runtime.preferences.IEclipsePreferences
import org.eclipse.ui.preferences.ScopedPreferenceStore

object CoopClient {
  private val PREFERENCE_USERNAME = "username"
  private val PREFERENCE_PASSWORD = "password"
  private val PREFERENCE_REMEMBER = "remember"

  // prefs are stored in
  // .metadata/.plugins/org.eclipse.core.runtime/.settings/com.weiglewilczek.coop.client.prefs
  private val preferenceStore = Activator.getDefault()
    .getPreferenceStore();

  private def commitPreferenceStore() {
    val nodes = preferenceStore.asInstanceOf[ScopedPreferenceStore]
      .getPreferenceNodes(false);

    nodes.forall(eclipsePreferences => {
      if (eclipsePreferences.name().equals(
        Activator.getDefault().getBundle().getSymbolicName())) {
        eclipsePreferences.flush();
      }
      true
    })
  }

  val getInstance: CoopClient = {
    val instance = new CoopClient
    val storeName = preferenceStore.getString(PREFERENCE_USERNAME)
    if (storeName != null && storeName.length > 0) {
      instance.user = storeName
    }

    val storePassword = preferenceStore.getString(PREFERENCE_PASSWORD)
    if (storePassword != null && storePassword.length > 0) {
      instance.password = storePassword
    }

    val storeRemember = preferenceStore.getBoolean(PREFERENCE_REMEMBER)
    if (storeRemember) {
      instance.remember = storeRemember
    }

    instance
  }

  def storeUserNamePassword {
    if (getInstance.remember) {
      preferenceStore.setValue(PREFERENCE_USERNAME, getInstance.user)
      preferenceStore.setValue(PREFERENCE_PASSWORD, getInstance.password)
      preferenceStore.setValue(PREFERENCE_REMEMBER, getInstance.remember)

      commitPreferenceStore
    }
  }
}

class CoopClient {
  var user: String = null
  var password: String = null
  var remember: Boolean = false

  var loggedIn: Boolean = false

  var currentUser: User = null

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
}