package me.djdisaster.misc

import java.io.File


object DirectoryController {

    var recursionLimit = 5

    var currentDirectory = "none"
    var loadedFunctions = ArrayList<LoadedFunction>()
    var loadedFiles = ArrayList<String>()


    fun setDirectory(dir: String) {
        currentDirectory = dir
        recache()
    }

    fun recache() {
        loadedFunctions.clear()
        loadedFiles.clear()

        recursiveLoad(currentDirectory, 0)

        for (function in loadedFunctions) {
            println("---------")
            println("Name: ${function.functionName}")
            println("Returns: ${function.returnType}")
            for (arg in function.arguments) {
                println(arg.name + ": " + arg.type)
            }
            println("Body:\n" + function.body)
            println("----------")
        }

    }

    fun recursiveLoad(dir: String, depth: Int) {
        val newDepth = depth+1
        if (newDepth > recursionLimit) {
            return
        }
        File(dir).listFiles()?.forEach {
            if (it.isDirectory) {
                recursiveLoad(it.toString(), newDepth)
            } else {
                parseFile(it.toString())
            }
        }
    }


    fun parseFile(path: String) {
        println("loading: ${path}")
        val lines = File(path).readLines()
        val iterator = lines.listIterator()

        while (iterator.hasNext()) {
            val line = iterator.next()
            val lowerLine = line.lowercase()
            if (lowerLine.startsWith("function")) {
                loadedFunctions.add(parseFunction(iterator, line))



            }
        }
    }
}

// Quality variable naming going on here!
fun parseFunction(it: ListIterator<String>, line: String): LoadedFunction {
    val split = line.split("(", limit = 2)
    val name = split[0].replaceFirst("function ", "")
    val split2 = split[1].split(")", limit=2)


    val arguments = ArrayList<FunctionArgument>()
    for (v in split2[0].split(", ?".toRegex())) {
        if (v == "") {
            continue
        }
        val split3 = v.split(": ?".toRegex()).toMutableList()
        arguments.add(FunctionArgument(split3[0], split3[1]))
    }
    var returnType = "none"
    if (split2[1].contains("::")) {
        returnType = split2[1].replace(":", "").replace(" ", "")
    }

    var body = ""
    var eof = true
    while (it.hasNext()) {
        val value = it.next()
        if (value.getIndentation() == 0) {
            eof = false
            break
        } else {
            body += value + "\n"
        }
    }
    if (body.contains("\n")) {
        body = body.substring(0,body.length-1)
    }

    if (!eof) {
        it.previous()
    }

    return LoadedFunction(name, arguments, returnType, body)
}


class LoadedFunction(var functionName: String, var arguments: List<FunctionArgument>, var returnType: String, var body: String)
class FunctionArgument(var name: String, var type: String)
