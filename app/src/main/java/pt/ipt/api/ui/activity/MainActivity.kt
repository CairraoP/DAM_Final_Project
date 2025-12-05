package pt.ipt.api.ui.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import pt.ipt.api.R
import pt.ipt.api.model.Album
import pt.ipt.api.retrofit.RetrofitInitializer
import pt.ipt.api.ui.adapter.MainAdapter
import pt.ipt.api.retrofit.service.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.album_list)


        if(!TokenManager.getToken().isNullOrEmpty()){
            listAlbums()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "music_channel",
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

    }

    private fun configureList(Albums: List<Album>) {
        val recyclerView: RecyclerView = findViewById(R.id.album_list_recyclerview)
        recyclerView.adapter = MainAdapter(Albums, this){
            albumID -> openAlbum(albumID)
        }
        val layoutManager = StaggeredGridLayoutManager( 2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
    }

    private fun listAlbums() {
        val call = RetrofitInitializer().albumService().list()
        call.enqueue(object : Callback<List<Album>?> {
            override fun onResponse(call: Call<List<Album>?>?, response: Response<List<Album>?>?) {
                Log.d("API", "Response code: ${response?.code()}")
                Log.d("API", "Body: ${response?.body()}")
                if (response?.isSuccessful == true && !response.body().isNullOrEmpty()) {
                    val albums = response.body()!!
                    configureList(albums)
                } else {
                    Toast.makeText(this@MainActivity, "No albums found", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Album>?>?, t: Throwable?) {
                t?.message?.let { Log.e("onFailure error", it) }
            }
        })
    }

    fun openAlbum(albumID: Int) {
        Toast.makeText(this@MainActivity, "Teste Olá Album", Toast.LENGTH_LONG).show()

        //Inicializar a classe da listagem de Musicas
        val intent = Intent(this, MusicActivity::class.java)
        intent.putExtra("album", albumID)
        startActivity(intent)
    }

}