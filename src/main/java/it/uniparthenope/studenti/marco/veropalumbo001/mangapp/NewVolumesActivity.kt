package it.uniparthenope.studenti.marco.veropalumbo001.mangapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.adapter.MyAdapter
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.model.CheckLogin
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.model.ReceivedVolumes
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.repository.Repository

class NewVolumesActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var viewModel: MainViewModel
    private val myAdapter by lazy { MyAdapter() }

    override fun onRestart() {
        super.onRestart()
        finish()
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newvolumes)

        val settings = getSharedPreferences("Login", MODE_PRIVATE)
        val mySessionCookie: String? = settings.getString("session",null)

        val settings1 = getSharedPreferences("Preferenze", Context.MODE_PRIVATE)
        val prefQuartiere: String = settings1.getString("quartiere", "")!!


        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val drawerButton = findViewById<ImageButton>(R.id.menuLaterale)

        navigationView.bringToFront()

        drawerButton.setOnClickListener{
            if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.closeDrawer(GravityCompat.START)
            }else{
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        navigationView.setNavigationItemSelectedListener(this)

        setupRecyclerview()

        viewModel.checkNewVolumes(mySessionCookie,prefQuartiere)
        viewModel.myResponseVolume.observe(this, Observer { response ->
            if(response.isSuccessful){
                response.body()?.let { myAdapter.setData(it) }
            }else{
                Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
            }
        })
        val cart = findViewById<Button>(R.id.addToCart)
        cart.setOnClickListener {
            val carrello = Intent(this, ActivityCarrello::class.java)
            finish()
            startActivity(carrello)
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
                val home = Intent(this, MainActivity::class.java)
                startActivity(home)
            }
            R.id.nav_mieiVolumi ->{
                val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.settings ->{
                val settings = Intent(this,SettingsActivity::class.java)
                startActivity(settings)
            }
            R.id.nav_listaCompleta ->{
                val allVolumes = Intent(this,AllVolumesActivity::class.java)
                startActivity(allVolumes)
            }
            R.id.nav_carrello ->{
                val carrello = Intent(this, ActivityCarrello::class.java)
                startActivity(carrello)
            }
        }
        return true
    }

    private fun setupRecyclerview(){
        val recyclerView = findViewById<RecyclerView>(R.id.newVolumes)
        recyclerView.adapter = myAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}