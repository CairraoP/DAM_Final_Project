package pt.ipt.api.ui.activity

import MusicAdapter
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import pt.ipt.api.R
import pt.ipt.api.model.Album
import pt.ipt.api.model.GlobalVariables
import pt.ipt.api.model.Music
import pt.ipt.api.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MusicActivity : BaseActivity() {

    private lateinit var binding : MusicActivity
    private lateinit var albumImageView : ImageView

    private var albumId : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewChild(R.layout.music_list)

        albumImageView = findViewById(R.id.fotoAlbum)
        // Receive album ID
        albumId = intent.getIntExtra("album", -1)

        if (albumId == -1) {
            Toast.makeText(this, "O álbum não foi encontrado", Toast.LENGTH_SHORT).show()
        }else{
        listMusics()
        }
    }

     fun configureList(albumObject: Album, musics: List<Music>) {
        val recyclerView: RecyclerView = findViewById(R.id.music_list_recyclerview)
        recyclerView.adapter = MusicAdapter(albumObject, musics) { music ->
            // Activity handles playback
            playService?.play(music)      // your service
        }
         val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
    }

    private fun listMusics() {

        val call = RetrofitInitializer().albumService().getAlbum(albumId)

        call.enqueue(object : Callback<Album?> {
            override fun onResponse(call: Call<Album?>?, response: Response<Album?>?) {

                Log.d("API", "Response code: ${response?.code()}")
                Log.d("API", "Body: ${response?.body()}")

                if (response?.isSuccessful == true && response.body() != null) {
                    val album = response.body()!!
                    val musics = album.musicas

                    // Load album image into the top ImageView
                    val imageUrl = GlobalVariables.CON_STRING + album.foto
                    Glide.with(this@MusicActivity)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(albumImageView)

                    //Escrever o nome do album e o seu artista ao lado da imagem
                    val albumTitle : TextView = findViewById(R.id.title_album)
                    albumTitle.setText(album.titulo.toString())

                    val artist : TextView = findViewById(R.id.artist_name_album)

                    //Se for nulo, reescrevemos o nome do artista
                    if(artist.toString().equals("null"))
                        artist.setText(getString(R.string.notFound_artist))
                    else
                        artist.setText(album.artista.toString())

                    configureList(album, musics)

                } else {
                    Toast.makeText(this@MusicActivity, "Album não foi encontrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Album?>?, t: Throwable?) {
                Log.e("onFailure", t?.message ?: "Unknown error")
            }
        })
    }
}