package com.simplemobiletools.fileproperties.extensions

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import java.io.File
import java.util.*

fun File.getDuration(): String {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    val timeInMs = java.lang.Long.parseLong(time)
    return getFormattedDuration((timeInMs / 1000).toInt())
}

fun File.getArtist(): String? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
}

fun File.getAlbum(): String? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
}

fun File.getVideoResolution(): String {
    try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH).toInt()
        val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT).toInt()
        return "$width x $height ${getMPx(width, height)}"
    } catch (ignored: Exception) {

    }
    return ""
}

fun File.getImageResolution(): String {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)
    val width = options.outWidth
    val height = options.outHeight
    return "$width x $height ${getMPx(width, height)}"
}

fun getMPx(width: Int, height: Int): String {
    val px = width * height / 1000000.toFloat()
    val rounded = Math.round(px * 10) / 10.toFloat()
    return "(${rounded}MP)"
}

private fun getFormattedDuration(duration: Int): String {
    val sb = StringBuilder(8)
    val hours = duration / (60 * 60)
    val minutes = duration % (60 * 60) / 60
    val seconds = duration % (60 * 60) % 60

    if (duration > 3600) {
        sb.append(String.format(Locale.getDefault(), "%02d", hours)).append(":")
    }

    sb.append(String.format(Locale.getDefault(), "%02d", minutes))
    sb.append(":").append(String.format(Locale.getDefault(), "%02d", seconds))
    return sb.toString()
}
