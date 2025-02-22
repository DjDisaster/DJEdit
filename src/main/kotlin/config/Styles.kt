package me.djdisaster.config

object Styles : Config() {


    override fun getSavePath(): String {
        return "styles.yml"
    }

    override fun options(): HashMap<String, String> {
        val map = HashMap<String, String>()

        map["background-colour"] = "black"
        map["border-colour"] = "#00000000"
        map["border-top"] = "1"
        map["border-left"] = "1"
        map["border-right"] = "1"
        map["border-bottom"] = "1"


        return map
    }


}