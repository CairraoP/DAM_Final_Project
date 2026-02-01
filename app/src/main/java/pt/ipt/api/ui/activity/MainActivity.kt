package pt.ipt.api.ui.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import pt.ipt.api.databinding.AlbumListBinding
import pt.ipt.api.model.Album
import pt.ipt.api.retrofit.RetrofitInitializer
import pt.ipt.api.retrofit.service.TokenManager
import pt.ipt.api.ui.adapter.MainAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : BaseActivity() {

    private lateinit var binding : AlbumListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AlbumListBinding.inflate(layoutInflater)

        setContentViewChild(binding.root)


        if(!TokenManager.getToken().isNullOrEmpty()){
            listAlbums()
        }

        val channel = NotificationChannel(
            "music_channel",
            "Music Playback",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

    }

    private fun configureList(Albums: List<Album>) {

        binding.albumListRecyclerview.adapter = MainAdapter(Albums, this){
            albumID -> openAlbum(albumID)
        }

        binding.albumListRecyclerview.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
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
                    Toast.makeText(this@MainActivity, "Não foi encontrado nenhum álbum", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Album>?>?, t: Throwable?) {
                t?.message?.let { Log.e("Erro inesperado, pedimos desculpa", it) }
            }
        })
    }

    fun openAlbum(albumID: Int) {
        //Inicializar a classe da listagem de Musicas
        val intent = Intent(this, MusicActivity::class.java)
        intent.putExtra("album", albumID)
        startActivity(intent)
    }

}