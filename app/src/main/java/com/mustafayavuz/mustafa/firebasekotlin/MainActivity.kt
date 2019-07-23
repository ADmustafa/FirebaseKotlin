package com.mustafayavuz.mustafa.firebasekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAuthStateListener()
    }

    private fun setKullanicilariListeler() {
        var kullanici = FirebaseAuth.getInstance().currentUser
        if (kullanici != null) {
            tvUsernameHome.text = if (kullanici.displayName.isNullOrEmpty()) "Tanımlanmadı" else kullanici.displayName
            tvEmailHome.text = kullanici.email
            tvUserIDHome.text = kullanici.uid
        }
    }

    private fun initAuthStateListener() {
        mAuthStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var kullanici = p0.currentUser
                if (kullanici == null) {
                    var intent = Intent(this@MainActivity, LoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.anamenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menuHesapAyarlari -> {
                val intent = Intent(this@MainActivity, KullaniciAyarlariActivity::class.java)
                startActivity(intent)
            }
            R.id.menuCikisYap -> {
                cikisYap()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun cikisYap() {
        FirebaseAuth.getInstance().signOut()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener)
    }

    override fun onResume() {
        super.onResume()
        kullaniciyiKontrolEt()
        setKullanicilariListeler()
    }

    private fun kullaniciyiKontrolEt() {
        var kullanici = FirebaseAuth.getInstance().currentUser
        if (kullanici == null) {
            var intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener)
        }
    }
}
