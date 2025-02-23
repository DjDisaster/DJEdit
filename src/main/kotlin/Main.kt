package me.djdisaster

import me.djdisaster.config.genConfigs
import me.djdisaster.display.RootPane
import me.djdisaster.misc.DirectoryController
import java.awt.GraphicsEnvironment


fun main() {
    genConfigs()
    RootPane
    DirectoryController.setDirectory("/media/code/Skript/DJEditor")
}