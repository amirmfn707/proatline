package com.example.webviewapp

import android.os.Bundle
import android.view.Menu
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private val menuUrl = "https://proatline.com/wp-json/wp-api-menus/v2/menus/webview"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        webView = findViewById(R.id.webview)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("https://proatline.com")

        loadMenuFromAPI()

        navView.setNavigationItemSelectedListener { item ->
            val url = item.intent?.getStringExtra("url")
            if (!url.isNullOrEmpty()) {
                webView.loadUrl(url)
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                Toast.makeText(this, "صفحه پیدا نشد", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun loadMenuFromAPI() {
        val queue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(Request.Method.GET, menuUrl, null,
            { response ->
                val items: JSONArray = response.getJSONArray("items")
                val menu: Menu = navView.menu
                menu.clear()
                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val title = item.getString("title")
                    val url = item.getString("url")
                    val menuItem = menu.add(Menu.NONE, i, Menu.NONE, title)
                    menuItem.intent = android.content.Intent().putExtra("url", url)
                }
            },
            { error ->
                Toast.makeText(this, "خطا در بارگذاری منو", Toast.LENGTH_SHORT).show()
            })
        queue.add(request)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}