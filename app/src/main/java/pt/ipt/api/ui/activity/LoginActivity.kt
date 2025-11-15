package pt.ipt.api.ui.activity

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.api.retrofit.service.*
import retrofit2.Call
import retrofit2.Callback
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import pt.ipt.api.databinding.ActivityLoginBinding
import pt.ipt.api.databinding.ActivityRegisterBinding
import pt.ipt.api.retrofit.service.ApiClient.authService
import retrofit2.Response
import kotlin.math.log

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding : ActivityLoginBinding
    private lateinit var registerBinding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //dar inflate aos 2 xml´s da activity, Registar e Login.
        //mostrar primeiro o de login
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(loginBinding.root)
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

    /**
     * Função para autenticar uma pessoa na app quando esta clica no botão de login
     * Validar o username e password e caso sejam válidos, ver o token devolvido pelo servidor.
     * Se o token for válido, permitir que o user passe para a aplicação
     */
    fun enterLogin(view: View){

        val username = loginBinding.usernameField.text.toString()
        val password = loginBinding.passwordField.text.toString()

        val loginBody = LoginRequest(username = username, password = password)

        // Make the login request
        authService.login(loginBody).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {

                    var token = TokenManager.getToken()

                    if (token.isNullOrEmpty()){
                        token = response.body()?.token?.result
                    }

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

                    val errorJson = response.errorBody()?.string();
                    //é necessário meter o RegistorError::class.java para o Gson saber que objeto dar parse no errorBody
                    val errorMessage = Gson().fromJson(errorJson, ApiError::class.java)

                    Toast.makeText(this@LoginActivity, errorMessage.message, Toast.LENGTH_LONG).show()
                    }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * SECÇÃO DE REGISTO DA APP
     */


    /**
     * Função para renderizar com o registo
      */
    fun renderRegister(view :View){
        setContentView(registerBinding.root)
    }

    fun enterRegister(view: View){

        val username = registerBinding.usernameField.text.toString()
        val email = registerBinding.emailField.text.toString()
        val password = registerBinding.passwordField.text.toString()
        val is_artista = registerBinding.isArtista.isChecked

        val registBody = RegisterRequest(
            username = username,
            email = email,
            password = password,
            is_artista = is_artista)

        // Make the login request
        authService.register(registBody).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    // Mostrar a mensagem vinda da API
                    val message = response.body()?.message ?: "Registo concluído!"
                    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()

                    // Redirecionar para a view de confirmação de email - TODO
                    setContentView(loginBinding.root)
                } else {

                    val errorJson = response.errorBody()?.string();
                    //é necessário meter o RegistorError::class.java para o Gson saber que objeto dar parse no errorBody
                    val errorMessage = Gson().fromJson(errorJson, ApiError::class.java)

                    Toast.makeText(this@LoginActivity, errorMessage.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}

