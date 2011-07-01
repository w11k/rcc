package com.weiglewilczek.coop.client.ui.login

import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Text
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.FormLayout
import org.eclipse.swt.layout.FormData
import org.eclipse.swt.layout.FormAttachment
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.graphics.FontData
import org.eclipse.swt.graphics.Font
import org.eclipse.swt.SWT
import org.eclipse.jface.dialogs.Dialog
import org.eclipse.jface.databinding.swt.SWTObservables
import org.eclipse.jface.dialogs.IDialogConstants
import org.eclipse.core.databinding.observable.value.WritableValue
import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.core.databinding.beans.PojoObservables
import java.lang.System
import com.weiglewilczek.coop.client.ui.Messages

object LoginDialog {

  val RESULT_CANCEL = -1
  val RESULT_LOGIN = 0
  def apply(display: Display): LoginDialog =
    {
      val shell = new Shell(display, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM)
      val instance = new LoginDialog(shell)
      return instance
    }
}

class LoginDialog(shell: Shell) extends Dialog(shell: Shell) {

  var result: Int = LoginDialog.RESULT_CANCEL

  val dbc = new DataBindingContext()
  val coopClientValue = new WritableValue
  coopClientValue.setValue(CoopClient.getInstance)

  override protected def createDialogArea(parent: Composite): Control = {
    val content = new Composite(parent, SWT.NONE)
    content.setBackground(content.getDisplay().getSystemColor(SWT.COLOR_BLUE))
    val gd = new GridData(SWT.FILL, SWT.FILL, true, true)
    gd.widthHint = 350
    gd.heightHint = 200
    content.setLayoutData(gd)
    content.setLayout(new FormLayout())

    val coop = new Label(content, SWT.NONE)
    coop.setForeground(coop.getDisplay().getSystemColor(SWT.COLOR_WHITE))
    val fontData = coop.getFont.getFontData
    fontData.head.height = 24
    coop.setFont(new Font(coop.getDisplay(), fontData))

    coop.setText("COOP")
    val coopData = new FormData()
    coopData.top = new FormAttachment(15, 0)
    coopData.left = new FormAttachment(40, 0)
    coop.setLayoutData(coopData)

    val nameLabel = new Label(content, SWT.NONE)
    nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_WHITE))
    nameLabel.setText(Messages.login + ":")
    val nameLabelData = new FormData()
    nameLabelData.top = new FormAttachment(45, 0)
    nameLabelData.left = new FormAttachment(10, 0)
    nameLabel.setLayoutData(nameLabelData)

    val passwordLabel = new Label(content, SWT.NONE)
    passwordLabel.setForeground(passwordLabel.getDisplay().getSystemColor(SWT.COLOR_WHITE))
    passwordLabel.setText(Messages.password + ":")
    val passwordLabelData = new FormData()
    passwordLabelData.top = new FormAttachment(nameLabel, 15, SWT.BOTTOM)
    passwordLabelData.left = new FormAttachment(10, 0)
    passwordLabel.setLayoutData(passwordLabelData)

    val name = new Text(content, SWT.BORDER)
    val nameData = new FormData()
    nameData.top = new FormAttachment(45, 0)
    nameData.left = new FormAttachment(passwordLabel, 5, SWT.RIGHT)
    nameData.right = new FormAttachment(90, 0)
    name.setLayoutData(nameData)

    dbc.bindValue(SWTObservables.observeText(name, SWT.Modify), PojoObservables.observeDetailValue(coopClientValue, "user", "".getClass))

    val password = new Text(content, SWT.PASSWORD | SWT.BORDER)
    val passwordData = new FormData()
    passwordData.top = new FormAttachment(nameLabel, 15, SWT.BOTTOM)
    passwordData.left = new FormAttachment(passwordLabel, 5, SWT.RIGHT)
    passwordData.right = new FormAttachment(90, 0)
    password.setLayoutData(passwordData)

    dbc.bindValue(SWTObservables.observeText(password, SWT.Modify), PojoObservables.observeDetailValue(coopClientValue, "password",
      "".getClass))

    val remember = new Button(content, SWT.CHECK)
    remember.setForeground(remember.getDisplay().getSystemColor(SWT.COLOR_WHITE))
    remember.setText(Messages.rememberMyLogin)
    val rememberData = new FormData()
    rememberData.top = new FormAttachment(passwordLabel, 15, SWT.BOTTOM)
    rememberData.left = new FormAttachment(passwordLabel, 5, SWT.RIGHT)
    remember.setLayoutData(rememberData)

    dbc.bindValue(SWTObservables.observeSelection(remember), PojoObservables.observeDetailValue(coopClientValue, "remember",
      Boolean.getClass))

    dbc.updateTargets()

    return content
  }

  override protected def createButtonBar(parent: Composite): Control = {
    val composite = super.createButtonBar(parent)
    parent.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_BLUE))
    composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_BLUE))

    val ok = getButton(IDialogConstants.OK_ID)
    ok.setText(Messages.login)
    ok.addSelectionListener(new SelectionAdapter() {
      override def widgetSelected(e: SelectionEvent) {
        result = LoginDialog.RESULT_LOGIN
        close()
      }
    })

    val cancel = getButton(IDialogConstants.CANCEL_ID)
    cancel.addSelectionListener(new SelectionAdapter() {
      override def widgetSelected(e: SelectionEvent) {
        close()
      }
    })

    return composite;
  }
}