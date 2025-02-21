package me.djdisaster.display

import me.djdisaster.config.Styles
import me.djdisaster.toColor
import java.awt.Color
import java.awt.Dimension
import java.awt.Toolkit
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.swing.JFrame
import javax.swing.SwingUtilities


object RootPane {

    private var frame = JFrame()
    private var contentFrame = frame.contentPane
    var graphics = frame.graphics

    init {

        val dimensions = Toolkit.getDefaultToolkit().screenSize
        println("color: ${Styles.getOption("background-colour")}")
        contentFrame.background = Styles.getOption("background-colour").toColor()

        frame.size = dimensions
        frame.isVisible = true



    }

}