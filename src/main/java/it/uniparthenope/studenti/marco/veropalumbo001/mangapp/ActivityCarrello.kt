package it.uniparthenope.studenti.marco.veropalumbo001.mangapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.adapter.Carrello
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.adapter.MyAdapterCart
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.model.CheckLogin
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.repository.Repository

class ActivityCarrello: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {
    private lateinit var viewModel: MainViewModel
    private val myAdapterCart by lazy { MyAdapterCart() }

    override fun onRestart() {
        super.onRestart()
        finish()
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrello)

        val settings = getSharedPreferences("Login", Context.MODE_PRIVATE)
        val mySessionCookie: String? = settings.getString("session", null)
        val user: String? = settings.getString("user", null)
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

        if(Carrello.isNullOrEmpty()){
            val error = findViewById<TextView>(R.id.allVolumesTxt)
            error.text = getString(R.string.empty_cart)
        }else{
            Carrello?.let { myAdapterCart.setData(it.toList()) }
            val buy = findViewById<Button>(R.id.buy)
            buy.setOnClickListener {
                viewModel.acquista(mySessionCookie, prefQuartiere, user, Carrello)
                viewModel.myResponseAcquisto.observe(this, Observer { response ->
                    if(response.isSuccessful){
                        Log.d("test","test")
                        Toast.makeText(this, getString(R.string.bought), Toast.LENGTH_SHORT).show()
                        Carrello?.clear()
                        val home = Intent(this, MainActivity::class.java)
                        finish()
                        startActivity(home)
                    }else{
                        Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        val dumpCart = findViewById<Button>(R.id.dumpCart)
        dumpCart.setOnClickListener {
            Carrello?.clear()
            val home = Intent(this, MainActivity::class.java)
            finish()
            startActivity(home)
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
                        val mySettings = getSharedPreferences("Login", MODE_PRIVATE)
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
                finish()
                startActivity(home)
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
                val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
        return true
    }

    private fun setupRecyclerview(){
        val recyclerView2 = findViewById<RecyclerView>(R.id.cartVolumes)
        recyclerView2.adapter = myAdapterCart
        recyclerView2.layoutManager = LinearLayoutManager(this)
    }
}