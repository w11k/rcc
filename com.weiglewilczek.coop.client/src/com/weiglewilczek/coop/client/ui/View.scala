package com.weiglewilczek.coop.client.ui

import org.eclipse.swt.graphics.Image
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Text
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.TabFolder
import org.eclipse.swt.widgets.TabItem
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.SWT
import org.eclipse.jface.viewers.ComboViewer
import org.eclipse.ui.part.ViewPart
import org.eclipse.ui.IViewSite
import org.eclipse.ui.IMemento
import org.eclipse.ui.IWorkbenchPartSite
import org.eclipse.ui.IPropertyListener
import com.weiglewilczek.flexlist.FlexList
import com.weiglewilczek.coop.model.Coop
import com.weiglewilczek.coop.model.HarvestTime
import com.weiglewilczek.coop.client.DaoManager

object View extends View {
  val VIEW_ID = "com.weiglewilczek.coop.client.ui.View"
}

class View extends ViewPart {

  var allText: Text = null
  var allButton: Button = null
  var allList: FlexList[Element] = null

  var coopText: Text = null
  var coopButton: Button = null
  var coopList: FlexList[CoopElement] = null

  var timeText: Text = null
  var timeProject: ComboViewer = null
  var timeTask: ComboViewer = null
  var timeButton: Button = null
  var timeList: FlexList[HarvestTimeElement] = null

  def createPartControl(parent: Composite): Unit = {
    val area = new Composite(parent, SWT.NONE)
    area setLayout new FillLayout

    val tabbedPane = new TabFolder(area, SWT.NONE)

    // all
    val allTab = new TabItem(tabbedPane, SWT.NONE)
    allTab setText "All"
    val allTabContent = new Composite(tabbedPane, SWT.NONE)
    allTabContent setLayout new GridLayout

    allText = new Text(allTabContent, SWT.BORDER | SWT.MULTI)
    var textLayout = new GridData(SWT.FILL, SWT.FILL, true, false)
    textLayout.heightHint = 30
    allText setLayoutData textLayout

    allButton = new Button(allTabContent, SWT.PUSH)
    allButton setText "Update"
    var buttonLayout = new GridData(SWT.RIGHT, SWT.TOP, false, false)
    allButton setLayoutData buttonLayout

    allList = FlexList[Element](allTabContent, SWT.NONE)
    var listLayout = new GridData(SWT.FILL, SWT.FILL, true, true)
    allList setLayoutData listLayout

    allList.setElementCreator(((parent: Composite, style: Int, inputElement: Object) =>
      {
        inputElement match {
          case time: HarvestTime => new HarvestTimeElement(parent)
          case coop: Coop => new CoopElement(parent)
          case _ => null
        }
      }))
    allList.setContentProvider(new CoopContentProvider())
    allList.setInput(DaoManager.getAll)

    allTab setControl allTabContent

    // coop
    val coopTab = new TabItem(tabbedPane, SWT.NONE)
    coopTab setText "Coop"
    val coopTabContent = new Composite(tabbedPane, SWT.NONE)
    coopTabContent setLayout new GridLayout

    coopText = new Text(coopTabContent, SWT.BORDER | SWT.MULTI)
    textLayout = new GridData(SWT.FILL, SWT.FILL, true, false)
    textLayout.heightHint = 30
    coopText setLayoutData textLayout

    coopButton = new Button(coopTabContent, SWT.PUSH)
    coopButton setText "Update"
    buttonLayout = new GridData(SWT.RIGHT, SWT.TOP, false, false)
    coopButton setLayoutData buttonLayout

    coopList = FlexList[CoopElement](coopTabContent, SWT.NONE)
    var coopListLayout = new GridData(SWT.FILL, SWT.FILL, true, true)
    coopList setLayoutData coopListLayout

    coopList.setElementCreator(((parent: Composite, style: Int, inputElement: Object) => new CoopElement(parent)))
    coopList.setContentProvider(new CoopContentProvider())
    coopList.setInput(DaoManager.getCoops)

    coopTab setControl coopTabContent

    // time
    val timeTab = new TabItem(tabbedPane, SWT.NONE)
    timeTab setText "Time Tracking"
    val timeTabContent = new Composite(tabbedPane, SWT.NONE)
    timeTabContent setLayout new GridLayout(3, false)

    timeText = new Text(timeTabContent, SWT.BORDER | SWT.MULTI)
    textLayout = new GridData(SWT.FILL, SWT.FILL, true, false)
    textLayout.heightHint = 30
    textLayout.horizontalSpan = 3
    timeText setLayoutData textLayout

    timeProject = new ComboViewer(timeTabContent, SWT.BORDER | SWT.READ_ONLY | SWT.DROP_DOWN)
    val projectLayout = new GridData(SWT.RIGHT, SWT.TOP, false, false)
    projectLayout.widthHint = 140
    timeProject.getCombo setLayoutData projectLayout

    val projects = new Array[String](1)
    projects.update(0, "Choose a Project")
    timeProject.getCombo setItems (projects)
    timeProject.getCombo().select(0)

    timeTask = new ComboViewer(timeTabContent, SWT.BORDER | SWT.READ_ONLY | SWT.DROP_DOWN)
    val taskLayout = new GridData(SWT.RIGHT, SWT.TOP, false, false)
    taskLayout.widthHint = 140
    timeTask.getCombo setLayoutData taskLayout

    val tasks = new Array[String](1)
    tasks.update(0, "Choose a Task")
    timeTask.getCombo setItems (tasks)
    timeTask.getCombo().select(0)

    timeButton = new Button(timeTabContent, SWT.PUSH)
    timeButton setText "Save"
    buttonLayout = new GridData(SWT.RIGHT, SWT.TOP, false, false)
    timeButton setLayoutData buttonLayout

    timeList = FlexList[HarvestTimeElement](timeTabContent, SWT.NONE)
    var timeListLayout = new GridData(SWT.FILL, SWT.FILL, true, true)
    timeListLayout.horizontalSpan = 3
    timeList setLayoutData timeListLayout

    timeList.setElementCreator(((parent: Composite, style: Int, inputElement: Object) => new HarvestTimeElement(parent)))
    timeList.setContentProvider(new CoopContentProvider())
    timeList.setInput(DaoManager.getTimes)

    timeTab setControl timeTabContent
  }

  def setFocus(): Unit = {
    allText.setFocus()
    allList.layout()
  }
}