package com.weiglewilczek.flexlist

import org.eclipse.swt.custom.ScrolledComposite
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Link
import org.eclipse.swt.widgets.Canvas
import org.eclipse.swt.widgets.Listener
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Display
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
import scala.Tuple2
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.core.databinding.observable.list.IListChangeListener
import org.eclipse.core.databinding.observable.list.ListChangeEvent
import org.eclipse.core.databinding.observable.list.ListDiffVisitor
import java.util.Iterator
import org.eclipse.ui.PlatformUI
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import java.net.URL

object FlexList {

  def apply[T <: IFlexListElement](parent: Composite, style: Int = SWT.NONE): FlexList[T] =
    {
      val instance = new FlexList[T](parent, style | SWT.H_SCROLL | SWT.V_SCROLL)
      instance.createContent
      instance
    }
}

class FlexList[T <: IFlexListElement](parent: Composite, style: Int) extends ScrolledComposite(parent, style) with IListChangeListener {

  var input: AnyRef = Nil
  val elements = new ListBuffer[Composite]
  var listContent: Composite = null
  var contentProvider: IFlexListContentProvider = null
  var elementCreator: ((Composite, Int, Object) => T) = null

  var space: Int = 0

  val layoutListener = new Listener {
    def handleEvent(event: Event) {
      layout()
    }
  }

  def setInput(input: AnyRef) {
    this.input = input

    this.input match {
      case inputList: WritableList => inputList.addListChangeListener(this)
      case _ =>
    }
    // not performance optimized
    clear
    buildList
  }

  def getInput(): AnyRef = {
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
      getContent().setSize(computeSize)
      super.layout(updateLabel, all)
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
        element match {
          case elem: Composite =>
            elem.dispose()
            elem.isDisposed()
          case _ =>
            true
        }
      })
  }

  private def buildList {
    elementCreator match {
      case creator: ((Composite, Int, Object) => T) =>
        {
          input match {
            case inputList: List[Object] =>

              inputList.forall(inputElement =>
                {
                  addElement(inputElement)
                  true
                })

              update
            case inputList: WritableList =>
              val iterator = inputList.listIterator
              while (iterator.hasNext()) {
                addElement(iterator.next().asInstanceOf[AnyRef])
              }

              update
          }
        }

      case _ =>
    }

    layout()
  }

  override def update {
    try {
      super.update
      contentProvider match {
        case contentProvider: IFlexListContentProvider =>
          elements.forall(element =>
            {
              val subElements = element.asInstanceOf[IFlexListElement].getListChildren()
              var index = 0
              subElements.forall(subElement => 
                {
                  val text = contentProvider.getValue(element.getData("FLEXLIST_DATA"), index)
                  val image = contentProvider.getImage(element.getData("FLEXLIST_DATA"), index)
                  val source = contentProvider.getSource(element.getData("FLEXLIST_DATA"), index)
                  subElement match {
                    case subElem: Label =>
                      if (text != null) {
                        subElem.setText(text.toString)
                      }
                    case subElem: Link =>
                      if (text != null) {
                        subElem.setText(text.toString)
                      }

                      if (text != null && text.asInstanceOf[String].indexOf("<a>") > -1) {

                        val oldSelectionListener = subElem.getData("FLEX_LIST_SELECTION_LISTENER")
                        if (oldSelectionListener != null && oldSelectionListener.isInstanceOf[SelectionAdapter]) {
                          subElem.removeSelectionListener(oldSelectionListener.asInstanceOf[SelectionAdapter])
                        }

                        val selectionListener = new SelectionAdapter() {
                          override def widgetSelected(e: SelectionEvent) {
                            val browserSupport =
                              PlatformUI.getWorkbench().getBrowserSupport()
                            try {
                              val browser = browserSupport.createBrowser(null)
                              browser.openURL(new URL(e.text))
                            } catch {
                              case exception: Throwable => println(exception)
                            }
                          }
                        }

                        subElem.addSelectionListener(selectionListener)
                        subElem.setData("FLEX_LIST_SELECTION_LISTENER", selectionListener)

                      }
                    case subElem: Canvas => subElem.setBackgroundImage(image)
                    case _ =>
                  }
                  index = index + 1

                  true
                })

                element.layout()
                
              true
            })
        case _ =>
      }
    } catch {
      case ex: Throwable =>
        println(ex)
        ex.printStackTrace()
    }
  }

  /**
   * At Element at the End
   * @param inputElement
   */
  protected def addElement(inputElement: AnyRef) {
    addElement(elements.length, inputElement)
  }

  protected def addElement(index: Int, inputElement: AnyRef) {

    val element = elementCreator(listContent, SWT.NONE, inputElement)
    element match {
      case elem: Composite =>
        elem.setData("FLEXLIST_DATA", inputElement)
        addElement(index, inputElement, elem)
        true
      case _ => false
    }
  }

  protected def addElement(index: Int, inputElement: AnyRef, element: Composite) {
    var previousElement: Composite = null
    var nextElement: Composite = null

    if (elements.size > 0 && index >= elements.size) {
      previousElement = elements.last
    } else if (elements.size > 0) {
      if (index > 0) {
        previousElement = elements.apply(index - 1)
      }

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

    update()
    layout(true, true)
  }

  protected def removeElement(index: Int) {
    var previousElement: Composite = null
    var nextElement: Composite = null
    var currentElement: Composite = elements.apply(index)

    if (index >= elements.size) {
      previousElement = elements.last
    } else if (index == 0) {
      nextElement = elements.apply(index + 1)
    } else {
      previousElement = elements.apply(index - 1)
      nextElement = elements.apply(index + 1)
    }

    if (previousElement != null) {
      nextElement.getLayoutData.asInstanceOf[FormData].top = new FormAttachment(previousElement, space, SWT.BOTTOM)
    } else if (nextElement != null) {
      nextElement.getLayoutData.asInstanceOf[FormData].top = new FormAttachment(0, 0)
    }

    elements.remove(index)
    currentElement.setVisible(false)
    currentElement.dispose()

    update()
    layout(true, true)
  }

  protected def moveElement(oldIndex: Int, newIndex: Int, inputElement: Object) {
    val element = elements.apply(oldIndex)
    removeElement(oldIndex)
    addElement(newIndex, inputElement, element)
  }

  def handleListChange(event: ListChangeEvent) {
    event.diff.accept(new ListDiffVisitor() {
      override def handleAdd(index: Int, child: AnyRef) {
        Display.getCurrent().syncExec(new Runnable() {
          override def run() {
            addElement(index, child)
          }
        })

      }

      override def handleRemove(index: Int, child: AnyRef) {
        Display.getCurrent().syncExec(new Runnable() {
          override def run() {
            removeElement(index)
          }
        })
      }

      override def handleReplace(index: Int, oldChild: AnyRef,
        newChild: AnyRef) {
      }

      override def handleMove(oldIndex: Int, newIndex: Int,
        child: AnyRef) {
        Display.getCurrent().syncExec(new Runnable() {
          override def run() {
            moveElement(oldIndex,
              newIndex, child)
          }
        })
      }
    });
  }
}