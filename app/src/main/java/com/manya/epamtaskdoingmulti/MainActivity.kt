package com.manya.epamtaskdoingmulti

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import android.net.ConnectivityManager
import android.support.v7.app.AlertDialog


/**
 *
 * [MainActivity] wuth [Button] and [ImageView]. Download button downloaded image and display it on imageView.
 * Downloading process working at the new async thread.
 *
 * @author Maria Kirdun
 */


class MainActivity : AppCompatActivity() {

    var isDownloaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!isOnline(this)){
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("No Internet")
                .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
            val alert = builder.create()
            alert.show()
        }

        if (savedInstanceState != null) {
            isDownloaded = savedInstanceState.getBoolean(IS_DOWNLOADED)
            if (isDownloaded) {
                DownloadAsyncTask().execute(IMAGE_URL)
            }
        }

        downloadButton.setOnClickListener {
            DownloadAsyncTask().execute(IMAGE_URL)
            isDownloaded = true
        }

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putBoolean(IS_DOWNLOADED,isDownloaded)
    }

    inner class DownloadAsyncTask() : AsyncTask<String, Int, Bitmap>() {

        private var progress = 0

        override fun onPreExecute() {
            updateProgress(DownloadService.PROGRESS_MIN)
        }

        override fun doInBackground(vararg imgUrl: String?): Bitmap? {
            return (Glide.with(this@MainActivity)
                .asBitmap()
                .load(imgUrl[0])
                .submit()
                    ).get()
        }

        override fun onPostExecute(result: Bitmap?) {
            updateProgress(DownloadService.PROGRESS_MAX)
            imgImageView.setImageBitmap(result)
            stopService(Intent(this@MainActivity, DownloadService::class.java))
        }

        private fun updateProgress(progress: Int) {
            val intent = Intent(this@MainActivity, DownloadService::class.java)
            intent.putExtra(DownloadService.PROGRESS, progress)
            startService(intent)
        }

    }

    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    companion object {
        private const val IMAGE_URL: String = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/db/106_Black_Vulture_or_Carrion_Crow.jpg/4096px-106_Black_Vulture_or_Carrion_Crow.jpg"
        private const val IS_DOWNLOADED = "isDownloaded"
    }
}
