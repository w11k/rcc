package com.weiglewilczek.coop.client.ui

import org.eclipse.ui.IPageLayout
import org.eclipse.ui.IPerspectiveFactory

class Perspective extends IPerspectiveFactory {

	def createInitialLayout(layout:IPageLayout) {
		layout.setEditorAreaVisible(false)
		layout.addStandaloneView("com.weiglewilczek.coop.client.ui.View", false, IPageLayout.TOP,
				IPageLayout.RATIO_MAX, IPageLayout.ID_EDITOR_AREA)

	}
}