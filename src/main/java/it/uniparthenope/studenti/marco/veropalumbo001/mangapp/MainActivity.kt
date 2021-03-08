package it.uniparthenope.studenti.marco.veropalumbo001.mangapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.adapter.MyAdapter
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.model.CheckLogin
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.model.ReceivedVolumes
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.repository.Repository


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var viewModel: MainViewModel
    private val myAdapter by lazy { MyAdapter() }
    private val drawerLayout: DrawerLayout by lazy {
        findViewById(R.id.drawer_layout)
    }

    override fun onRestart() {
        super.onRestart()
        finish()
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settings1 = getSharedPreferences("Login", Context.MODE_PRIVATE)
        val mySessionCookie: String? = settings1.getString("session",null)
        val settings2 = getSharedPreferences("Preferenze", Context.MODE_PRIVATE)
        val prefQuartiere: String? = settings2.getString("quartiere",null)

        setupRecyclerview()

        if(mySessionCookie.isNullOrBlank()){
            val loginIntent = Intent(this, LoginActivity::class.java)

            startActivity(loginIntent)
        }else{
            val repository = Repository()
            val viewModelFactory = MainViewModelFactory(repository)
            viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
            val mySession = CheckLogin(false, null)
            viewModel.checkSession(mySessionCookie, mySession)
            viewModel.myResponseSession.observe(this, Observer { response ->
                if(response.isSuccessful){
                    if (response.body()?.check == true){

                        val mainText = findViewById<TextView>(R.id.MainText)
                        val navigationView = findViewById<NavigationView>(R.id.nav_view)
                        val drawerButton = findViewById<ImageButton>(R.id.menuLaterale)
                        val user = response.body()?.username
                        mainText.text = getString(R.string.welcomeBack, user)

                        navigationView.bringToFront()
                        drawerButton.setOnClickListener{
                            if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                                drawerLayout.closeDrawer(GravityCompat.START)
                            }else{
                                drawerLayout.openDrawer(GravityCompat.START)
                            }
                        }
                        navigationView.setNavigationItemSelectedListener(this)

                        val cart=findViewById<Button>(R.id.addToCart)
                        cart.setOnClickListener {
                            val carrello = Intent(this, ActivityCarrello::class.java)
                            finish()
                            startActivity(carrello)
                        }

                        viewModel.checkMyVolumes(mySessionCookie,user,prefQuartiere)
                        viewModel.myResponsePersonal.observe(this, Observer { response ->
                            if(response.isSuccessful){
                                response.body()?.let { myAdapter.setData(it) }
                            }else{
                                Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
                            }
                        })
                    }else{
                        val loginIntent = Intent(this, LoginActivity::class.java)

                        startActivity(loginIntent)
                    }
                }else{
                    Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_logout ->{
                val settings = getSharedPreferences("Login", Context.MODE_PRIVATE)
                val mySessionCookie: String? = settings.getString("session",null)
                val user: String? = settings.getString("user",null)
                val mySession2 = CheckLogin(true, user)
                viewModel.myLogout(mySessionCookie, mySession2)
                viewModel.myResponseSession.observe(this, Observer { response ->
                    if(response.isSuccessful){
                        val mySettings = getSharedPreferences("Login", Context.MODE_PRIVATE)
                        mySettings.edit().remove("session").apply()
                        finish()
                        val home = Intent(this, MainActivity::class.java)
                        startActivity(home)
                    }else{
                        Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
                    }
                })
            }
            R.id.nav_home ->{
                val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_mieiVolumi ->{
                val personalVolumes = Intent(this, NewVolumesActivity::class.java)
                finish()
                startActivity(personalVolumes)
            }
            R.id.settings ->{
                val settings = Intent(this,SettingsActivity::class.java)
                finish()
                startActivity(settings)
            }
            R.id.nav_listaCompleta ->{
                val allVolumes = Intent(this,AllVolumesActivity::class.java)
                finish()
                startActivity(allVolumes)
            }
            R.id.nav_carrello ->{
                val carrello = Intent(this, ActivityCarrello::class.java)
                finish()
                startActivity(carrello)
            }
        }
        return true
    }

    private fun setupRecyclerview(){
        val recyclerView = findViewById<RecyclerView>(R.id.myVolumes)
        recyclerView.adapter = myAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}