package com.example.rssreader

import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity(){
    var curFragment : Fragment? = null
    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        image =  findViewById(R.id.spinner)
        Glide.with(this)
            .asGif()
            .load(R.drawable.fidget)
            .into(image)

        var key = "VIEWER"
        if (savedInstanceState != null) {
            curFragment = supportFragmentManager.getFragment(savedInstanceState, "READER")
            if (curFragment == null) {
                curFragment = supportFragmentManager.getFragment(savedInstanceState, "VIEWER")
            } else
                key = "READER"
        } else curFragment = RSSFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_root, curFragment!!, key).commit()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    val webView = findViewById<WebView>(R.id.webView)
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        curFragment = supportFragmentManager.findFragmentByTag("VIEWER")
                        supportFragmentManager.popBackStack()
                    }
                } else {
                    finish()
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val key = if (supportFragmentManager.findFragmentByTag("READER") != null) "READER"
        else "VIEWER"
        supportFragmentManager.putFragment(outState, key, curFragment!!)
    }
}