package me.djdisaster.config

import me.djdisaster.operatingSystemSavePath
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.charset.Charset
import kotlin.io.path.Path
import kotlin.io.path.readLines




var configs = listOf(
    Styles
)

fun genConfigs() {
    configs.forEach { it.generate() }
}

// (Always re-invent the wheel, but make sure it's worse!)
abstract class Config {

    lateinit var loadedOptions: HashMap<String, String>;

    abstract fun getSavePath(): String
    abstract fun options(): HashMap<String, Any>


    fun getFullSavePath(): String {
        val expandedPath = if (operatingSystemSavePath!!.startsWith("~")) {
            operatingSystemSavePath!!.replaceFirst("~", System.getProperty("user.home"))
        } else {
            operatingSystemSavePath
        }
        return File(expandedPath, getSavePath()).absolutePath
    }



    fun write(str: String) {

        val writer = BufferedWriter(FileWriter(getFullSavePath()))
        writer.write(str)
        writer.close()
    }

    fun read(): List<String> {
        return Path(getFullSavePath()).readLines(Charset.defaultCharset())
    }



    fun getOptions(): HashMap<String, String> {
        val map = HashMap<String, String>()
        for (line in read()) {
            val parsedLine = line.replaceFirst(": ", ":")
            val split = parsedLine.split(":")

            if (split.size < 2) {
                continue
            }

            map[split[0]] = split[1]

        }

        return map
    }

    fun getOption(option: String): String {
        return loadedOptions[option]!!
    }


    fun generate() {
        val file = File(getFullSavePath())

        println("saves at: ${getFullSavePath()}")

        println(file.absolutePath)

        if (file.exists()) {
            loadedOptions = getOptions()
            return
        }

        file.parentFile?.mkdirs()

        var write = ""
        for (option in options().entries) {
            write += "${option.key}: ${option.value}\n"
            println("+")
        }

        println("saves at: ${getFullSavePath()}")
        println("text: " + write)

        file.createNewFile()
        val bw = BufferedWriter(FileWriter(file))
        bw.write(write)
        bw.close()

        loadedOptions = getOptions()

    }

}