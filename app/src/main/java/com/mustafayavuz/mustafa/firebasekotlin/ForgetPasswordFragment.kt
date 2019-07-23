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
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordFragment : DialogFragment() {

    lateinit var email:EditText
    lateinit var mContext:FragmentActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view= inflater!!.inflate(R.layout.fragment_forget_password, container, false)

        mContext=activity!!

        var btnIptal=view.findViewById<Button>(R.id.btnIptalFrgtPsswrd)
        var btnGonder=view.findViewById<Button>(R.id.btnGonderFrgtPsswrd)

        email=view.findViewById(R.id.etEmailFrgtPsswrd)

        btnIptal.setOnClickListener {
            dialog.dismiss()
        }

        btnGonder.setOnClickListener {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email.text.toString())
                    .addOnCompleteListener{task ->
                        if (task.isSuccessful){
                            Toast.makeText(mContext,"Şifre sıfırlama maili gönderildi!",Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }else{
                            Toast.makeText(mContext,"Mail gönderilemedi: "+task.exception?.message,Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
        }

        return view
    }


}
