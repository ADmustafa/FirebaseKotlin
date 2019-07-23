package com.mustafayavuz.mustafa.firebasekotlin

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_kullanici_ayarlari.*
import java.io.ByteArrayOutputStream
import java.lang.Exception

class KullaniciAyarlariActivity : AppCompatActivity(), ProfilResmiFragment.onProfilResmiListener {

    var izinlerVerildi = false
    var galeridenGelenURI: Uri? = null
    var cameraBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kullanici_ayarlari)

        getKullaniciDetay()

        var kullanici = FirebaseAuth.getInstance().currentUser!!

        btnGuncelleDetay.setOnClickListener {
            if (etUsernameDetay.text.toString().isNotEmpty() && etMailDetay.text.toString().isNotEmpty()) {
                if (!etUsernameDetay.text.toString().equals(kullanici.displayName.toString())) {
                    var bilgileriGuncelle = UserProfileChangeRequest.Builder()
                            .setDisplayName(etUsernameDetay.text.toString())
                            .build()
                    kullanici.updateProfile(bilgileriGuncelle)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this@KullaniciAyarlariActivity, "Bilgiler güncellendi!", Toast.LENGTH_SHORT).show()
                                }
                            }
                }
            } else {
                Toast.makeText(this@KullaniciAyarlariActivity, "Boş alanları doldurunuz!", Toast.LENGTH_SHORT).show()
            }

            if (galeridenGelenURI != null) {
                fotoGaleriCompressed(galeridenGelenURI!!)
            } else if (cameraBitmap != null) {
                fotoCameraCompressed(cameraBitmap!!)
            }

        }

        ivProfil.setOnClickListener {
            if (izinlerVerildi) {
                var dialog = ProfilResmiFragment()
                dialog.show(supportFragmentManager, "fotosec")
            } else {
                izinlerIste()
            }
        }
    }

    inner class BackgroundResimCompress : AsyncTask<Uri, Double, ByteArray?> {

        var myBitmap: Bitmap? = null

        constructor() {}

        constructor(bm: Bitmap) {
            if (bm != null) {
                myBitmap = bm
            }
        }

        //doInBackground yapılmadan önce yapılan method
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Uri?): ByteArray? {
            //Galeriden resim seçilmiş demektir.
            if (myBitmap == null) {
                myBitmap = MediaStore.Images.Media.getBitmap(this@KullaniciAyarlariActivity.contentResolver, params[0])
                Log.e("TESTFIREBASE", "Orjinal resmin boyutu: " + (myBitmap!!.byteCount).toDouble() / 1000000.toDouble())
            }

            var resimBytes: ByteArray? = null

            for (i in 1..10) {
                resimBytes = convertBitmapToByte(myBitmap, 100 / i)
                publishProgress(resimBytes!!.size.toDouble())
            }

            return resimBytes
        }

        override fun onProgressUpdate(vararg values: Double?) {
            super.onProgressUpdate(*values)
        }

        //doInBackground yapıldıktan sonra yapılan method
        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)
            uploadResimToFirebase(result)
        }

    }

    private fun uploadResimToFirebase(result: ByteArray?) {
        progressBarGoster()
        var storageReferans = FirebaseStorage.getInstance().getReference()
        var resimEklenecekYer = storageReferans.child("images/kullanici/" + FirebaseAuth.getInstance().currentUser?.uid + "/profile_image")

        var upload=resimEklenecekYer.putBytes(result!!)
        upload.addOnSuccessListener(object :OnSuccessListener<UploadTask.TaskSnapshot>{

            override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                var firebaseURL=p0?.uploadSessionUri

                FirebaseDatabase.getInstance().reference
                        .child("kullanici")
                        .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                        .child("profil_resmi")
                        .setValue(firebaseURL.toString())
                Toast.makeText(this@KullaniciAyarlariActivity,"Resmin Yolu: "+firebaseURL.toString(), Toast.LENGTH_SHORT).show()
                progressBarGizle()
            }
        }).addOnFailureListener(object :OnFailureListener{
            override fun onFailure(p0: Exception) {
                Toast.makeText(this@KullaniciAyarlariActivity,"Resim yüklenirken hata oluştu: "+p0.message,Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun convertBitmapToByte(myBitmap: Bitmap?, i: Int): ByteArray? {
        var stream = ByteArrayOutputStream()
        myBitmap?.compress(Bitmap.CompressFormat.JPEG, i, stream)
        return stream.toByteArray()
    }

    private fun fotoCameraCompressed(cameraBitmap: Bitmap) {
        var compressed = BackgroundResimCompress(cameraBitmap)
        var uri: Uri? = null
        compressed.execute(uri)
    }

    private fun fotoGaleriCompressed(galeridenGelenURI: Uri) {
        var compressed = BackgroundResimCompress()
        compressed.execute(galeridenGelenURI)

    }

    private fun izinlerIste() {
        var izinler = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)
        if (ContextCompat.checkSelfPermission(this, izinler[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, izinler[1]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, izinler[2]) == PackageManager.PERMISSION_GRANTED) {
            izinlerVerildi = true
        } else {
            ActivityCompat.requestPermissions(this, izinler, 3)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                var dialog = ProfilResmiFragment()
                dialog.show(supportFragmentManager, "fotosec")
            } else {
                Toast.makeText(this@KullaniciAyarlariActivity, "Tüm izinleri vermelisiniz!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getKullaniciDetay() {

        var kullanici = FirebaseAuth.getInstance().currentUser!!
        var referans = FirebaseDatabase.getInstance().reference!!

        etMailDetay.setText(kullanici.email.toString())

        var query = referans.child("kullanici")
                .orderByKey()
                .equalTo(kullanici.uid)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (singleSnapshot in p0.children) {
                    var okunanKullanici = singleSnapshot.getValue(Kullanici::class.java)
                    etUsernameDetay.setText(okunanKullanici?.isim.toString())
                    etTelefonDetay.setText(okunanKullanici?.telefon.toString())
                    Picasso.with(this@KullaniciAyarlariActivity).load(okunanKullanici?.profil_resmi).resize(100,100).into(ivProfil)
                }
            }

        })

    }

    override fun getResimYolu(resimPath: Uri?) {
        galeridenGelenURI = resimPath
        Picasso.with(this).load(galeridenGelenURI).resize(100, 100).into(ivProfil)
    }

    override fun getResimBitmap(bitmap: Bitmap) {
        cameraBitmap = bitmap
        ivProfil.setImageBitmap(cameraBitmap)
        //Picasso.with(this).load()
    }

    fun progressBarGoster(){
        pbPicture.visibility=View.VISIBLE
    }

    fun progressBarGizle(){
        pbPicture.visibility=View.INVISIBLE
    }
}
