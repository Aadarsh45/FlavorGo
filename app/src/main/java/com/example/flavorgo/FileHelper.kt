package com.example.flavorgo

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class FileHelper(private val context: Context) {

    fun createFileAndGetUri(fileName: String, fileContent: String): Uri {
        val imageDir = File(context.filesDir, "images")
        imageDir.mkdirs()

        val file = File(imageDir, fileName)
        FileOutputStream(file).use {
            it.write(fileContent.toByteArray())
        }

        return FileProvider.getUriForFile(context, "com.example.flavorgo.provider", file)
    }
}