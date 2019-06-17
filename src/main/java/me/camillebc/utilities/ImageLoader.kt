package me.camillebc.utilities

import android.graphics.BitmapFactory
import android.os.Build
import android.widget.ImageView
import java.io.File
import java.lang.Exception
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption

class ImageLoader(
    private val imageUrl: String,
    private val file: File,
    private val imageView: ImageView
) : Runnable {
    override fun run() {
        if (!file.exists()) saveUrlToFile(imageUrl, file)
        displayImage(file, imageView)
    }

    private fun displayImage(file: File, imageView: ImageView) {
        val bitmap = BitmapFactory.decodeFile(file.path)
        imageView.setImageBitmap(bitmap)
    }

    private fun saveUrlToFile(imageUrl: String, file: File) {
        val inputStream = URL(imageUrl).openStream() ?: throw Exception("Could not open image stream")
        val readableByteChannel = Channels.newChannel(inputStream) ?: throw Exception("Could not create image channel")
        val fileChannel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            FileChannel.open(
                file.toPath(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            ) ?: throw Exception("Could not open image channel")
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE)
        fileChannel.close()
    }
}