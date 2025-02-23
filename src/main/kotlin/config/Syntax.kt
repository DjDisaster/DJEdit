package me.djdisaster.config

import me.djdisaster.misc.toColor
import java.awt.Color

object Syntax : Config() {

    override fun getSavePath(): String = "syntax.yml"

    override fun options(): HashMap<String, String> {
        return hashMapOf(
            "enabled" to "functions",
            "functions-regex" to "(function [A-z|0-9]+\\(([A-z|0-9]+\\: [A-z|0-9]+, )+[A-z|0-9]+\\: [A-z|0-9]+\\)|function [A-z|0-9]+\\([A-z|0-9]+\\: [A-z|0-9]+\\)|function [A-z|0-9]+\\(\\))( ?:: ?[A-z]+ ?)?:",
            "functions-colour" to "yellow"
        )
    }

    fun getMatchPairs(): List<MatchPair> {
        val options = getOptions()
        return options["enabled"]!!.split(",").mapNotNull { entry ->
            options["${entry}-regex"]?.let { regexPattern ->
                MatchPair(Regex(regexPattern), options["${entry}-colour"]!!.toColor())
            }
        }
    }
}

class MatchPair(val regex: Regex, val color: Color)
