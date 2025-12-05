package pt.ipt.api.ui.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.api.R
import pt.ipt.api.model.Music
import pt.ipt.api.retrofit.service.playerService

open class BaseActivity : AppCompatActivity() {

    protected lateinit var miniPlayer: View
    protected lateinit var musicTitle: TextView
    protected lateinit var btnPlayPause: ImageButton

    // ✔ MUST be named playService so child Activities can access it
    protected var playService: playerService? = null
    private var bound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        // Inflate base layout
        val base = layoutInflater.inflate(R.layout.activity_base, null)
        val container = base.findViewById<FrameLayout>(R.id.contentFrame)

        // Inflate child layout inside the container
        layoutInflater.inflate(layoutResID, container, true)
        super.setContentView(base)

        // Bind mini-player UI
        miniPlayer = base.findViewById(R.id.musicPlayerBar)
        musicTitle = base.findViewById(R.id.musicTitle)
        btnPlayPause = base.findViewById(R.id.btnPlayPause)

        btnPlayPause.setOnClickListener {
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
        miniPlayer.visibility = View.VISIBLE

        musicTitle.text = m.nome  // or m.title — match your model

        btnPlayPause.setImageResource(
            if (playing) R.drawable.ic_pause else R.drawable.ic_play
        )
    }
}
