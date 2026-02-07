package pt.ipt.api.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import okhttp3.ResponseBody
import pt.ipt.api.databinding.ActivityAccountBinding
import pt.ipt.api.model.Album
import pt.ipt.api.retrofit.service.ApiClient.albumService
import pt.ipt.api.retrofit.service.ApiClient.artistService
import pt.ipt.api.retrofit.service.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountActivity : BaseActivity() {
    private lateinit var binding: ActivityAccountBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentViewChild(binding.root)

        binding.btnDeleteAccount.setOnClickListener {
            deleteAccount()
        }

        binding.userName.setText(TokenManager.getUsername())
        binding.userAlbumCount.setText("Função a Implementar")
    }

    fun deleteAccount(){
        AlertDialog.Builder(this)
                .setTitle("Atenção")
                .setMessage("Tem a certeza que deseja eliminar a sua conta? Esta ação é irreversível.")
                .setPositiveButton("Eliminar") { _, _ ->

                    artistService.deleteAccount(TokenManager.getUsername())
                        .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@AccountActivity, "Conta eliminada com sucesso", Toast.LENGTH_LONG).show()

                                // Limpar dados de login local (Token)
                                TokenManager.clearToken()

                                // Voltar para o ecrã de Login ou fechar a app
                                val intent = Intent(this@AccountActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@AccountActivity, "Erro ao eliminar: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(this@AccountActivity, "Falha de rede: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                .setNegativeButton("Cancelar", null)
                .show()
    }


    private fun albumCount() {
        val call = albumService.list()
        call.enqueue(object : Callback<List<Album>?> {
            override fun onResponse(call: Call<List<Album>?>?, response: Response<List<Album>?>?) {
                Log.d("API", "Response code: ${response?.code()}")
                Log.d("API", "Body: ${response?.body()}")
                if (response?.isSuccessful == true && !response.body().isNullOrEmpty()) {
                    val albums = response.body()!!
                } else {
                    Toast.makeText(this@AccountActivity, "Não foi encontrado nenhum álbum", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Album>?>?, t: Throwable?) {
                t?.message?.let { Log.e("onFailure error", it) }
            }
        })
    }

}