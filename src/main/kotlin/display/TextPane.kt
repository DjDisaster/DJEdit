import me.djdisaster.config.Syntax
import me.djdisaster.misc.DirectoryController
import java.awt.*
import java.awt.event.*
import java.util.Vector
import java.util.regex.Pattern
import javax.swing.*
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import javax.swing.undo.UndoManager

class TextPane : JTextPane() {

    var document = styledDocument
    val scrollPane = JScrollPane(this)


    val suggestionsPopup = JPopupMenu()
    val suggestionsList = JList<String>()
    val suggestionScroll = JScrollPane(suggestionsList)

    val undoManager = UndoManager()

    init {
        isOpaque = false
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(false)
        val font = Font("", 0, 24)
        setFont(font)
        caretColor = Color.YELLOW

        suggestionsList.selectionMode = ListSelectionModel.SINGLE_SELECTION
        suggestionsPopup.add(suggestionScroll)
        suggestionScroll.preferredSize = Dimension(1000, 0)
        suggestionsPopup.isFocusable = false
        suggestionsPopup.putClientProperty("JPopupMenu.firePopupMenuCanceledOnDismiss", false)
        suggestionsList.isFocusable = false
        suggestionScroll.isFocusable = false
        suggestionScroll.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_NEVER
        suggestionScroll.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER

        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        scrollPane.isOpaque = false
        scrollPane.viewport.isOpaque = false


        val popupBackgroundColour = Color.DARK_GRAY
        suggestionsPopup.background = popupBackgroundColour
        suggestionsList.background = popupBackgroundColour
        suggestionScroll.background = popupBackgroundColour

        suggestionsList.foreground = Color.WHITE
        suggestionsList.font = Font("SansSerif", 0, 20)

        suggestionsPopup.border = BorderFactory.createEmptyBorder()
        suggestionsList.border = BorderFactory.createEmptyBorder()
        suggestionScroll.border = BorderFactory.createEmptyBorder()





        suggestionsList.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                insertSuggestion()
            }
        })


        addKeyListener(object : KeyAdapter() {
            override fun keyTyped(e: KeyEvent) {
                SwingUtilities.invokeLater {
                    println("update")
                    updateSuggestions()
                }
            }

            override fun keyPressed(e: KeyEvent) {
                if (suggestionsPopup.isVisible) {
                    when (e.keyCode) {
                        KeyEvent.VK_DOWN -> {
                            val index = (suggestionsList.selectedIndex + 1) % suggestionsList.model.size
                            suggestionsList.selectedIndex = index
                        }
                        KeyEvent.VK_UP -> {
                            val index = (suggestionsList.selectedIndex - 1 + suggestionsList.model.size) % suggestionsList.model.size
                            suggestionsList.selectedIndex = index
                        }
                        KeyEvent.VK_ENTER -> {
                            e.consume()
                            insertSuggestion()
                        }
                        KeyEvent.VK_TAB -> {
                            e.consume()
                            insertSuggestion()
                        }
                    }
                }

                if (e.isControlDown) {
                    if (e.keyCode == KeyEvent.VK_Z) {
                        if (undoManager.canUndo()) {
                            undoManager.undo()
                        }
                    } else if (e.keyCode == KeyEvent.VK_Y) {
                        if (undoManager.canRedo()) {
                            undoManager.redo()
                        }
                    }

                }

                SwingUtilities.invokeLater {
                    updateSyntaxHighlighting()
                }
            }
        })

        document.addUndoableEditListener { event ->
            undoManager.addEdit(event.edit)
        }

    }

    private fun getCurrentWord(): String {
        val caretPos = caretPosition
        val text = text.substring(0, caretPos)
        val match = Regex("\\b(\\w+)\$").find(text)
        return match?.value ?: ""
    }

    val infoPopup = JPopupMenu().apply {
        isFocusable = false
        setRequestFocusEnabled(false)
        background = Color.GRAY
        font = Font("", 0, 24)
        border = BorderFactory.createEmptyBorder()
    }



    private fun showInfo(info: String) {
        infoPopup.isVisible = true
        infoPopup.removeAll()

        val formattedInfo = "<html>" + info.replace("\n", "<br>").replace(" ", "&nbsp;") + "</html>"

        val infoLabel = JLabel(formattedInfo).apply {
            foreground = Color.WHITE
            background = Color.DARK_GRAY
            border = BorderFactory.createEmptyBorder()
            font = Font("", Font.PLAIN, 20)
            isOpaque = true
        }

        infoPopup.add(infoLabel)
        val caretRect = modelToView(caretPosition)
        infoPopup.show(this, caretRect.x, caretRect.y + font.size)
    }





    private fun updateSuggestions() {
        infoPopup.isVisible = false
        val currentWord = getCurrentWord()
        if (currentWord.isEmpty()) {
            suggestionsPopup.isVisible = false
            return
        }

        val data = Vector<String>()
        for (function in DirectoryController.loadedFunctions) {
            if (function.functionName.startsWith(currentWord) && function.functionName != currentWord) {
                var append = "${function.functionName}("
                if (function.arguments.size == 0) {
                    append += ")"
                }




                data.add(append)
            }
            var splitWord = currentWord.split("(")

            println("SW: " + splitWord[0])
            if (splitWord[0] == function.functionName) {
                println("pass")
                showInfo(function.regLine + "\n" + function.body.replace("\t", "    "))
                return;
            }

        }

        if (data.isEmpty()) {
            suggestionsPopup.isVisible = false
            return
        }

        suggestionsList.setListData(data)
        suggestionsList.selectedIndex = 0

        val itemHeight = suggestionsList.fixedCellHeight.takeIf { it > 0 } ?: 24
        val maxVisibleItems = 10
        val popupHeight = (data.size.coerceAtMost(maxVisibleItems) * itemHeight) + 5

        suggestionScroll.preferredSize = Dimension(400, popupHeight)
        suggestionScroll.revalidate()
        suggestionScroll.repaint()

        val caretPos = modelToView(caretPosition)
        suggestionsPopup.pack()
        suggestionsPopup.show(this, caretPos.x, caretPos.y + font.size)
    }



    override fun addNotify() {
        super.addNotify()
        SwingUtilities.getWindowAncestor(this)?.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                setSize(e.component.size)
            }
        })
    }

    fun insertSuggestion() {
        val selected = suggestionsList.selectedValue ?: return
        val caretPos = caretPosition
        val text = text.substring(0, caretPos)
        val match = Regex("\\b(\\w+)\$").find(text)

        match?.let {
            document.remove(it.range.first, it.range.last - it.range.first + 1)
            document.insertString(it.range.first, selected, SimpleAttributeSet())
        }

        suggestionsPopup.isVisible = false
    }



    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 1.0f)
        g2.fillRect(0, 0, width, height)
        g2.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)
        super.paintComponent(g)
    }

    fun updateSyntaxHighlighting() {
        val visibleRect = this.visibleRect
        val startOffset = viewToModel(Point(visibleRect.x, visibleRect.y))
        val endOffset = viewToModel(Point(visibleRect.x + visibleRect.width, visibleRect.y + visibleRect.height))

        val visibleLength = endOffset - startOffset
        if (visibleLength <= 0) return
        val visibleText = document.getText(startOffset, visibleLength)

        val redStyle = SimpleAttributeSet().apply {
            StyleConstants.setForeground(this, Color.RED)
        }


        document.setCharacterAttributes(startOffset, visibleLength, SimpleAttributeSet(), true)

        //println("visible: " + visibleText)
        for (entry in Syntax.getMatchPairs()) {
            val regex = entry.regex
            val matches = regex.findAll(text)

            val style = SimpleAttributeSet().apply {
                StyleConstants.setForeground(this, entry.color)
            }

            matches.forEach { match ->
                document.setCharacterAttributes(match.range.first, match.range.last - match.range.first + 1, style, false)
            }
        }

    }




}
