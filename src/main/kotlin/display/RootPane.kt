package me.djdisaster.display

import TextPane
import me.djdisaster.config.Styles
import me.djdisaster.toColor
import java.awt.*
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

object RootPane {

    private val frame = JFrame()
    private val textFrame = TextPane()
    init {

        frame.isUndecorated = true
        frame.background = Styles.getOption("background-colour").toColor()

        val dimensions = Toolkit.getDefaultToolkit().screenSize
        frame.size = dimensions

        frame.contentPane = object : JPanel() {
            override fun isOpaque(): Boolean = false
        }

        frame.add(textFrame)
        textFrame.background = Color(0,0,0,0)
        textFrame.preferredSize = dimensions
        textFrame.margin = Insets(20,20,0,0)
        textFrame.foreground = Color.PINK



        frame.isVisible = true
    }
}
