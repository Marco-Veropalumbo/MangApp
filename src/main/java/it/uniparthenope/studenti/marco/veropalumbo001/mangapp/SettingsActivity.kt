package it.uniparthenope.studenti.marco.veropalumbo001.mangapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.model.CheckLogin
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.repository.Repository

class SettingsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val drawerButton = findViewById<ImageButton>(R.id.menuLaterale)

        navigationView.bringToFront()

        drawerButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        navigationView.setNavigationItemSelectedListener(this)

        var selezione = 0
        var quartieriArray = mutableListOf<String>()

        var chosenQuartiere = findViewById<TextView>(R.id.chosenQuartiere)

        val settings1 = getSharedPreferences("Preferenze", Context.MODE_PRIVATE)
        val prefQuartiere: String = settings1.getString("quartiere", "")!!

        chosenQuartiere.text = getString(R.string.quartiereNow, prefQuartiere)

        viewModel.getQuartieri()
        viewModel.myResponseQuartieri.observe(this, Observer { response ->
            if (response.isSuccessful) {
                response.body()?.forEach {
                    quartieriArray.add(it.QUARTIERE)
                }

                val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, quartieriArray)
                val quartieri = findViewById<Spinner>(R.id.changeQuartiere)
                val conferma = findViewById<Button>(R.id.confirmQuartiere)
                quartieri.adapter = arrayAdapter

                quartieri.onItemSelectedListener = object :

                        AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selezione = position
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }
                conferma.setOnClickListener {
                    val settings = getSharedPreferences("Preferenze", Context.MODE_PRIVATE)
                    settings.edit().putString("quartiere", quartieriArray[selezione]).apply()
                    chosenQuartiere.text = getString(R.string.quartiereNow, quartieriArray[selezione])
                }
            } else {
                Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout -> {
                val settings = getSharedPreferences("Login", Context.MODE_PRIVATE)
                val mySessionCookie: String? = settings.getString("session", null)
                val user: String? = settings.getString("user", null)
                val mySession2 = CheckLogin(true, user)
                viewModel.myLogout(mySessionCookie, mySession2)
                viewModel.myResponseSession.observe(this, Observer { response ->
                    if (response.isSuccessful) {
                        val mySettings = getSharedPreferences("Login", Context.MODE_PRIVATE)
                        mySettings.edit().remove("session").apply()
                        finish()
                        val home = Intent(this, MainActivity::class.java)
                        startActivity(home)
                    } else {
                        Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
                    }
                })
            }
            R.id.nav_home -> {
                val home = Intent(this, MainActivity::class.java)
                startActivity(home)
            }
            R.id.nav_mieiVolumi ->{
                val personalVolumes = Intent(this, NewVolumesActivity::class.java)
                startActivity(personalVolumes)
            }
            R.id.settings -> {
                val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_listaCompleta ->{
                val allVolumes = Intent(this,AllVolumesActivity::class.java)
                startActivity(allVolumes)
            }
        }
        return true
    }
}