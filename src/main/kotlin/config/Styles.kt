package me.djdisaster.config

object Styles : Config() {


    override fun getSavePath(): String {
        return "styles.yml"
    }

    override fun options(): HashMap<String, Any> {
        val map = HashMap<String, Any>()

        map["background-colour"] = "black"

        return map
    }


}