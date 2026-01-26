package pt.ipt.api.ui.activity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import pt.ipt.api.R
import pt.ipt.api.model.Music
import pt.ipt.api.retrofit.service.playerService

open class BaseActivity : AppCompatActivity() {

    //Foi necessário assumir as variáveis relacionadas ao botão de música como Null por causa da ordem dos inflates.
    protected var miniPlayer: View? = null
    protected var musicTitle: TextView? = null
    protected var btnPlayPause: ImageButton? = null

    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    // MUST be named playService so child Activities can access it
    protected var playService: playerService? = null
    private var bound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        // to make the Navigation drawer icon always appear on the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        actionBarDrawerToggle.syncState()


        val navigationView : NavigationView = findViewById(R.id.nav_view)

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_account -> {
                    // Action for Account
                }
                R.id.nav_settings -> {
                    // Action for Artistas
                }
                R.id.nav_logout -> {
                    val intent = Intent(this@BaseActivity, LoginActivity::class.java)

                    //Comecar atividade
                    startActivity(intent)
                }
            }

            // Close the drawer
            drawerLayout.closeDrawers()
            true
        }
    }

    @SuppressLint("InflateParams")
     fun setContentViewChild(view: View) {
            // 1. Find the container that was already inflated in onCreate
            val container = findViewById<FrameLayout>(R.id.contentFrame)

            // 2. Clear it just in case, then inflate the child layout into it
            container.removeAllViews()
            container.addView(view)
            //layoutInflater.inflate(layoutResID, container, true)

            // 3. Bind your player UI views (they exist in activity_base)
            // Ensure IDs match what is inside @layout/bottom_player
            miniPlayer = findViewById(R.id.musicPlayerBar)
            musicTitle = findViewById(R.id.musicTitle)
            btnPlayPause = findViewById(R.id.btnPlayPause)

            btnPlayPause?.setOnClickListener {
                playService?.toggle()
            }
        }

    // Service binding
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val b = binder as playerService.MusicPlayerBinder
            playService = b.getService()
            bound = true

            // Let the service notify UI updates
            playService?.setMiniPlayerListener { m, playing ->
                updateMiniPlayer(m, playing)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, playerService::class.java).also {
            bindService(it, connection, BIND_AUTO_CREATE)
            startService(it)
        }
    }

    override fun onStop() {
        super.onStop()
        if (bound) unbindService(connection)
    }

    // Default mini-player UI update
    protected open fun updateMiniPlayer(m: Music, playing: Boolean) {
        miniPlayer?.visibility = View.VISIBLE

        musicTitle?.text = m.nome  // or m.title — match your model

        btnPlayPause?.setImageResource(
            if (playing) R.drawable.ic_pause else R.drawable.ic_play
        )
    }

    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
}
