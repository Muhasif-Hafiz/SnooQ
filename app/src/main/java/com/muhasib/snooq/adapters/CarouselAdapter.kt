package com.muhasib.snooq.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.animation.AnimationUtils.lerp
import com.muhasib.snooq.R
import com.muhasib.snooq.databinding.CaraoselLayoutBinding
import com.muhasib.snooq.model.CarouselModel

class CarouselAdapter(val list : ArrayList<CarouselModel>) : RecyclerView.Adapter<CarouselAdapter.ItemViewHolder> (){


    inner  class ItemViewHolder( val binding : CaraoselLayoutBinding) : RecyclerView.ViewHolder(binding.root){

        @SuppressLint("RestrictedApi")
        fun bind (model : CarouselModel){
            binding.apply {
              //  carouselImageView.setImageResource(model.imageId)

                val url = model.imageUrl
                Log.d("ShopImages ", "$url")
                Glide.with(binding.root.context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.img_1)
                    .error(R.drawable.ic_error)
                    .into(carouselImageView)


               carouselItemContainer.setOnMaskChangedListener {
                        maskRect ->

                    carouselTextView.setTranslationX(maskRect.left)
                    carouselTextView.alpha =lerp(1F, 0F, 0F, 80F, maskRect.left)
                }



            }
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CarouselAdapter.ItemViewHolder {
        return ItemViewHolder(CaraoselLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: CarouselAdapter.ItemViewHolder, position: Int) {

        val model = list[position]
        holder.bind(model)

    }

    override fun getItemCount() = list.size


}