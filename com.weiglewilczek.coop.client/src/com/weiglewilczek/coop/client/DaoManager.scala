package com.weiglewilczek.coop.client

import com.weiglewilczek.coop.model.User
import com.weiglewilczek.coop.model.Coop
import com.weiglewilczek.coop.model.HarvestTime
import com.weiglewilczek.coop.model.HarvestTask
import com.weiglewilczek.coop.model.HarvestProject
import java.util.Date

object DaoManager {

  def getUsers: List[User] = {
    return new User(0, null, "User", "Dummy") :: Nil
  }

  def getProjects: List[HarvestProject] = {
    return new HarvestProject(0, "Dummy Project", getTasks(null)) :: Nil
  }

  def getTasks(project: HarvestProject): List[HarvestTask] = {
    return new HarvestTask(0, "Dummy Task", true) :: Nil
  }

  def getCoops: List[Coop] = {
    val c5 = new Coop(4, DaoManager.getUsers.head, new Date, "5 iruzveiru tuewrht urgt erut eurtg utger t uezgt urtgerztg erterzgt zertt")
    val c4 = new Coop(3, DaoManager.getUsers.head, new Date, "4 iruzveiru tuewrht urgt erut eurtg utger t uezgt urtgerztg erterzgt zertt")
    val c3 = new Coop(2, DaoManager.getUsers.head, new Date, "3 iruzveiru tuewrht urgt erut eurtg utger t uezgt urtgerztg erterzgt zertt")
    val c2 = new Coop(1, DaoManager.getUsers.head, new Date, "2 iruzveiru tuewrht urgt erut eurtg utger t uezgt urtgerztg erterzgt zertt")
    val c1 = new Coop(0, DaoManager.getUsers.head, new Date, "1 iruzveiru tuewrht urgt erut eurtg utger t uezgt urtgerztg erterzgt zertt")
    return c5 :: c4 :: c3 :: c2 :: c1 :: Nil
  }

  def getTimes: List[HarvestTime] = {
    val c5 = new HarvestTime(4, DaoManager.getUsers.head, new Date, "5 iruzveiru tuewrht urgt erut eurtg utger t uezgt urtgerztg erterzgt zertt", 18000000, DaoManager.getProjects.head, DaoManager.getTasks(DaoManager.getProjects.head).head)
    val c4 = new HarvestTime(3, DaoManager.getUsers.head, new Date, "4 iruzveiru tuewrht urgt erut eurtg utger t uezgt urtgerztg erterzgt zertt", 14400000, DaoManager.getProjects.head, DaoManager.getTasks(DaoManager.getProjects.head).head)
    val c3 = new HarvestTime(2, DaoManager.getUsers.head, new Date, "3 iruzveiru tuewrht urgt erut eurtg utger t uezgt urtgerztg erterzgt zertt", 10800000, DaoManager.getProjects.head, DaoManager.getTasks(DaoManager.getProjects.head).head)
    val c2 = new HarvestTime(1, DaoManager.getUsers.head, new Date, "2 iruzveiru tuewrht urgt erut eurtg utger t uezgt urtgerztg erterzgt zertt", 7200000, DaoManager.getProjects.head, DaoManager.getTasks(DaoManager.getProjects.head).head)
    val c1 = new HarvestTime(0, DaoManager.getUsers.head, new Date, "1 iruzveiru tuewrht urgt erut eurtg utger t uezgt urtgerztg erterzgt zertt", 3600000, DaoManager.getProjects.head, DaoManager.getTasks(DaoManager.getProjects.head).head)
    return c5 :: c4 :: c3 :: c2 :: c1 :: Nil
  }

  def getAll: List[Coop] = {
    return getCoops ::: getTimes
  }
}