package com.mustafayavuz.mustafa.firebasekotlin


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth


class OnayMailTekrarFragment : DialogFragment() {

    lateinit var email: EditText
    lateinit var sifre: EditText
    lateinit var mContext: FragmentActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater!!.inflate(R.layout.fragment_dialog, container, false)

        var btnIptal = view.findViewById<Button>(R.id.btnDialogIptal)
        var btnGonder = view.findViewById<Button>(R.id.btnDialogGonder)
        mContext = activity!!

        email = view.findViewById(R.id.etDialogMail)
        sifre = view.findViewById(R.id.etDialogSifre)

        btnIptal.setOnClickListener {
            dialog.dismiss()
        }

        btnGonder.setOnClickListener {
            if (email.text.toString().isNotEmpty() && sifre.text.toString().isNotEmpty()) {
                girisYapOnayMailGonder(email.text.toString(), sifre.text.toString())
            } else {
                Toast.makeText(mContext, "Boş alanları doldurunuz!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun girisYapOnayMailGonder(email: String, sifre: String) {
        var credential = EmailAuthProvider.getCredential(email, sifre)
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onayMailTekrarGonder()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(mContext, "Email veya şifre hatalı!", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun onayMailTekrarGonder() {
        var kullanici = FirebaseAuth.getInstance().currentUser
        if (kullanici != null) {
            kullanici.sendEmailVerification()
                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                        override fun onComplete(p0: Task<Void>) {
                            if (p0.isSuccessful) {
                                Toast.makeText(mContext, "Onay maili gönderildi, Mail kutunuzu kontrol ediniz!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(mContext, "Mail Gönderilemedi!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
        }
    }


}
