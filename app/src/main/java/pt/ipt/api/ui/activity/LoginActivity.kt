package pt.ipt.api.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import pt.ipt.api.databinding.ActivityLoginBinding
import pt.ipt.api.databinding.ActivityRegisterBinding
import pt.ipt.api.retrofit.service.ApiClient.authService
import pt.ipt.api.retrofit.service.ApiError
import pt.ipt.api.retrofit.service.LoginRequest
import pt.ipt.api.retrofit.service.LoginResponse
import pt.ipt.api.retrofit.service.RegisterRequest
import pt.ipt.api.retrofit.service.RegisterResponse
import pt.ipt.api.retrofit.service.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var registerBinding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //dar inflate aos 2 xml´s da activity, Registar e Login.
        //mostrar primeiro o de login
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(loginBinding.root)
        // Initialize token manager
        //TokenManager.init(applicationContext)
        Log.d("Token Response:",TokenManager.getToken().toString())

        //NOTA FUTURA: QUANDO HOUVER SPLASH SCREEN, TROCAR ESTE CÓDIGO NO LOGIN DO JWT PARA O SPLASH SCREEN

        //Ir buscar o token JWT, se for null enviar para a activity de Login
        // Se já tivermos o nosso token guardado, enviar para a activity dos albuns
        /*val token =  TokenManager.getToken()

        if(!token.isNullOrEmpty()){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)

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

    fun renderLogin(view: View) {
        setContentView(loginBinding.root)
    }

    fun enterLogin(view: View) {

        val username = loginBinding.usernameField.text.toString()
        val password = loginBinding.passwordField.text.toString()

        val loginBody = LoginRequest(username = username, password = password)

        // Make the login request
        authService.login(loginBody).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {

                    var token = response.body()?.token?.result

                    if (!token.isNullOrEmpty()) {
                        TokenManager.saveToken(token)

                        TokenManager.saveUsername(username)

                        //Dizer que queremos criar a "intenção" de sair desta atividade (thisLoginActivity)
                        // e ir para a atividade do album
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)

                        //Comecar atividade
                        startActivity(intent)

                        //encerrar esta atividade
                        finish()
                    }
                } else {

                    Toast.makeText(this@LoginActivity, "Algo correu mal, por favor confirme os seus dados", Toast.LENGTH_LONG).show()

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
    fun renderRegister(view: View) {
        setContentView(registerBinding.root)
    }

    fun enterRegister(view: View) {

        val username = registerBinding.usernameField.text.toString()
        val email = registerBinding.emailField.text.toString()
        val password = registerBinding.passwordField.text.toString()
        val is_artista = registerBinding.isArtista.isChecked

        val registBody = RegisterRequest(
            username = username,
            email = email,
            password = password,
            is_artista = is_artista
        )

        // Make the login request
        authService.register(registBody).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    // Mostrar a mensagem vinda da API
                    val message = response.body()?.message ?: "Registo concluído!"
                    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()

                    // Redirecionar para a view de confirmação de email - TODO
                    setContentView(loginBinding.root)
                } else {
                    // 1. Read the error body ONCE into a variable
                    val errorJson = response.errorBody()?.string()

                    // 2. Parse the JSON
                    val apiError = try {
                        errorJson?.let {
                                Gson().fromJson(it, ApiError::class.java).errors
                        }
                    } catch (e: Exception) {
                        null
                    }

                    // 2. Parse the JSON
                    val apiErrorMessage = try {
                        errorJson?.let {
                            Gson().fromJson(it, ApiError::class.java).message
                        }
                    } catch (e: Exception) {
                        null
                    }

                    // 3. Clear previous errors so they don't stay visible from the last attempt
                    registerBinding.usernameLayout.error = null
                    registerBinding.emailLayout.error = null
                    registerBinding.passwordLayout.error = null

                    // 4. Show the error messages
                    apiError.let { errors ->
                        // Note: Assuming 'errorMessage' is a List<String> based on your joinToString code
                        val fullErrorMessage = errors?.joinToString("\n")

                        var ErrorsToPortuguese = ""

                        // Logic: Try to put the error under the correct field
                        when {
                            fullErrorMessage?.contains("email", ignoreCase = true) == true -> {
                                registerBinding.emailLayout.error = fullErrorMessage
                            }
                            fullErrorMessage?.contains("Nome", ignoreCase = true) == true ||
                                    fullErrorMessage?.contains("utilizador", ignoreCase = true) == true -> {
                                registerBinding.usernameLayout.error = fullErrorMessage
                            }
                            fullErrorMessage?.contains("password", ignoreCase = true) == true -> {

                                if(fullErrorMessage.contains("digit"))
                                    ErrorsToPortuguese = ErrorsToPortuguese + ("Palavras-Passe devem conter pelo menos um dígito.\n")

                                if(fullErrorMessage.contains("alphanumeric"))
                                    ErrorsToPortuguese = ErrorsToPortuguese + ("Palavras-Passe devem conter pelo menos um caracter especial.\n")

                                if(fullErrorMessage.contains("6"))
                                    ErrorsToPortuguese = ErrorsToPortuguese + ("Palavras-Passe devem ter pelo menos 6 caracteres.\n")

                                registerBinding.passwordLayout.error = ErrorsToPortuguese
                            }
                            else -> {
                                // If we don't know which field it is, show a Toast
                                Toast.makeText(this@LoginActivity, "Ups! Algo correu mal, por favor confirme os dados", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    // 4. Show the error messages
                    apiErrorMessage.let{ message ->
                        // Logic: Try to put the error under the correct field
                        when {
                            message?.contains("email", ignoreCase = true) == true -> {
                                registerBinding.emailLayout.error = message
                            }
                            message?.contains("Nome", ignoreCase = true) == true ||
                                    message?.contains("utilizador", ignoreCase = true) == true -> {
                                registerBinding.usernameLayout.error = message
                            }
                            message?.contains("password", ignoreCase = true) == true -> {
                                registerBinding.passwordLayout.error = message
                            }
                            else -> {
                                // If we don't know which field it is, show a Toast
                                Toast.makeText(this@LoginActivity, "Ups! Algo correu mal, por favor confirme os dados", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }
}

