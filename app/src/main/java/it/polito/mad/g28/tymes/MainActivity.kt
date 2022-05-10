package it.polito.mad.g28.tymes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment

import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerlayout)
        val navView: NavigationView = findViewById(R.id.navigationView)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {

            it.isChecked = true

            when(it.itemId){

                R.id.home -> changeFrag(Home(), it.title.toString())
                R.id.my_profile_icon -> changeFrag(ShowProfileActivity(), it.title.toString())
                R.id.my_clan_icon -> changeFrag(clanFragement(), it.title.toString())
                R.id.all_tslots_list_icon -> changeFrag(TimeSlotListFragment(), it.title.toString())
                R.id.my_tslots_icon -> changeFrag(TimeSlotDetailsFragment(), it.title.toString())
                R.id.tymes_settings_icon -> changeFrag(settingsFragment(), it.title.toString())
                R.id.about_tymes_icon -> changeFrag(aboutFragment(), it.title.toString())

            }
            true
        }


    }

    private fun changeFrag(fragment: Fragment, title: String){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment).commit()
//        findNavController(R.id.homeFragment).navigate(R.id.action_homeFragment_to_showProfileActivity)
        drawerLayout.closeDrawers()
        setTitle(title)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}