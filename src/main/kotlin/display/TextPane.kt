import me.djdisaster.config.Syntax
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.regex.Pattern
import javax.swing.JTextPane
import javax.swing.RepaintManager
import javax.swing.SwingUtilities
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants

class TextPane : JTextPane() {

    var document = styledDocument

    init {
        isOpaque = false
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(false)
        val font = Font("", 0, 24)
        setFont(font)
        caretColor = Color.YELLOW


        addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent) {
                println("Key Typed: ${e.keyChar}")
            }

            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_TAB) {
                    e.consume()
                    document.insertString(caretPosition, "\t", null)
                } else if (e.keyCode == KeyEvent.VK_ENTER) {
                    e.consume()
                    document.insertString(caretPosition, "\n", null)
                }

                SwingUtilities.invokeLater {
                    updateVisibleRedHighlighting()
                }
            }

            override fun keyReleased(e: KeyEvent) {
                println("Key Released: ${KeyEvent.getKeyText(e.keyCode)}")

            }
        })
    }

    override fun addNotify() {
        super.addNotify()
        SwingUtilities.getWindowAncestor(this)?.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                setSize(e.component.size)
            }
        })
    }


    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 1.0f)
        g2.fillRect(0, 0, width, height)
        g2.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)
        super.paintComponent(g)
    }

    fun updateVisibleRedHighlighting() {
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

        println("visible: " + visibleText)
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
