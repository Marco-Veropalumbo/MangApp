package it.uniparthenope.studenti.marco.veropalumbo001.mangapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.model.Quartiere
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.model.SignupForm
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.repository.Repository

class SignupFillActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onRestart() {
        super.onRestart()
        finish()
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signupfill)

        val SignupError = findViewById<TextView>(R.id.SignupFillError)
        val SignupUsername = findViewById<EditText>(R.id.SignupUsernameEdit)
        val SignupPassword = findViewById<EditText>(R.id.SignupPasswordEdit)
        val Signupbtn = findViewById<Button>(R.id.SignupButton)
        val NT = getIntent().getStringExtra("NumeroTessera")
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        var selezione = 0
        var quartieriArray = mutableListOf<String>()
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.getQuartieri()
        viewModel.myResponseQuartieri.observe(this, Observer { response ->
            if(response.isSuccessful){
                response.body()?.forEach {
                    quartieriArray.add(it.QUARTIERE)
                }

                val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, quartieriArray)
                val quartieri = findViewById<Spinner>(R.id.quartieri)
                quartieri.adapter = arrayAdapter

                quartieri.onItemSelectedListener = object :

                        AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selezione=position
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }
            }else{
                SignupError.text = getString(R.string.connectionError)
            }
        })

        Signupbtn.setOnClickListener {
            val UsernameField = SignupUsername.getText().toString()
            val PasswordField = SignupPassword.getText().toString()

            val mySignupForm = SignupForm(UsernameField,PasswordField,NT)
            viewModel.signup(mySignupForm)
            viewModel.myResponseSignup.observe(this, Observer {  response ->
                if(response.isSuccessful){
                    if(response.body()?.username == "utilizzato"){
                        SignupError.text = getString(R.string.usedUsername)
                    }else{
                        val settings1 = getSharedPreferences("Preferenze",Context.MODE_PRIVATE)
                        settings1.edit().putString("quartiere", quartieriArray[selezione]).apply()
                        val settings2 = getSharedPreferences("Login",Context.MODE_PRIVATE)
                        settings2.edit().putString("user",UsernameField).apply()
                        val Home = Intent(this, MainActivity::class.java)
                        Home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(Home)
                    }
                }else{
                    SignupError.text = getString(R.string.connectionError)
                }
            })
        }
    }
}