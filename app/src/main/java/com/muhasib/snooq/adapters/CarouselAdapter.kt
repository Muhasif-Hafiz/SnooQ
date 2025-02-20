package com.muhasib.snooq.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.animation.AnimationUtils.lerp
import com.muhasib.snooq.databinding.CaraoselLayoutBinding
import com.muhasib.snooq.model.CarouselModel

class CarouselAdapter(val list : ArrayList<CarouselModel>) : RecyclerView.Adapter<CarouselAdapter.ItemViewHolder> (){


    inner  class ItemViewHolder( val binding : CaraoselLayoutBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind (model : CarouselModel){
            binding.apply {
                carouselImageView.setImageResource(model.imageId)
                carouselTextView.text = model.title

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