package me.djdisaster

import me.djdisaster.config.genConfigs
import me.djdisaster.display.RootPane
import me.djdisaster.misc.DirectoryController
import java.awt.GraphicsEnvironment


fun main() {
    genConfigs()
    RootPane

    DirectoryController.setDirectory("/media/code/Skript/DJEditor")

    println("To Know the available font family names")
    val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()

    println("Getting the font family names")


    // Array of all the fonts available in AWT
    val fonts = ge.availableFontFamilyNames


    // Getting the font family names
    for (i in fonts) {
        println("$i ")
    }
}