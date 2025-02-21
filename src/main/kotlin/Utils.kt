package me.djdisaster

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
    map["linux"] = "~/.config/${appName}"
    map["windows"] = "C:\\Users\\${username}\\AppData\\Roaming\\${appName}\\"

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
        val field = clazz.getField(this)
        return field.get(null) as Color
    } catch (ignored: Exception) {

    }


    if (this == "#[0-9|a-f]{1,6}") {
        return Color.decode(this)
    }

    return Color.PINK

}
