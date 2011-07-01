package com.weiglewilczek.coop.client

import scala.xml.Elem
import com.meterware.httpunit.WebConversation
import com.meterware.httpunit.PostMethodWebRequest
import scala.xml.XML
import java.io.ByteArrayInputStream
import com.weiglewilczek.coop.client.ui.login.CoopClient
import java.net.URL
import java.net.URLConnection
import java.net.HttpURLConnection
import org.apache.commons.codec.binary.Base64

class RestWriter(url: String) {
  // TODO exception handling show on ui
  def write(text: String) {
    try {
      
      val body = new ByteArrayInputStream(("<status>" + text + "</status>").getBytes())

      val wc = new WebConversation()
      wc.setAuthorization(CoopClient.getInstance.user, CoopClient.getInstance.password)

      // TODO: test
      val request = new PostMethodWebRequest(url)
//      val request = new PostMethodWebRequest(url, body, "text/xml")

      request.setParameter("group_id", CoopClient.getInstance.currentGroup.id)
      request.setParameter("status", text)

      val response = wc.getResource(request)

      if (response.getResponseCode() != 201) {
        throw new Exception("HTTP Response code " + response.getResponseCode() + ": " + response.getResponseMessage())
      }
      
      body.close()
    } catch {
      case ex: Throwable => println(ex)
    }

  }
}