package me.djdisaster.misc

import java.awt.Color
import java.util.*
import kotlin.collections.HashMap

var appName = "DJEdit"
val username: String = System.getProperty("user.name")
var knownOperatingSystems = listOf(
    "linux",
    "windows"
)
var operatingSystem = operatingSystemName()
var operatingSystemSavePaths: HashMap<String, String> = genOperatingSystemPathMap()
var operatingSystemSavePath = operatingSystemSavePaths[operatingSystem]



fun genOperatingSystemPathMap(): HashMap<String, String> {
    val map = HashMap<String, String>()
    map["linux"] = "~/.config/$appName"
    map["windows"] = "C:\\Users\\$username\\AppData\\Roaming\\$appName\\"

    return map
}



fun operatingSystemName(): String {

    val operatingSystem = System.getProperty("os.name").lowercase()

    if (knownOperatingSystems.contains(operatingSystem)) {
        return operatingSystem
    }
    return "windows"
}


var colors: HashMap<String, Color> = getColorLookupMap()

private fun getColorLookupMap(): HashMap<String, Color> {
    var map = HashMap<String, Color>()
    return map
}


fun String.toColor(): Color {
    try {
        val clazz = Color::class.javaObjectType
        val field = clazz.getField(this.uppercase(Locale.getDefault()))
        return field.get(null) as Color
    } catch (ignored: Exception) { }

    val hexPattern = "#[0-9A-Fa-f]{6,8}".toRegex()
    if (this.matches(hexPattern)) {
        return if (this.length == 9) {
            val alpha = Integer.valueOf(this.substring(1, 3), 16)
            val red = Integer.valueOf(this.substring(3, 5), 16)
            val green = Integer.valueOf(this.substring(5, 7), 16)
            val blue = Integer.valueOf(this.substring(7, 9), 16)
            Color(red, green, blue, alpha)
        } else {
            Color.decode(this)
        }
    }

    return Color.PINK
}

fun String.getIndentation(): Int {
    var point = 0
    while (point < this.length && this[point] == '\t') {
        point++
    }
    return point
}



