package com.example.applinkstest

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class AppLinksActivity : AppCompatActivity() {
    private val TAG = "AppLinksActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_links)
        findViewById<Button>(R.id.button).setOnClickListener {

            val i = Intent(Intent.ACTION_VIEW, Uri.parse("kite://handshake?api_key=uf8cguv719djhxfc"))
            val pkgManager = applicationContext.packageManager
            val packages = pkgManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            Log.d(TAG, "onCreate: packages: ${packages}")
            startActivity(i)
        }
    }
}