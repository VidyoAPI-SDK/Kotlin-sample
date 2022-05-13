package com.vidyo.vidyoconnector.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object Zip {
    suspend fun compress(input: File, output: File) = withContext(Dispatchers.Main) {
        output.delete()
        ZipOutputStream(output.outputStream().buffered()).use { stream ->
            write(stream, root = "", file = input)
        }
    }

    private fun write(stream: ZipOutputStream, root: String, file: File) {
        when {
            file.isFile -> writeFile(stream, root, file)
            file.isDirectory -> writeFolder(stream, root, file)
        }
    }

    private fun writeFile(stream: ZipOutputStream, root: String, file: File) {
        val entry = ZipEntry(root + file.name).also {
            it.time = file.lastModified()
        }

        stream.putNextEntry(entry)
        file.inputStream().copyTo(stream)
    }

    private fun writeFolder(stream: ZipOutputStream, root: String, file: File) {
        val newRoot = root + file.name + "/"
        for (child in file.listFiles() ?: emptyArray()) {
            write(stream, newRoot, child)
        }
    }
}
