package com.weiglewilczek.coop.client.ui.login

import java.net.URL
import java.net.URLConnection
import java.io.BufferedReader
import java.io.InputStreamReader
import org.eclipse.core.internal.preferences.Base64
import java.lang.StringBuffer

object LogonManager {
  def login {
//    val user = new URL("http://coopapp.com/groups/9395/users/17790") // ich
//    val day = new URL("http://coopapp.com/groups/9395/2011-02-28") // tag
//    val yc = day.openConnection()
//
//    // TODO: direkt so in CoopClient ablegen und auch so in den prefs speichern
//    val authentication = new String(Base64.encode((CoopClient.getInstance.user + ":" + CoopClient.getInstance.password).getBytes())).replaceAll("\n\r", "")
//    yc.setRequestProperty("Authorization", authentication)
//    yc.setRequestProperty("Content-Type", "application/xml")
//    yc.setRequestProperty("Content-Accept", "application/xml")
//
//    val in = new BufferedReader(
//      new InputStreamReader(
//        yc.getInputStream()))
//    var inputLine:String = null
//    
//    var input:StringBuffer = new StringBuffer
//
//    while ((inputLine = in.readLine) != null)
//    {
//    	input.append(new String(inputLine) + "\n\r")
//    }
//
//    in.close()

    CoopClient.getInstance.loggedIn = true
  }

}