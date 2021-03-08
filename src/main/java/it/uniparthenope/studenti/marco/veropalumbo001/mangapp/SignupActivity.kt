package it.uniparthenope.studenti.marco.veropalumbo001.mangapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.model.NTCheck
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.repository.Repository

class SignupActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onRestart() {
        super.onRestart()
        finish()
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val errorSignup = findViewById<TextView>(R.id.SignupError)
        val checkButton = findViewById<Button>(R.id.btngo)

        checkButton.setOnClickListener {
            val numeroTessera = findViewById<EditText>(R.id.editcardid)
            val NT = numeroTessera.getText().toString()
            val repository = Repository()
            val viewModelFactory = MainViewModelFactory(repository)
            viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
            val myNTCheck = NTCheck(null, null, NT)
            viewModel.checkID(myNTCheck)
            viewModel.myResponse.observe(this, Observer { response ->
                if (response.isSuccessful) {
                    if (response.body()?.NTNotFound == true) {
                        errorSignup.text = getString(R.string.IDNotFound)
                    } else if (response.body()?.NTAlreadyUsed == true) {
                        errorSignup.text = getString(R.string.IDAlreadyRegistered)
                    } else {
                        val SignupFill = Intent(this, SignupFillActivity::class.java)
                        SignupFill.putExtra("NumeroTessera", NT)
                        startActivity(SignupFill)
                    }
                } else {
                    errorSignup.text = getString(R.string.connectionError)
                }
            })
        }
    }
}