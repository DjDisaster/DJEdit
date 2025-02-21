package me.djdisaster

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
