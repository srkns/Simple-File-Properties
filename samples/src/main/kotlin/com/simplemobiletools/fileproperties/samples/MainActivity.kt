package com.simplemobiletools.fileproperties.samples

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.simplemobiletools.filepicker.dialogs.FilePickerDialog
import com.simplemobiletools.filepicker.extensions.hasStoragePermission
import com.simplemobiletools.fileproperties.dialogs.PropertiesDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val STORAGE_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        select_file_button.setOnClickListener {
            if (hasStoragePermission())
                showFilePicker()
            else
                requestStoragePermission()
        }
    }

    private fun showFilePicker() {
        FilePickerDialog(this, listener = object : FilePickerDialog.OnFilePickerListener {
            override fun onFail(error: FilePickerDialog.FilePickerResult) {

            }

            override fun onSuccess(pickedPath: String) {
                PropertiesDialog(this@MainActivity, pickedPath)
            }
        })
    }

    private fun requestStoragePermission() = ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showFilePicker()
        }
    }
}
