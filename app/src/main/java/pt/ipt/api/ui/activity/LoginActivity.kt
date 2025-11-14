package pt.ipt.api.ui.activity

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.api.R
import pt.ipt.api.retrofit.RetrofitInitializer
import pt.ipt.api.retrofit.service.LoginRequest
import pt.ipt.api.retrofit.service.LoginResponse
import pt.ipt.api.retrofit.service.TokenManager
import retrofit2.Call
import retrofit2.Callback
import android.os.Bundle
import android.view.View
import pt.ipt.api.databinding.ActivityLoginBinding
import pt.ipt.api.model.GlobalVariables
import pt.ipt.api.retrofit.service.ApiClient.authService
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize token manager
        TokenManager.init(applicationContext)

        //NOTA FUTURA: QUANDO HOUVER SPLASH SCREEN, TROCAR ESTE CÓDIGO NO LOGIN DO JWT PARA O SPLASH SCREEN

        //Ir buscar o token JWT, se for null enviar para a activity de Login
        // Se já tivermos o nosso token guardado, enviar para a activity dos albuns
        /*val token =  TokenManager.getToken()

        if(!token.isNullOrEmpty()){
            val intent = Intent(this@LoginActivity, AlbumListActivity::class.java)

            intent.putExtra("user_token", token)

            //Comecar atividade
            startActivity(intent)

            //encerrar esta atividade
            finish()
        }*/
    }

    fun enterLogin(view: View){

        val username = binding.usernameField.text.toString()
        val password = binding.passwordField.text.toString()

        val loginBody = LoginRequest(username = username, password = password)

        // Make the login request
        authService.login(loginBody).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token?.result
                    if (!token.isNullOrEmpty()) {
                        TokenManager.saveToken(token)

                        //Dizer que queremos criar a "intenção" de sair desta atividade (thisLoginActivity)
                        // e ir para a atividade do album
                        val intent = Intent(this@LoginActivity, AlbumListActivity::class.java)

                        //passar dados extra, neste caso o jwt token
                        intent.putExtra("user_token", token)

                        //Comecar atividade
                        startActivity(intent)

                        //encerrar esta atividade
                        finish()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

