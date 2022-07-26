package com.example.applinkstest

import android.content.ComponentName
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.*
import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private var mCustomTabsSession: CustomTabsSession? = null
    private var mCustomTabsServiceConnection: CustomTabsServiceConnection? = null

    val fragment1 = LoadingFragment()
    val fragment2 = FallbackFragment()

    var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn2 = findViewById<Button>(R.id.button2)
        val btn3 = findViewById<Button>(R.id.button3)
        val btn4 = findViewById<Button>(R.id.button4)
        val switchFragBtn = findViewById<Button>(R.id.switch_frag_btn)
        val txt = findViewById<TextView>(R.id.textView2)
        val txt3 = findViewById<TextView>(R.id.textView3)

        mCustomTabsServiceConnection = CustomTabServiceConnectionController()
        CustomTabsClient.bindCustomTabsService(this, "com.android.chrome", mCustomTabsServiceConnection!!)

        btn2.setOnClickListener {
            if (doesPackageExist("com.zerodha.kite3")) {
                txt.text = "TRUE"
                return@setOnClickListener
            }
            txt.text = "FALSE"
        }
        btn3.setOnClickListener {
            val granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val permission = android.Manifest.permission.QUERY_ALL_PACKAGES;
                val res = checkCallingOrSelfPermission(permission)
                res == PackageManager.PERMISSION_GRANTED
            } else true
            txt3.text = if (granted) "TRUE" else "FALSE"
        }
        btn4.setOnClickListener {
            val url = findViewById<EditText>(R.id.editTextTextPersonName).text.toString()
            val builder = CustomTabsIntent.Builder(mCustomTabsSession)
            val customTabsIntent = builder.build()
            try {
                customTabsIntent.launchUrl(this, Uri.parse("https://"+url))
            } catch (e: Exception) {
                Log.d(TAG, "onCreate: launch url -> $url exception $e")
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        switchFragBtn.setOnClickListener {
            currentFragment = if (currentFragment == fragment1) fragment2 else fragment1
            supportFragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, currentFragment ?: fragment1).commit()
        }
    }

    override fun onDestroy() {
        mCustomTabsServiceConnection?.let { unbindService(it) }
        super.onDestroy()
    }

    fun doesPackageExist(pkgName: String): Boolean {
        return try {
            val pkgInfo = packageManager.getPackageInfo(pkgName, 0)
            Log.d(TAG, "doesPackageExist: $pkgInfo")
            true
        } catch (e: Exception) {
            Log.d(TAG, "doesPackageExist: EXCEPTION-> $e")
            false
        }
    }

    inner class CustomTabServiceConnectionController : CustomTabsServiceConnection() {

        override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
            mCustomTabsSession = client.newSession(object : CustomTabsCallback() {
                override fun onPostMessage(message: String, extras: Bundle?) {
                    Log.d(TAG, "onPostMessage: $message")
                    super.onPostMessage(message, extras)
                }

                override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
                    super.onNavigationEvent(navigationEvent, extras)

                    Log.e("MainActivity", "on navigation event: $navigationEvent")
                }
            })

        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            p0?.toShortString()
            Log.e(TAG, "on Service Disconnected: ${p0?.toShortString()}")
        }
    }
}