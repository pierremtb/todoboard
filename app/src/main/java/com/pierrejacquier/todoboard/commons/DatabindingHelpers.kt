package com.pierrejacquier.todoboard.commons

import android.R.attr.data
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.ViewGroup
import android.databinding.BindingAdapter
import com.android.databinding.library.baseAdapters.BR


@BindingAdapter("entries", "layout")
fun <T> setEntries(viewGroup: ViewGroup,
                   entries: List<T>?, layoutId: Int) {
    viewGroup.removeAllViews()
    if (entries != null) {
        val inflater = viewGroup.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        for (i in entries.indices) {
            val entry = entries[i]
            val binding = DataBindingUtil
                    .inflate<ViewDataBinding>(inflater, layoutId, viewGroup, true)
            binding.setVariable(BR.data, entry)
        }
    }
}