package me.djdisaster.display

import java.awt.Toolkit
import javax.swing.JFrame


object RootPane {

    var frame = JFrame()

    init {
        val dimensions = Toolkit.getDefaultToolkit().screenSize
        frame.size = dimensions
    }

}