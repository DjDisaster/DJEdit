package me.djdisaster.config

object Styles : Config() {


    override fun getSavePath(): String {
        return "styles.yml"
    }

    override fun options(): HashMap<String, String> {
        val map = HashMap<String, String>()

        map["background-colour"] = "black"
        map["border-colour"] = "#00000000"

        return map
    }


}