package com.weiglewilczek.coop.client.ui.preferences

import org.eclipse.jface.dialogs.TitleAreaDialog
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Text
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.graphics.Point
import org.eclipse.jface.viewers.ComboViewer
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.layout.GridData
import org.eclipse.jface.layout.GridLayoutFactory
import org.eclipse.swt.SWT
import org.eclipse.core.databinding.observable.value.WritableValue
import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.core.databinding.beans.PojoObservables
import com.weiglewilczek.coop.client.ui.login.CoopClient
import com.weiglewilczek.coop.client.ui.login.ResultListType
import org.eclipse.jface.databinding.swt.SWTObservables
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import com.weiglewilczek.coop.client.ui.login.ResultListType
import com.weiglewilczek.coop.client.ui.Messages

class PreferenceDialog(shell: Shell) extends TitleAreaDialog(shell) {

  val dbc = new DataBindingContext()
  val coopClientValue = new WritableValue
  coopClientValue.setValue(CoopClient.getInstance)

  override protected def createDialogArea(parent: Composite): Control = {
    // create the top level composite for the dialog area
    val dialogArea = super.createDialogArea(parent).asInstanceOf[Composite]
    val workingArea = new Composite(dialogArea, SWT.NONE)
    workingArea.setLayout(new GridLayout(2, false))

    val rememberLabel = new Label(workingArea, SWT.NONE)
    rememberLabel.setText(Messages.rememberLoginAndAutoLogIn + ":")

    val remember = new Button(workingArea, SWT.CHECK)

    dbc.bindValue(SWTObservables.observeSelection(remember), PojoObservables.observeDetailValue(coopClientValue, "remember",
      Boolean.getClass))

    val updateIntervalLabel = new Label(workingArea, SWT.NONE)
    updateIntervalLabel.setText(Messages.updateInterval + ":")

    val updateInterval = new Text(workingArea, SWT.BORDER)
    val intervalGd = new GridData()
    intervalGd.widthHint = 70
    updateInterval.setLayoutData(intervalGd)

    dbc.bindValue(SWTObservables.observeText(updateInterval, SWT.Modify), PojoObservables.observeDetailValue(coopClientValue, "updateIntervall",
      Long.getClass))

    val resultListEntriesLabel = new Label(workingArea, SWT.NONE)
    resultListEntriesLabel.setText(Messages.shownAmountOfCoopEntries + ":")
    val gd = new GridData()
    gd.verticalAlignment = SWT.TOP
    resultListEntriesLabel.setLayoutData(gd)

    val resultListEntries = new Composite(workingArea, SWT.NONE)
    resultListEntries.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create)

    class ResultListEntriesSelectionListener extends SelectionAdapter {
      override def widgetSelected(e: SelectionEvent) {
        e.item.getData match {
          case selectedResultListEntryType: ResultListType.Value => CoopClient.getInstance.resultListEntries = selectedResultListEntryType
          case _ => CoopClient.getInstance.resultListEntries = ResultListType.Default
        }
      }
    }

    val resultListEntries30 = new Button(resultListEntries, SWT.RADIO)
    resultListEntries30.setText(Messages.thirtyEntries)
    resultListEntries30.setData(ResultListType.Thirty)
    val resultListEntriesDefault = new Button(resultListEntries, SWT.RADIO)
    resultListEntriesDefault.setText(Messages.defaultEntries)
    resultListEntriesDefault.setData(ResultListType.Default)
    val resultListEntriesToday = new Button(resultListEntries, SWT.RADIO)
    resultListEntriesToday.setText(Messages.today)
    resultListEntriesToday.setData(ResultListType.Today)
    val resultListEntries2Days = new Button(resultListEntries, SWT.RADIO)
    resultListEntries2Days.setText(Messages.twoDays)
    resultListEntries2Days.setData(ResultListType.TwoDays)
    val resultListEntries3Days = new Button(resultListEntries, SWT.RADIO)
    resultListEntries3Days.setText(Messages.threeDays)
    resultListEntries3Days.setData(ResultListType.ThreeDays)
    val gridData = new GridData()
    gridData.horizontalSpan = 2
    resultListEntriesDefault.setLayoutData(gridData)

    val radios = resultListEntries30 :: resultListEntriesToday :: resultListEntries2Days :: resultListEntries3Days :: resultListEntriesDefault :: Nil
    radios.forall({ radio =>
      if (CoopClient.getInstance.resultListEntries.equals(radio.getData)) {
        radio.setSelection(true)
      } else {
        radio.setSelection(false)
      }

      true
    })

    return dialogArea
  }
  
  // TODO save on ok!

  override protected def getInitialSize(): Point = {
    val shellSize = super.getInitialSize();
    new Point(shellSize.x,
      Math.max(convertVerticalDLUsToPixels(300),
        shellSize.y));
  }
}