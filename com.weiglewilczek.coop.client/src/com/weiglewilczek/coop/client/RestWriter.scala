package com.weiglewilczek.coop.client

import scala.xml.Elem
import com.meterware.httpunit.WebConversation
import com.meterware.httpunit.PostMethodWebRequest
import scala.xml.XML
import java.io.InputStreamReader
import java.io.InputStream
import java.lang.StringBuffer
import java.io.BufferedReader
import com.weiglewilczek.coop.client.ui.login.CoopClient
import java.net.URL
import java.net.URLConnection
import java.net.HttpURLConnection
import org.apache.commons.codec.binary.Base64

class RestWriter(url: String) {
  // TODO exception handling show on ui
	// TODO implement, bis jetzt nur eine kopie vom reader
  def write(): Option[Elem] =
    {
      try {
        val wc = new WebConversation()
        wc.setAuthorization(CoopClient.getInstance.user,
          CoopClient.getInstance.password)
        val request = new PostMethodWebRequest(url)
        request.setHeaderField("Accept", "application/xml")
        request.setHeaderField("Content-Type", "application/xml")
        val response = wc.getResource(request)

        if (response.getResponseCode() < 200 || response.getResponseCode() >= 300) {
          throw new Exception("HTTP Response code " + response.getResponseCode() + ": " + response.getResponseMessage())
        }

        val is = response.getInputStream
        val isr = new InputStreamReader(is)
        val in = new BufferedReader(isr)
        var inputLine: String = null

        var input: StringBuffer = new StringBuffer

        while ({ inputLine = in.readLine(); inputLine != null }) {
          input.append(new String(inputLine) + "\n\r")
        }

        in.close
        isr.close

        val xml = XML.loadString(input.toString)
        assert(xml.isInstanceOf[scala.xml.Elem])

        return Some(xml)
      } catch {
        case ex: Throwable => println(ex)
      }

      return None
    }
}