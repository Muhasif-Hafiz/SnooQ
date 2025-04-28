package com.muhasib.snooq.view.ShopProfile.ShopFragment

import android.annotation.SuppressLint
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
import com.muhasib.snooq.mvvm.ViewModel.shopViewModel


class ViewBannerImageFragment : Fragment() {

    private val viewModel: shopViewModel by viewModels()
    private var imageUrl: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_view_banner_image, container, false)
        val photoView: PhotoView = view.findViewById(R.id.photoViewBanner)
        val closeButton: ImageView = view.findViewById(R.id.closeButton_Banner)
        val imageUrl = viewModel.getBannerImageUrlFromPrefs()

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(requireContext()).load(imageUrl).into(photoView)
        }

        closeButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }


        photoView.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        return  view
    }

}