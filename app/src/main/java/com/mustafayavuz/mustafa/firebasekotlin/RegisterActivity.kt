package com.mustafayavuz.mustafa.firebasekotlin

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    var izinlerVerildi =false

    var galeridenGelenURI: Uri? = null
    var cameraBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnKayit.setOnClickListener {

            if (etEmail.text.isNotEmpty() && etPass.text.isNotEmpty() && etPassTekrar.text.isNotEmpty()) {
                if (etPass.text.toString().equals(etPassTekrar.text.toString())) {
                    progressBarGoster()
                    yeniUyeKayit(etEmail.text.toString(), etPass.text.toString())
                } else {
                    Toast.makeText(this@RegisterActivity, "Şifreler aynı değil!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@RegisterActivity, "Boş alanları doldurunuz!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun yeniUyeKayit(mail: String, sifre: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail, sifre)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                    override fun onComplete(p0: Task<AuthResult>) {
                        if (p0.isSuccessful) {
                            progressBarGizle()
                            onayMailiGonder()

                            var veritabaninaEklenecekKullanici=Kullanici()
                            veritabaninaEklenecekKullanici.isim=etEmail.text.toString().substring(0,etEmail.text.toString().indexOf("@"))
                            veritabaninaEklenecekKullanici.kullanici_id=FirebaseAuth.getInstance().currentUser?.uid
                            veritabaninaEklenecekKullanici.profil_resmi=""
                            veritabaninaEklenecekKullanici.seviye="1"
                            veritabaninaEklenecekKullanici.telefon="05458726091"

                            FirebaseDatabase.getInstance().reference
                                    .child("kullanici")
                                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                    .setValue(veritabaninaEklenecekKullanici).addOnCompleteListener { task ->
                                        if (task.isSuccessful){
                                            Toast.makeText(this@RegisterActivity, "Veritabanına kayıt başarı ile oluşturuldu: " + FirebaseAuth.getInstance().currentUser?.email, Toast.LENGTH_SHORT).show()
                                            FirebaseAuth.getInstance().signOut()
                                            loginGonder()
                                        }else{
                                            Toast.makeText(this@RegisterActivity, "Veritabanına kayıt oluşturulurken hata oluştu: " + task.exception?.message, Toast.LENGTH_SHORT).show()
                                        }
                                    }

                            FirebaseAuth.getInstance().signOut()
                        } else {
                            progressBarGizle()
                            Toast.makeText(this@RegisterActivity, "Kayıt oluşturulurken hata oluştu: " + p0.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
    }

    private fun onayMailiGonder() {
        var kullanici=FirebaseAuth.getInstance().currentUser
        if (kullanici!=null){
            kullanici.sendEmailVerification()
                    .addOnCompleteListener(object :OnCompleteListener<Void>{
                        override fun onComplete(p0: Task<Void>) {
                            if (p0.isSuccessful){
                                Toast.makeText(this@RegisterActivity,"Onay maili gönderildi, Mail kutunuzu kontrol ediniz!",Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this@RegisterActivity,"Mail Gönderilemedi!",Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
        }
    }

    private fun progressBarGoster() {
        pbRegister.visibility = View.VISIBLE
    }

    private fun progressBarGizle() {
        pbRegister.visibility = View.INVISIBLE
    }

    private fun loginGonder(){
        val intent=Intent(this@RegisterActivity,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}