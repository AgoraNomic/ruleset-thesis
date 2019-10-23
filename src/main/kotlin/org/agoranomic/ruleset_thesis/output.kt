package org.agoranomic.ruleset_thesis

import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

private fun createDirectoryIfNotExists(path: Path) {
    Files.createDirectories(path)
}

private fun createFileIfNotExists(path: Path) {
    try {
        createDirectoryIfNotExists(path.normalize().parent)
        Files.createFile(path)
    } catch (e: FileAlreadyExistsException) {
    }
}

private fun doOutput(filename: String, text: String) {
    val path = Path.of("out", filename)
    createFileIfNotExists(path)
    Files.writeString(path, text, StandardOpenOption.TRUNCATE_EXISTING)
}

fun outputText(filename: String, text: String) = doOutput(filename + ".txt", text)
fun outputGraph(filename: String, text: String) = doOutput(filename + ".gv", text)