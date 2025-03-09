package com.muhasib.snooq.view.ShopProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.muhasib.snooq.R
import com.muhasib.snooq.databinding.FragmentFloatingDialogBinding


class FloatingDialogFragment(val clickChat: (Int) -> Unit, val clickAudio: (Int) -> Unit, val clickVideo: (Int) -> Unit) : DialogFragment() {

    private lateinit var binding : FragmentFloatingDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFloatingDialogBinding.inflate(inflater, container, false)


        binding.btnClose.setOnClickListener {
            dismiss()
        }
        binding.btnChat.setOnClickListener {
            clickChat(1)
            dismiss()
        }
        binding.btnAudioCall.setOnClickListener {
            clickAudio(2)
            dismiss()
        }
        binding.btnVideoCall.setOnClickListener {
            clickVideo(3)
            dismiss()
        }


        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentFullScreenDialog)
    }

    override fun onStart(){
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}