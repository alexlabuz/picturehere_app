package com.example.picture_here_app.activity.service

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class GetFile {
    companion object{
        fun getFile(uri: Uri, context: Context): File{
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            try {
                val file = File(context.cacheDir, "cacheFileAppeal.srl")
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(4 * 1024) // or other buffer size
                    var read: Int
                    while (inputStream!!.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                    }
                    output.flush()
                    return file
                }
            } finally {
                inputStream!!.close()
            }
        }
    }
}