package com.weiglewilczek.coop.client.ui.login

import java.lang.StringBuffer
import com.weiglewilczek.coop.model.Group
import com.weiglewilczek.coop.client.DaoManager
import com.weiglewilczek.coop.client.RestReader

object LogonManager {
  def login {
    if (CoopClient.getInstance.user != null && CoopClient.getInstance.password != null) {
      readGroup()
      if (CoopClient.getInstance.currentGroup != null) {
        DaoManager.initialize
        
        CoopClient.getInstance.loggedIn = true
      }
    }
  }

  private def readGroup() {
    try {
    	val groupsXML =
    	{
	    	if(!CoopClient.getInstance.offline) {
		    	new RestReader("http://coopapp.com/groups").read().get
		    } else {
		    	<groups type="array">
				  <group>
				    <name>WeigleWilczek</name>
				    <created-at type="datetime">2011-01-20T14:10:13-05:00</created-at>
				    <updated-at type="datetime">2011-01-20T14:10:13-05:00</updated-at>
				    <id type="integer">9395</id>
				    <timezone>Berlin</timezone>
				  </group>
				</groups>
	    	}
    	}

      val groupId = groupsXML \ "group" \ "id" text
      val groupName = groupsXML \ "group" \ "name" text
      val currentGroup = new Group(groupId, groupName)
      CoopClient.getInstance.currentGroup = currentGroup
    } catch {
      case ex: Throwable => println(ex);
    }
  }
}