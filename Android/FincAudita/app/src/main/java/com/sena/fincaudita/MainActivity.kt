package com.sena.fincaudita

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.green)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var btnUser = findViewById<ImageButton>(R.id.btnUser)
        var btnAlert = findViewById<ImageButton>(R.id.btnAlert)
        var btnHome = findViewById<Button>(R.id.btnHome)
        var btnRevision = findViewById<Button>(R.id.btnRevision)
        var btnFertilization = findViewById<Button>(R.id.btnFertilization)
        var btnFumigation = findViewById<Button>(R.id.btnFumigation)
        var btnFarm = findViewById<Button>(R.id.btnFarm)

        btnUser.setOnClickListener {
            showCustomPopupWindow(it)
        }

        btnHome.setOnClickListener {
            changeFragment(HomeFragment.newInstance())
        }

        btnAlert.setOnClickListener {
            changeFragment(NotificationFragment.newInstance())
        }

        btnRevision.setOnClickListener {
            changeFragment(RevisionFragment.newInstance())
        }

        btnFertilization.setOnClickListener {
            changeFragment(FertilizationFragment.newInstance())
        }

        btnFumigation.setOnClickListener {
            changeFragment(FumigationFragment.newInstance())
        }

        btnFarm.setOnClickListener {
            changeFragment(FarmFragment.newInstance())
        }

    }

    private fun showCustomPopupWindow(view: View) {
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.menu_perfil, null)

        val popupWindow = PopupWindow(
            popupView,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        ).apply {
            isFocusable = true
            setBackgroundDrawable(ContextCompat.getDrawable(this@MainActivity, android.R.color.transparent))
        }

        popupView.findViewById<TextView>(R.id.action_perfil).setOnClickListener {
            changeFragment(PerfilFragment.newInstance())
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.action_cerrar_sesion).setOnClickListener {
            //changeFragment(LoginFragment.newInstance())
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(view, 0, 0, Gravity.START)
    }

    private fun changeFragment(fragment:  Fragment?){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment!!)
        fragmentTransaction.commit()
    }
}