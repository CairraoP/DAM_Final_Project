package pt.ipt.api.ui.activity

import MusicAdapter
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MusicActivity : BaseActivity() {

    private lateinit var binding: MusicListBinding
    private lateinit var albumImageView: ImageView

    // on below line we are creating
    // a variable for bitmap
    lateinit var bitmap: Bitmap

    // on below line we are creating
    // a variable for qr encoder.
    lateinit var qrEncoder: QRGEncoder


    lateinit var generateQRBtn: Button

    private var albumId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MusicListBinding.inflate(layoutInflater)

        setContentViewChild(binding.root)

        //albumImageView = findViewById(R.id.fotoAlbum)
        albumImageView = binding.fotoAlbum
        // Receive album ID
        albumId = intent.getIntExtra("album", -1)

        if (albumId == -1) {
            Toast.makeText(this, "O álbum não foi encontrado", Toast.LENGTH_SHORT).show()
        } else {
            listMusics()
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
                    binding.artistNameAlbum.setText(album.artista.toString())

                    binding.titleAlbum.setText(album.titulo)

                    //Se for nulo, reescrevemos o nome do artista
                    if (binding.artistNameAlbum.toString().equals("null"))
                        binding.artistNameAlbum.setText(getString(R.string.notFound_artist))

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
}