package me.djdisaster.config

import me.djdisaster.misc.toColor
import java.awt.Color

object Syntax : Config() {

    override fun getSavePath(): String = "syntax.yml"

    override fun options(): HashMap<String, String> {
        return hashMapOf(
            "enabled" to "functions",
            "functions-regex" to "function [A-Za-z]+\\(([A-Za-z0-9]+: [A-Za-z0-9]+(, [A-Za-z0-9]+: [A-Za-z0-9]+)*)?\\)",
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
