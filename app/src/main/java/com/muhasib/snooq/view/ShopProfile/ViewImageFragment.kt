package com.muhasib.snooq.view.ShopProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.muhasib.snooq.R
import com.muhasib.snooq.viewModel.shopViewModel

class ViewImageFragment : Fragment() {

    private val viewModel: shopViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_image, container, false)
        val photoView: PhotoView = view.findViewById(R.id.photoView)
        val closeButton: ImageView = view.findViewById(R.id.closeButton)

        // Retrieve the image URL from ViewModel
        val imageUrl = viewModel.getProfileImageUrlFromPrefs()


        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(requireContext()).load(imageUrl).into(photoView)
        }


        closeButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }


        photoView.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }
}
