package it.uniparthenope.studenti.marco.veropalumbo001.mangapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.model.Login
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.repository.Repository

class LoginActivity : AppCompatActivity() {

    override fun onBackPressed() {
    }
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameField = findViewById<EditText>(R.id.UsernameField)
        val passwordField = findViewById<EditText>(R.id.PasswordField)
        val loginButton = findViewById<Button>(R.id.Login)
        val signupButton = findViewById<Button>(R.id.Signup)
        val loginFailed = findViewById<TextView>(R.id.LoginFailed)

        loginButton.setOnClickListener {
            val repository = Repository()
            val viewModelFactory = MainViewModelFactory(repository)
            viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
            val username = usernameField.getText().toString()
            val password = passwordField.getText().toString()
            val myLogin = Login(username, password)
            viewModel.login(myLogin)
            viewModel.myResponseLogin.observe(this, Observer { response ->
                if (response.isSuccessful) {
                    if (response.body()?.username != username) {
                        loginFailed.text = getString(R.string.loginFailed)
                    } else {
                        val cookies: List<String> = response.headers().values("Set-Cookie")
                        val sessionCookie = cookies.get(0).split(";")[0]
                        val settings = getSharedPreferences("Login", Context.MODE_PRIVATE)
                        settings.edit().putString("session", sessionCookie).apply()
                        settings.edit().putString("user",username).apply()
                        val home = Intent(this, MainActivity::class.java)
                        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(home)
                    }
                } else {
                    Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
                }
            })
        }

        signupButton.setOnClickListener {
            val signupIntent = Intent(this, SignupActivity::class.java)

            startActivity(signupIntent)
        }
    }



}