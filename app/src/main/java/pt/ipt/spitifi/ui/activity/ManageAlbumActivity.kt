    package pt.ipt.spitifi.ui.activity

    import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import pt.ipt.spitifi.R
import pt.ipt.spitifi.databinding.ActivityAlbumBinding
import pt.ipt.spitifi.model.Album
import pt.ipt.spitifi.retrofit.service.ApiClient.albumService
import pt.ipt.spitifi.retrofit.service.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

    class ManageAlbumActivity : BaseActivity() {

        private lateinit var binding: ActivityAlbumBinding
        private var albumId: Int = -1 // Se for =/= -1, estamos a editar

        private var albumOG: Album? = null

        private var uriFoto: Uri? = null
        private val listaUrisMusicas = mutableListOf<Uri>()

        // 1. Seletor de Foto
        private val photoPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                uriFoto = it
                binding.imgAlbumPreview.setImageURI(it) // Mostra a foto no ecrã
            }
        }

        // 2. Seletor de Músicas (Múltiplas)
        private val musicPicker = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                listaUrisMusicas.clear()
                listaUrisMusicas.addAll(uris)
                binding.txtMusicCount.text = "${uris.size} músicas selecionadas"
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityAlbumBinding.inflate(layoutInflater)
            setContentViewChild(binding.root)

            // Verificar se recebemos um ID
            albumId = intent.getIntExtra("albumId", -1)

            if (albumId != -1) {
                loadAlbum(albumId)
                binding.btnSave.text = getString(R.string.atualizar_lbum)
            }

            binding.btnSave.setOnClickListener {
                if (albumId == -1) saveAlbum() else editAlbum()
            }

            // Para selecionar Foto
            binding.btnSelectPhoto.setOnClickListener {
                photoPicker.launch("image/*")
            }

            // Para selecionar Músicas
            binding.btnSelectMusic.setOnClickListener {
                musicPicker.launch("audio/*")
            }

            // O botão Delete só aparece se estivermos a editar
            if (albumId != -1) {
                binding.btnDelete.visibility = View.VISIBLE
                binding.btnDelete.setOnClickListener {
                    deleteAlbum(albumId)
                }
            }
        }

        private fun saveAlbum() {
            val title = binding.editTitle.text.toString()
            val token = TokenManager.getToken().toString()

            binding.btnSave.isEnabled = false

            binding.btnSave.text = "A criar..."

            Log.d("Token:", token.toString())

            if (title.isBlank()) {
                binding.editTitle.error = "O título é obrigatório"
                return
            }
            if (uriFoto == null) {
                Toast.makeText(this, "Selecione uma foto de capa", Toast.LENGTH_SHORT).show()
                return
            }
            if (listaUrisMusicas.isEmpty()) {
                Toast.makeText(this, "Selecione pelo menos uma música", Toast.LENGTH_SHORT).show()
                return
            }

            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())

            val fotoPart = uriToMultipart(uriFoto!!, "FotoAlbum") // Nome deve ser igual ao C#

            val musicasParts = listaUrisMusicas.mapNotNull { uri ->
                uriToMultipart(uri, "MusicasNovas") // Nome deve ser igual ao List no C#
            }

            albumService.createAlbum(titleBody, fotoPart, musicasParts)
                .enqueue(object : Callback<Album> {
                    override fun onResponse(call: Call<Album>, response: Response<Album>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ManageAlbumActivity, "Criado com sucesso!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@ManageAlbumActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@ManageAlbumActivity, "Erro ao criar: ${response.code()}", Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onFailure(call: Call<Album>, t: Throwable) {
                        Toast.makeText(this@ManageAlbumActivity, "Falha: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
        private fun editAlbum() {
            val titleText = binding.editTitle.text.toString()
            val artistText = albumOG?.artista ?: ""

            binding.btnSave.isEnabled = false
            binding.btnSave.text = "A editar..."

            // 1. Prepare the text parts
            val titlePart = titleText.toRequestBody("text/plain".toMediaTypeOrNull())
            val artistPart = artistText.toRequestBody("text/plain".toMediaTypeOrNull())

            // 2. Prepare the photo part (only if a new one was selected)
            // If uriFoto is null, the server won't update the photo
            val fotoPart: MultipartBody.Part? = if (uriFoto != null) {
                uriToMultipart(uriFoto!!, "FotoAlbum") // Use the helper we made earlier
            } else {
                null
            }

            // 3. Make the call
           albumService.updateAlbum(albumId, titlePart, artistPart, fotoPart)
                .enqueue(object : Callback<Album> {
                    override fun onResponse(call: Call<Album>, response: Response<Album>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ManageAlbumActivity, "Atualizado!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@ManageAlbumActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Log the error body to see exactly why it failed
                            Log.e("API_ERROR", response.errorBody()?.string() ?: "Unknown")
                            Toast.makeText(this@ManageAlbumActivity, "Erro: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Album>, t: Throwable) {
                        Toast.makeText(this@ManageAlbumActivity, "Falha: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }

        fun deleteAlbum(id: Int) {
            AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("Tem a certeza que deseja eliminar este álbum?")
                .setPositiveButton("Sim") { _, _ ->

                    albumService.deleteAlbum(id).enqueue(object : Callback<Unit> {
                            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(this@ManageAlbumActivity, "Eliminado!", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this@ManageAlbumActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    // REQUISITO: Mensagem de erro adequada (Controlo de acesso)
                                    if (response.code() == 403) {
                                        Toast.makeText(this@ManageAlbumActivity, "Não tem permissão de Admin!", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                            override fun onFailure(call: Call<Unit>, t: Throwable) {}
                        })
                }
                .setNegativeButton("Não", null)
                .show()
        }

        private fun loadAlbum(id: Int) {
           albumService.getAlbum(id).enqueue(object : Callback<Album> {
                override fun onResponse(call: Call<Album>, response: Response<Album>) {
                    if (response.isSuccessful) {
                        binding.editTitle.setText(response.body()?.titulo)
                        albumOG = response.body()
                    }
                }
                override fun onFailure(call: Call<Album>, t: Throwable) {
                    Toast.makeText(this@ManageAlbumActivity, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
                }
            })
        }

        private fun uriToMultipart(uri: Uri, paramName: String): MultipartBody.Part? {
            val contentResolver = contentResolver

            // 1. Get the actual file name from the Uri
            var fileName = "file_${System.currentTimeMillis()}" // Fallback name
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = it.getString(nameIndex)
                    }
                }
            }

            // 2. Open an InputStream to read the file data
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val requestBody = inputStream.readBytes().toRequestBody(
                contentResolver.getType(uri)?.toMediaTypeOrNull(),
                0
            )

            // 3. Create the Part
            return MultipartBody.Part.createFormData(paramName, fileName, requestBody)
        }
    }