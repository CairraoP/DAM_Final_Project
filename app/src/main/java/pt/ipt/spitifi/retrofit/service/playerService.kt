package pt.ipt.spitifi.retrofit.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import pt.ipt.spitifi.model.GlobalVariables
import pt.ipt.spitifi.model.Music

class playerService : Service() {

    private val binder = MusicPlayerBinder()
    private var mediaPlayer: MediaPlayer? = null

    // Stores last music played
    private var currentMusic: Music? = null

    // Listener for updating mini-player UI
    private var miniPlayerListener: ((Music, Boolean) -> Unit)? = null

    inner class MusicPlayerBinder : Binder() {
        fun getService(): playerService = this@playerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    // Called by BaseActivity to receive mini-player updates
    fun setMiniPlayerListener(listener: (Music, Boolean) -> Unit) {
        miniPlayerListener = listener

        // If activity recreated while music is playing → sync UI
        currentMusic?.let {
            miniPlayerListener?.invoke(it, isPlaying())
        }
    }

    // MAIN PLAY FUNCTION
    fun play(music: Music) {
        currentMusic = music

        val url = GlobalVariables.CON_STRING + music.filePath

        mediaPlayer?.stop()
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener { start() }
            setOnCompletionListener {
                miniPlayerListener?.invoke(music, false)
            }
        }

        miniPlayerListener?.invoke(music, true)
    }

    fun pause() {
        mediaPlayer?.pause()
        currentMusic?.let {
            miniPlayerListener?.invoke(it, false)
        }
    }

    fun resume() {
        mediaPlayer?.start()
        currentMusic?.let {
            miniPlayerListener?.invoke(it, true)
        }
    }

    fun restart() {
        mediaPlayer?.seekTo(0)
        mediaPlayer?.start()
        currentMusic?.let {
            miniPlayerListener?.invoke(it, true)
        }
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun toggle() {
        if (isPlaying()) pause() else resume()
    }

}
