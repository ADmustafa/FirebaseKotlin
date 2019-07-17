package com.mustafayavuz.mustafa.firebasekotlin

import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnKayit.setOnClickListener {
            if (etEmail.text.isNotEmpty() && etPass.text.isNotEmpty() && etPassTekrar.text.isNotEmpty()) {
                if (etPass.text.toString().equals(etPassTekrar.text.toString())) {
                    yeniUyeKayit(etEmail.text.toString(), etPass.text.toString())
                } else {
                    Toast.makeText(this, "Şifreler aynı değil!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Boş alanları doldurunuz!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun yeniUyeKayit(mail: String, sifre: String) {
        progressBarGoster()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail, sifre)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                    override fun onComplete(p0: Task<AuthResult>) {
                        if (p0.isSuccessful) {
                            progressBarGizle()
                            Toast.makeText(this@RegisterActivity, "Kayıt başarı ile oluşturuldu: " + FirebaseAuth.getInstance().currentUser?.email, Toast.LENGTH_SHORT).show()
                            FirebaseAuth.getInstance().signOut()
                        } else {
                            progressBarGizle()
                            Toast.makeText(this@RegisterActivity, "Kayıt oluşturulurken hata oluştu: " + p0.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
    }

    private fun progressBarGoster() {
        pbRegister.visibility = View.VISIBLE
    }

    private fun progressBarGizle() {
        pbRegister.visibility = View.INVISIBLE
    }
}