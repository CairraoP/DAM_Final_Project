package pt.ipt.api.ui.activity

import MusicAdapter
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import pt.ipt.api.R
import pt.ipt.api.databinding.MusicListBinding
import pt.ipt.api.model.Album
import pt.ipt.api.model.GlobalVariables
import pt.ipt.api.model.Music
import pt.ipt.api.retrofit.RetrofitInitializer
import pt.ipt.api.retrofit.service.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MusicActivity : BaseActivity() {

    private lateinit var binding: MusicListBinding
    private lateinit var albumImageView: ImageView

    private var albumId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MusicListBinding.inflate(layoutInflater)

        setContentViewChild(binding.root)

        albumImageView = binding.fotoAlbum
        // Receive album ID
        albumId = intent.getIntExtra("album", -1)

        if (albumId == -1) {
            Toast.makeText(this, "O álbum não foi encontrado", Toast.LENGTH_SHORT).show()
        } else {
            listMusics()
        }

        binding.deleteAlbum.setOnClickListener {
            deleteAlbum(albumId)
        }

    }

    fun configureList(albumObject: Album, musics: List<Music>) {

        binding.musicListRecyclerview.adapter = MusicAdapter(
            albumObject,
            musics,
            onPlayClicked = { music ->
                playService?.play(music)
            },
            onQrClick = { music ->
                // Call the helper function to show the QR code
                generateAndShowQR(music)
            }
        )
        binding.musicListRecyclerview.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
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
                    binding.artistNameAlbum.setText(getString(R.string.artista_nome) + album.artista.toString())

                    binding.titleAlbum.setText(getString(R.string.album) + album.titulo)

                    //Se for nulo, reescrevemos o nome do artista
                    if (binding.artistNameAlbum.toString().equals("null"))
                        binding.artistNameAlbum.setText(getString(R.string.notFound_artist))

                    if (!TokenManager.getUsername().equals(album.artista)) {
                        binding.editAlbum.visibility = View.GONE
                        binding.deleteAlbum.visibility = View.GONE
                    }

                    configureList(album, musics)

                } else {
                    Toast.makeText(
                        this@MusicActivity,
                        "Album não foi encontrado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Album?>?, t: Throwable?) {
                Log.e("onFailure", t?.message ?: "Unknown error")
            }
        })
    }

    private fun generateAndShowQR(music: Music) {
        val dataToEncode = GlobalVariables.CON_STRING + music.filePath

        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val dimen = if (width < height) width * 3 / 4 else height * 3 / 4

        val qrEncoder = QRGEncoder(dataToEncode, null, QRGContents.Type.TEXT, dimen)
        qrEncoder.colorBlack = Color.BLACK
        qrEncoder.colorWhite = Color.WHITE

        try {
            // CHANGED: Using .getBitmap() which you confirmed is available
            val qrBitmap = qrEncoder.getBitmap()

            if (qrBitmap != null) {
                val qrImageView = ImageView(this)
                qrImageView.setImageBitmap(qrBitmap)

                AlertDialog.Builder(this)
                    .setTitle("QR Code: ${music.nome}")
                    .setView(qrImageView)
                    .setPositiveButton("Fechar") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        } catch (e: Exception) {
            // The library uses com.google.zxing.WriterException
            Log.e("QR_ERROR", "Erro ao gerar QR: ${e.message}")
        }
    }

    fun editAlbumInActivity(view: View) {
        val edit = Intent(this, ManageAlbumActivity::class.java).putExtra("albumId", albumId)

        startActivity(edit)
        finish()
    }

    fun deleteAlbum(id: Int) {
        android.app.AlertDialog.Builder(this)
            .setTitle("Eliminar")
            .setMessage("Tem a certeza que deseja eliminar este álbum?")
            .setPositiveButton("Sim") { _, _ ->

                RetrofitInitializer().albumService().deleteAlbum(id)
                    .enqueue(object : Callback<Unit> {
                        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@MusicActivity, "Eliminado!", Toast.LENGTH_SHORT)
                                    .show()

                                val intent = Intent(this@MusicActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                // REQUISITO: Mensagem de erro adequada (Controlo de acesso)
                                if (response.code() == 403) {
                                    Toast.makeText(
                                        this@MusicActivity,
                                        "Não tem permissão de Admin!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<Unit>, t: Throwable) {}
                    })
            }
            .setNegativeButton("Não", null)
            .show()
    }
}