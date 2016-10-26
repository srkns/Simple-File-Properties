package com.simplemobiletools.fileproperties.dialogs

import android.app.Activity
import android.content.res.Resources
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import com.simplemobiletools.fileproperties.R
import com.simplemobiletools.fileproperties.extensions.*
import kotlinx.android.synthetic.main.smtpr_item_properties.view.*
import kotlinx.android.synthetic.main.smtpr_property_item.view.*
import java.io.File
import java.util.*

class PropertiesDialog() {
    lateinit var mInflater: LayoutInflater
    lateinit var mPropertyView: ViewGroup
    lateinit var mResources: Resources

    private var mCountHiddenItems = false
    private var mFilesCnt = 0

    /**
     * A File Properties dialog constructor with an optional parameter, usable at 1 file selected
     *
     * @param activity use the activity instead of any context to avoid some Theme.AppCompat issues
     * @param path the file path
     * @param countHiddenItems toggle determining if we will count hidden files themselves and their sizes (reasonable only at directory properties)
     */
    constructor(activity: Activity, path: String, countHiddenItems: Boolean = false) : this() {
        mCountHiddenItems = countHiddenItems
        mInflater = LayoutInflater.from(activity)
        mResources = activity.resources
        mPropertyView = mInflater.inflate(R.layout.smtpr_item_properties, null) as ViewGroup

        val file = File(path)
        addProperty(R.string.smtpr_name, file.name)
        addProperty(R.string.smtpr_path, file.parent)
        addProperty(R.string.smtpr_size, getItemSize(file).formatSize())
        addProperty(R.string.smtpr_last_modified, file.lastModified().formatLastModified())

        if (file.isDirectory) {
            addProperty(R.string.smtpr_files_count, mFilesCnt.toString())
        } else if (file.isImage()) {
            addProperty(R.string.smtpr_resolution, file.getImageResolution())
        } else if (file.isAudio()) {
            addProperty(R.string.smtpr_duration, file.getDuration())
            addProperty(R.string.smtpr_artist, file.getArtist())
            addProperty(R.string.smtpr_album, file.getAlbum())
        } else if (file.isVideo()) {
            addProperty(R.string.smtpr_duration, file.getDuration())
            addProperty(R.string.smtpr_resolution, file.getVideoResolution())
            addProperty(R.string.smtpr_artist, file.getArtist())
            addProperty(R.string.smtpr_album, file.getAlbum())
        }

        AlertDialog.Builder(activity)
                .setTitle(mResources.getString(R.string.smtpr_properties))
                .setView(mPropertyView)
                .setPositiveButton(R.string.smtpr_ok, null)
                .create()
                .show()
    }

    /**
     * A File Properties dialog constructor with an optional parameter, usable at multiple items selected
     *
     * @param activity use the activity instead of any context to avoid some Theme.AppCompat issues
     * @param path the file path
     * @param countHiddenItems toggle determining if we will count hidden files themselves and their sizes
     */
    constructor(activity: Activity, paths: List<String>, countHiddenItems: Boolean = false) : this() {
        mCountHiddenItems = countHiddenItems
        mInflater = LayoutInflater.from(activity)
        mResources = activity.resources
        mPropertyView = mInflater.inflate(R.layout.smtpr_item_properties, null) as ViewGroup

        val files = ArrayList<File>(paths.size)
        paths.forEach { files.add(File(it)) }

        addProperty(R.string.smtpr_path, files[0].parent)
        addProperty(R.string.smtpr_size, getItemsSize(files).formatSize())
        addProperty(R.string.smtpr_files_count, mFilesCnt.toString())

        AlertDialog.Builder(activity)
                .setTitle(mResources.getString(R.string.smtpr_properties))
                .setView(mPropertyView)
                .setPositiveButton(R.string.smtpr_ok, null)
                .create()
                .show()
    }

    private fun addProperty(labelId: Int, value: String?) {
        if (value == null)
            return

        val view = mInflater.inflate(R.layout.smtpr_property_item, mPropertyView, false)
        view.property_label.text = mResources.getString(labelId)
        view.property_value.text = value
        mPropertyView.properties_holder.addView(view)
    }

    private fun getItemsSize(files: ArrayList<File>): Long {
        var size = 0L
        files.forEach { size += getItemSize(it) }
        return size
    }

    private fun getItemSize(file: File): Long {
        if (file.isDirectory) {
            return getDirectorySize(File(file.path))
        }

        mFilesCnt++
        return file.length()
    }

    private fun getDirectorySize(dir: File): Long {
        var size = 0L
        if (dir.exists()) {
            val files = dir.listFiles()
            for (i in files.indices) {
                if (files[i].isDirectory) {
                    size += getDirectorySize(files[i])
                } else if (!files[i].isHidden && !dir.isHidden || mCountHiddenItems) {
                    mFilesCnt++
                    size += files[i].length()
                }
            }
        }
        return size
    }
}
