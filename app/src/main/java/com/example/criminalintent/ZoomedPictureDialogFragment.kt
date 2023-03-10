package com.example.criminalintent

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment


class ZoomedPictureDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewPhoto = inflater.inflate(R.layout.zoom_layout, container, false)
        val imageView = viewPhoto.findViewById(R.id.zoom_image_view) as ImageView

        val imageFileName = arguments?.getSerializable(PICTURE_DIALOG) as String

        imageView.setImageBitmap(BitmapFactory.decodeFile(
            requireActivity().applicationContext.filesDir.path + "/" + imageFileName))

        return viewPhoto
    }

    companion object {
        const val PICTURE_DIALOG = "PICTURE_DIALOG"

        fun newInstance(photoFileName: String?): ZoomedPictureDialogFragment {
            val args = Bundle()

            args.putSerializable(PICTURE_DIALOG, photoFileName)

            return ZoomedPictureDialogFragment().apply { arguments = args }
        }
    }


}
