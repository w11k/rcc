package com.weiglewilczek.flexlist

import org.eclipse.swt.custom.ScrolledComposite
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Link
import org.eclipse.swt.widgets.Canvas
import org.eclipse.swt.widgets.Listener
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Color
import scala.collection.immutable.List
import scala.collection.mutable.ListBuffer
import org.eclipse.swt.layout.FormLayout
import org.eclipse.swt.layout.FormData
import org.eclipse.swt.layout.FormAttachment
import org.eclipse.swt.SWT
import java.lang.Class
import java.lang.Math
import scala.Array
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.core.databinding.observable.list.IListChangeListener
import org.eclipse.core.databinding.observable.list.ListChangeEvent
import org.eclipse.core.databinding.observable.list.ListDiffVisitor

object FlexList {

  def apply[T <: IFlexListElement](parent: Composite, style: Int = SWT.NONE): FlexList[T] =
    {
      val instance = new FlexList[T](parent, style | SWT.H_SCROLL | SWT.V_SCROLL)
      instance.createContent
      instance
    }
}

class FlexList[T <: IFlexListElement](parent: Composite, style: Int) extends ScrolledComposite(parent, style) with IListChangeListener {

  var input: List[Object] = Nil
  val elements: ListBuffer[Composite] = new ListBuffer[Composite]
  var listContent: Composite = null
  var contentProvider: IFlexListContentProvider = null
  var elementCreator: ((Composite, Int, Object) => T) = null

  var space: Int = 0

  val layoutListener = new Listener {
    def handleEvent(event: Event) {
      layout()
    }
  }

  def setInput(input: List[Object]) {
    this.input = input

    this.input match {
      case inputList: WritableList => inputList.addListChangeListener(this)
      case _ =>
    }
    // not performance optimized
    clear
    buildList
  }

  def getInput(): List[Object] = {
    return input
  }

  def setContentProvider(contentProvider: IFlexListContentProvider) {
    this.contentProvider = contentProvider
  }

  def setElementCreator(creator: ((Composite, Int, Object) => T)) {
    elementCreator = creator;
  }

  private def createContent {
    setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE))

    listContent = new Composite(this, SWT.NONE)
    setContent(listContent)
    getContent().setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE))
    getContent().asInstanceOf[Composite].setLayout(new FormLayout())

    addListener(SWT.Resize, layoutListener)
  }

  override def layout(updateLabel: Boolean, all: Boolean) {
    if (!computeSize.equals(getContent().getSize())) {
      super.layout(updateLabel, all)
      getContent().setSize(computeSize)
    }
  }

  def computeSize: Point =
    {
      val barWidth = getVerticalBar().getSize().x

      val width = getSize().x - barWidth
      return getContent().computeSize(Math.max(-1, width), -1, true)
    }

  def clear {
    elements.forall(element =>
      {
        element match
          {
            case elem: Composite =>
              elem.dispose()
              return elem.isDisposed()
            case _ =>
              return true;
          }
      })
  }

  private def buildList {
    elementCreator match {
      case creator: ((Composite, Int, Object) => T) =>
        {
        input.forall(inputElement =>
          {
            addElement(inputElement)
            true
          })

        update
      }

      case _ =>
    }

    layout()
  }

  override def update {
    super.update
    contentProvider match {
      case contentProvider: IFlexListContentProvider =>
        elements.forall(element =>
          {
            val subElements = element.asInstanceOf[IFlexListElement].getListChildren()
            var index = 0
            subElements.forall(subElement =>
              {
                val text = contentProvider.getValue(element.getData("DATA"), index)
                val image = contentProvider.getImage(element.getData("DATA"), index)
                subElement match {
                  case subElem: Label => subElem.setText(text.toString)
                  case subElem: Link => subElem.setText(text.toString)
                  case subElem: Canvas => subElem.setBackgroundImage(image)
                  case _ =>
                }
                index = index + 1

                true
              })

            true
          })
      case _ =>
    }
  }

  /**
   * At Element at the End
   * @param inputElement
   */
  protected def addElement(inputElement: Object) {
    addElement(elements.length, inputElement)
  }

  protected def addElement(index: Int, inputElement: Object) {

    val element = elementCreator(listContent, SWT.NONE, inputElement)
    element match {
      case elem: Composite =>
        elem.setData("DATA", inputElement)
        addElement(index, inputElement, elem)
        true
      case _ => false
    }
  }

  protected def addElement(index: Int, inputElement: Object, element: Composite) {
    var previousElement: Composite = null
    var nextElement: Composite = null

    if (elements.size > 0 && index >= elements.size) {
      previousElement = elements.last
    } else if(elements.size > 0) {
      previousElement = elements.apply(index - 1)
      nextElement = elements.apply(index)
    }

    val fd = new FormData()
    if (previousElement != null) {
      fd.top = new FormAttachment(previousElement, space, SWT.BOTTOM)
    } else {
      fd.top = new FormAttachment(0, 0)
    }
    fd.left = new FormAttachment(0, 0)
    fd.right = new FormAttachment(100, 0)
    element.setLayoutData(fd)

    if (nextElement != null) {
      nextElement.getLayoutData.asInstanceOf[FormData].top = new FormAttachment(element, space, SWT.BOTTOM)
    }

    if (index < elements.length) {
      elements.insertAll(index, element :: Nil)
    } else {
    	elements.append(element)
    }
  }

  protected def removeElement(index: Int) {
    var previousElement: Composite = null
    var nextElement: Composite = null

    if (index >= elements.size) {
      previousElement = elements.last
    } else {
      previousElement = elements.apply(index - 1)
      nextElement = elements.apply(index + 1)
    }

    if (nextElement != null && previousElement != null) {
      nextElement.getLayoutData.asInstanceOf[FormData].top = new FormAttachment(previousElement, space, SWT.BOTTOM)
    }

    elements.drop(index)
  }

  protected def moveElement(oldIndex: Int, newIndex: Int, inputElement: Object) {
    val element = elements.apply(oldIndex)
    removeElement(oldIndex)
    addElement(newIndex, inputElement, element)
  }

  def handleListChange(event: ListChangeEvent) {
    event.diff.accept(new ListDiffVisitor() {
      override def handleAdd(index: Int, child: AnyRef) {
        addElement(index, child);
      }

      override def handleRemove(index: Int, child: AnyRef) {
        removeElement(index);
      }

      override def handleReplace(index: Int, oldChild: AnyRef,
        newChild: AnyRef) {
      }

      override def handleMove(oldIndex: Int, newIndex: Int,
        child: AnyRef) {
        moveElement(oldIndex,
          newIndex, child);
      }
    });
  }
}