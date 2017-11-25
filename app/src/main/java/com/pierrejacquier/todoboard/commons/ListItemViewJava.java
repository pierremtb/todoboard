package com.pierrejacquier.todoboard.commons;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.util.AttributeSet;
import android.view.View;

import com.lucasurbas.listitemview.ListItemView;

public class ListItemViewJava extends ListItemView {
    public ListItemViewJava(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @BindingAdapter("title")
    public static void bindTitle(ListItemViewJava view, String title) {
        if (view != null) {
            view.setTitle(title);
        }
    }

    @BindingAdapter("subtitle")
    public static void bindSubtitle(ListItemViewJava view, String subtitle) {
        if (view != null) {
            view.setSubtitle(subtitle);
        }
    }

    @BindingAdapter("circularIconColor")
    public static void bindCircularIconColor(ListItemViewJava view, int color) {
        if (view != null) {
            view.setCircularIconColor(color);
        }
    }
}
