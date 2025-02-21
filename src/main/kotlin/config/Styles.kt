package me.djdisaster.config

class Styles : Config {


    override fun getSavePath(): String {
        return "styles.yml"
    }

    override fun options(): HashMap<String, Any> {
        var map = HashMap<String, Any>()

        map.put("background-colour", "black")

        return map
    }


}