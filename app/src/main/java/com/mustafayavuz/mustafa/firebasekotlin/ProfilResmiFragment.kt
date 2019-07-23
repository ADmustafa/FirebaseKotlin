package com.mustafayavuz.mustafa.firebasekotlin


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class ProfilResmiFragment : DialogFragment() {

    lateinit var tvCamera: TextView
    lateinit var tvGaleri: TextView

    interface onProfilResmiListener {
        fun getResimYolu(resimPath: Uri?)
        fun getResimBitmap(bitmap: Bitmap)
    }

    lateinit var mProfilResmiListener: onProfilResmiListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profil_resmi, container, false)

        tvCamera = view.findViewById(R.id.tvCameraFragment)
        tvGaleri = view.findViewById(R.id.tvGaleriFragment)

        tvCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 2)
        }

        tvGaleri.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            var galeridenSecilenResimYolu = data.data
            mProfilResmiListener.getResimYolu(galeridenSecilenResimYolu)
            dialog.dismiss()
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            var bitmap: Bitmap
            bitmap = data.extras.get("data") as Bitmap
            mProfilResmiListener.getResimBitmap(bitmap)
            dialog.dismiss()
        } else {

        }


        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onAttach(context: Context?) {

        mProfilResmiListener=activity as onProfilResmiListener

        super.onAttach(context)
    }


}
