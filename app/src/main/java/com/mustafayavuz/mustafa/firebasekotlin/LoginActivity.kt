package com.mustafayavuz.mustafa.firebasekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initMyAuthStateListener()

        tvRgstrLgn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        tvOnayMailiniTkrGndr.setOnClickListener {
            var dialogGoster = OnayMailTekrarFragment()
            dialogGoster.show(supportFragmentManager,"gosterDialog")
        }

        tvSifremiUnuttum.setOnClickListener {
            var dialogSifremiUnuttum=ForgetPasswordFragment()
            dialogSifremiUnuttum.show(supportFragmentManager,"gosterDialogFP")
        }

        btnGirisLgn.setOnClickListener {

            if (etMailLgn.text.isNotEmpty() && etPassLgn.text.isNotEmpty()) {
                progressBarGoster()
                FirebaseAuth.getInstance().signInWithEmailAndPassword(etMailLgn.text.toString(), etPassLgn.text.toString())
                        .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                            override fun onComplete(p0: Task<AuthResult>) {
                                if (p0.isSuccessful) {
                                    progressBarGizle()
                                    if (!p0.result.user.isEmailVerified){
                                        FirebaseAuth.getInstance().signOut()
                                    }
                                } else {
                                    progressBarGizle()
                                    Toast.makeText(this@LoginActivity, "Giriş yapılamadı!" + p0.exception?.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
            } else {
                Toast.makeText(this@LoginActivity, "Boş alanları doldurunuz!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun progressBarGoster() {
        pbLgn.visibility = View.VISIBLE
    }

    private fun progressBarGizle() {
        pbLgn.visibility = View.INVISIBLE
    }

    private fun initMyAuthStateListener() {
        mAuthStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var kullanici = p0.currentUser
                if (kullanici != null) {
                    if (kullanici.isEmailVerified) {
                        Toast.makeText(this@LoginActivity, "Mail onaylanmış, giriş yapılabilir!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Mail Adresinizi onayladıktan sonra tekrar giriş yapınız!", Toast.LENGTH_SHORT).show()
                        FirebaseAuth.getInstance().signOut()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener)
    }
}
